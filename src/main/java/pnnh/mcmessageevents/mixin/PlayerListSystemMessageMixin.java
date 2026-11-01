package pnnh.mcmessageevents.mixin;

import java.util.function.Function;

import pnnh.mcmessageevents.observe.ChatObservation;
import pnnh.mcmessageevents.observe.MessagePhase;
import pnnh.mcmessageevents.observe.ObservationDispatcher;
import pnnh.mcmessageevents.observe.ObservationSide;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

/**
 * Observes server system broadcasts. Does not alter arguments or cancel.
 */
@Mixin(PlayerList.class)
public abstract class PlayerListSystemMessageMixin {
    @Inject(
            method = "broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V",
            at = @At("HEAD"))
    private void mcmessageevents$observeSystem(Component message, boolean bypassHiddenChat, CallbackInfo ci) {
        emit(message);
    }

    @Inject(
            method = "broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Ljava/util/function/Function;Z)V",
            at = @At("HEAD"))
    private void mcmessageevents$observeSystemFactory(
            Component serverMessage,
            Function<ServerPlayer, Component> playerMessageFactory,
            boolean bypassHiddenChat,
            CallbackInfo ci) {
        emit(serverMessage);
    }

    private static void emit(Component message) {
        String plain = message == null ? "" : message.getString();
        ObservationDispatcher.emitChat(new ChatObservation(
                plain,
                ObservationSide.SERVER,
                MessagePhase.SYSTEM,
                null,
                null,
                false,
                System.currentTimeMillis()));
    }
}
