package net.aosankaku.liteminerdelta.networking;

import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import com.iamkaf.amber.api.networking.v1.PacketEncoder;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.aosankaku.liteminerdelta.Liteminer;
import net.aosankaku.liteminerdelta.LiteminerClient;

public record S2CHungerRequirement(boolean hungerRequired) implements Packet<S2CHungerRequirement> {
    public static final PacketEncoder<S2CHungerRequirement> ENCODER =
            (packet, buffer) -> buffer.writeBoolean(packet.hungerRequired);

    public static final PacketDecoder<S2CHungerRequirement> DECODER =
            buffer -> new S2CHungerRequirement(buffer.readBoolean());

    public static final PacketHandler<S2CHungerRequirement> HANDLER = (packet, context) -> {
        if (context.isServerSide()) {
            Liteminer.LOGGER.warn("Received S2CHungerRequirement on server side - this should not happen");
            return;
        }

        context.execute(() -> LiteminerClient.setHungerRequired(packet.hungerRequired));
    };
}
