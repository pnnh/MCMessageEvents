package pnnh.mcmessageevents.observe;

/**
 * Capture phase for chat observations. SYSTEM is server broadcast only;
 * client receives system HUD lines as {@link #DELIVERED}.
 */
public enum MessagePhase {
    PLAYER_SEND,
    DELIVERED,
    SYSTEM
}
