package pnnh.mcmessageevents.log;

import pnnh.mcmessageevents.observe.LogObservation;
import pnnh.mcmessageevents.observe.ObservationSide;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Log4j2 appender that forwards qualifying lines into observation events.
 */
public final class MessageLogAppender extends AbstractAppender {
    public static final String NAME = "MCMessageEventsLog";
    private static final String SELF_LOGGER_PREFIX = "pnnh.mcmessageevents";

    private static final ThreadLocal<Boolean> RECURSION = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private volatile Level minimumLevel = Level.INFO;

    public MessageLogAppender() {
        super(NAME, null, PatternLayout.newBuilder().withPattern("%m").build(), false, Property.EMPTY_ARRAY);
    }

    public void setMinimumLevel(Level minimumLevel) {
        this.minimumLevel = minimumLevel == null ? Level.INFO : minimumLevel;
    }

    @Override
    public void append(LogEvent event) {
        if (Boolean.TRUE.equals(RECURSION.get()) || event == null) {
            return;
        }
        Level level = event.getLevel();
        Level min = this.minimumLevel;
        if (level == null || min == null || !level.isMoreSpecificThan(min)) {
            return;
        }
        String loggerName = event.getLoggerName();
        if (loggerName != null && loggerName.startsWith(SELF_LOGGER_PREFIX)) {
            return;
        }

        String plain;
        try {
            plain = getLayout().toSerializable(event).toString();
        } catch (RuntimeException ex) {
            plain = event.getMessage() == null ? "" : event.getMessage().getFormattedMessage();
        }
        ObservationSide side = FMLEnvironment.dist == Dist.CLIENT
                ? ObservationSide.CLIENT
                : ObservationSide.SERVER;
        LogObservation observation = new LogObservation(
                plain == null ? "" : plain,
                side,
                loggerName == null ? "" : loggerName,
                LogLevels.fromLog4j(level),
                System.currentTimeMillis());

        RECURSION.set(Boolean.TRUE);
        try {
            LogBridge.dispatchObservation(observation);
        } finally {
            RECURSION.set(Boolean.FALSE);
        }
    }
}
