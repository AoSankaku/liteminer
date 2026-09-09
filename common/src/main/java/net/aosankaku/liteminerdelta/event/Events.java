package net.aosankaku.liteminerdelta.event;

import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import net.aosankaku.liteminerdelta.Liteminer;

public class Events {
    public static void init() {
        OnBlockBreak.init();
        OnBlockInteraction.init();
        PlayerEvents.PLAYER_LEAVE.register(player -> Liteminer.instance.onPlayerLeave(player));
    }
}
