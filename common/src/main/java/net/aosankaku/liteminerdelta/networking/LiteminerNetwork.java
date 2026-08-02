package net.aosankaku.liteminerdelta.networking;

import net.aosankaku.liteminerdelta.Liteminer;
import net.aosankaku.liteminerdelta.LiteminerClient;
import net.aosankaku.liteminerdelta.event.FoodExhaustion;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class LiteminerNetwork {
    public static final SimpleNetworkManager NET = SimpleNetworkManager.create(Liteminer.MOD_ID);
    public static MessageType VEINMINE_KEYBIND_CHANGE =
            NET.registerC2S("veinmine_keybind_change", Messages.C2SVeinmineKeybindChange::new);
    public static MessageType HUNGER_REQUIREMENT =
            NET.registerS2C("hunger_requirement", Messages.S2CHungerRequirement::new);

    public static void init() {
    }

    public static class Messages {
        public static class C2SVeinmineKeybindChange extends BaseC2SMessage {
            private final boolean keybindState;
            private final int shape;
            private final boolean distinguishDeepslateOres;
            private final boolean distinguishStoneVariants;

            public C2SVeinmineKeybindChange(boolean keybindState, int shape,
                    boolean distinguishDeepslateOres, boolean distinguishStoneVariants) {
                this.keybindState = keybindState;
                this.shape = shape;
                this.distinguishDeepslateOres = distinguishDeepslateOres;
                this.distinguishStoneVariants = distinguishStoneVariants;
            }

            public C2SVeinmineKeybindChange(FriendlyByteBuf buf) {
                this.keybindState = buf.readBoolean();
                this.shape = buf.readInt();
                this.distinguishDeepslateOres = buf.readBoolean();
                this.distinguishStoneVariants = buf.readBoolean();
            }

            @Override
            public MessageType getType() {
                return VEINMINE_KEYBIND_CHANGE;
            }

            @Override
            public void write(FriendlyByteBuf buf) {
                buf.writeBoolean(keybindState);
                buf.writeInt(shape);
                buf.writeBoolean(distinguishDeepslateOres);
                buf.writeBoolean(distinguishStoneVariants);
            }

            @Override
            public void handle(NetworkManager.PacketContext context) {
                ServerPlayer player = (ServerPlayer) context.getPlayer();
                Liteminer.instance.onKeymappingStateChange(player,
                        keybindState,
                        shape,
                        distinguishDeepslateOres,
                        distinguishStoneVariants
                );
                new S2CHungerRequirement(FoodExhaustion.isHungerRequired()).sendTo(player);
            }
        }

        public static class S2CHungerRequirement extends BaseS2CMessage {
            private final boolean hungerRequired;

            public S2CHungerRequirement(boolean hungerRequired) {
                this.hungerRequired = hungerRequired;
            }

            public S2CHungerRequirement(FriendlyByteBuf buf) {
                this.hungerRequired = buf.readBoolean();
            }

            @Override
            public MessageType getType() {
                return HUNGER_REQUIREMENT;
            }

            @Override
            public void write(FriendlyByteBuf buf) {
                buf.writeBoolean(hungerRequired);
            }

            @Override
            public void handle(NetworkManager.PacketContext context) {
                LiteminerClient.setHungerRequired(hungerRequired);
            }
        }
    }
}
