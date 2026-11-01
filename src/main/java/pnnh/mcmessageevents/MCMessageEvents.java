package pnnh.mcmessageevents;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import pnnh.mcmessageevents.config.CommonConfig;
import pnnh.mcmessageevents.log.LogBridge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

/**
 * Dual-side message/chat event bridge. Client-only and dedicated-server-only
 * installs are both supported; neither side requires the other.
 * KubeJS types must not be referenced from this class.
 */
@Mod(MCMessageEvents.MOD_ID)
public class MCMessageEvents {
    public static final String MOD_ID = "mcmessageevents";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MCMessageEvents(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modEventBus.addListener(this::onConfig);
        LOGGER.info("MCMessageEvents loading");
    }

    private void onConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() == CommonConfig.SPEC) {
            LogBridge.syncFromConfig();
        }
    }
}
