package pnnh.mcmessageevents.log;

import pnnh.mcmessageevents.MCMessageEvents;
import pnnh.mcmessageevents.config.CommonConfig;
import pnnh.mcmessageevents.observe.LogObservation;
import pnnh.mcmessageevents.observe.ObservationDispatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * Installs or removes the observation appender from the Log4j root logger
 * according to COMMON config. Dispatches onto the game thread.
 * Avoids hard references to client-only classes so dedicated servers can load.
 */
public final class LogBridge {
    private static MessageLogAppender appender;

    private LogBridge() {
    }

    public static synchronized void syncFromConfig() {
        if (CommonConfig.loggerObservationEnabled()) {
            install(LogLevels.toLog4j(CommonConfig.loggerMinimumLevel()));
        } else {
            remove();
        }
    }

    private static void install(org.apache.logging.log4j.Level minimum) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();
        if (appender == null) {
            appender = new MessageLogAppender();
            appender.start();
            configuration.addAppender(appender);
        }
        appender.setMinimumLevel(minimum);
        LoggerConfig root = configuration.getRootLogger();
        if (root.getAppenders().get(MessageLogAppender.NAME) == null) {
            root.addAppender(appender, null, null);
        }
        context.updateLoggers();
        MCMessageEvents.LOGGER.info("Logger observation enabled (min={})", minimum);
    }

    private static void remove() {
        if (appender == null) {
            return;
        }
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();
        LoggerConfig root = configuration.getRootLogger();
        root.removeAppender(MessageLogAppender.NAME);
        configuration.getAppenders().remove(MessageLogAppender.NAME);
        appender.stop();
        appender = null;
        context.updateLoggers();
        MCMessageEvents.LOGGER.info("Logger observation disabled");
    }

    static void dispatchObservation(LogObservation observation) {
        runOnGameThread(() -> ObservationDispatcher.emitLog(observation));
    }

    private static void runOnGameThread(Runnable task) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.execute(task);
            return;
        }
        if (FMLEnvironment.dist == Dist.CLIENT && runOnClient(task)) {
            return;
        }
        task.run();
    }

    private static boolean runOnClient(Runnable task) {
        try {
            Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
            Object minecraft = minecraftClass.getMethod("getInstance").invoke(null);
            if (minecraft == null) {
                return false;
            }
            minecraftClass.getMethod("execute", Runnable.class).invoke(minecraft, task);
            return true;
        } catch (ReflectiveOperationException ex) {
            MCMessageEvents.LOGGER.debug("Could not hop log observation to client thread", ex);
            return false;
        }
    }
}
