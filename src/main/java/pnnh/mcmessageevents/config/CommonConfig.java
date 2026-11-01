package pnnh.mcmessageevents.config;

import pnnh.mcmessageevents.observe.LogSeverity;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Dual-side COMMON settings. Logger observation defaults to off.
 */
public final class CommonConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue LOGGER_OBSERVATION_ENABLED;
    public static final ModConfigSpec.ConfigValue<String> LOGGER_MINIMUM_LEVEL;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("logger_observation");
        LOGGER_OBSERVATION_ENABLED = builder
                .comment("When true, qualifying Log4j lines are emitted as log observation events.")
                .define("enabled", false);
        LOGGER_MINIMUM_LEVEL = builder
                .comment("Minimum Log4j severity when logger observation is enabled: TRACE, DEBUG, INFO, WARN, ERROR, FATAL.")
                .define("minimumLevel", "INFO");
        builder.pop();
        SPEC = builder.build();
    }

    private CommonConfig() {
    }

    public static boolean loggerObservationEnabled() {
        try {
            return LOGGER_OBSERVATION_ENABLED.get();
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public static LogSeverity loggerMinimumLevel() {
        try {
            return LogSeverity.parseConfig(LOGGER_MINIMUM_LEVEL.get());
        } catch (RuntimeException ignored) {
            return LogSeverity.INFO;
        }
    }
}
