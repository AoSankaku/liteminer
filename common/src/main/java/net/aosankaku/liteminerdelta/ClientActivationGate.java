package net.aosankaku.liteminerdelta;

/**
 * Prevents client-only activation on servers that do not support Liteminer Delta.
 */
final class ClientActivationGate {
    private boolean unavailableNotificationShown;

    Result evaluate(boolean requestedByKeybind, boolean serverSupported) {
        if (serverSupported) {
            unavailableNotificationShown = false;
            return Result.ALLOW;
        }

        if (requestedByKeybind && !unavailableNotificationShown) {
            unavailableNotificationShown = true;
            return Result.BLOCK_AND_NOTIFY;
        }

        return Result.BLOCK;
    }

    void reset() {
        unavailableNotificationShown = false;
    }

    enum Result {
        ALLOW,
        BLOCK,
        BLOCK_AND_NOTIFY
    }
}
