package net.aosankaku.liteminerdelta.networking;

import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import com.iamkaf.amber.api.networking.v1.PacketEncoder;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.aosankaku.liteminerdelta.Liteminer;
import net.aosankaku.liteminerdelta.event.FoodExhaustion;
import net.minecraft.server.level.ServerPlayer;

public record C2SVeinmineKeybindChange(
        boolean keybindState,
        int shape,
        boolean distinguishDeepslateOres,
        boolean distinguishStoneVariants
) implements Packet<C2SVeinmineKeybindChange> {
    public static final PacketEncoder<C2SVeinmineKeybindChange> ENCODER = (packet, buffer) -> {
        buffer.writeBoolean(packet.keybindState);
        buffer.writeInt(packet.shape);
        buffer.writeBoolean(packet.distinguishDeepslateOres);
        buffer.writeBoolean(packet.distinguishStoneVariants);
    };

    public static final PacketDecoder<C2SVeinmineKeybindChange> DECODER = buffer -> {
        boolean keybindState = buffer.readBoolean();
        int shape = buffer.readInt();
        boolean distinguishDeepslateOres = buffer.readBoolean();
        boolean distinguishStoneVariants = buffer.readBoolean();
        return new C2SVeinmineKeybindChange(
                keybindState,
                shape,
                distinguishDeepslateOres,
                distinguishStoneVariants
        );
    };

    public static final PacketHandler<C2SVeinmineKeybindChange> HANDLER = (packet, context) -> {
        if (context.isServerSide()) {
            context.execute(() -> {
                Liteminer.LOGGER.debug(
                        "Received C2SVeinmineKeybindChange: keybindState={}, shape={}, player={}",
                        packet.keybindState,
                        packet.shape,
                        context.getPlayer().getName().getString()
                );

                Liteminer.instance.onKeymappingStateChange(
                        (ServerPlayer) context.getPlayer(),
                        packet.keybindState,
                        packet.shape,
                        packet.distinguishDeepslateOres,
                        packet.distinguishStoneVariants
                );
                LiteminerNetwork.sendToPlayer(
                        new S2CHungerRequirement(FoodExhaustion.isHungerRequired()),
                        (ServerPlayer) context.getPlayer()
                );
            });
        } else {
            Liteminer.LOGGER.warn("Received C2SVeinmineKeybindChange on client side - this should not happen");
        }
    };
}
