package net.aosankaku.liteminerdelta;

import java.util.UUID;

public class LiteminerPlayerState {
    private final UUID uuid;
    private boolean keymappingState = false;
    private int shape = 0;
    private boolean distinguishDeepslateOres = true;
    private boolean distinguishStoneVariants = true;

    public LiteminerPlayerState(UUID playerUuid) {
        this.uuid = playerUuid;
    }

    public boolean getKeymappingState() {
        return keymappingState;
    }

    public void setKeymappingState(boolean keymappingState) {
        this.keymappingState = keymappingState;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public boolean getDistinguishDeepslateOres() {
        return distinguishDeepslateOres;
    }

    public void setDistinguishDeepslateOres(boolean distinguishDeepslateOres) {
        this.distinguishDeepslateOres = distinguishDeepslateOres;
    }

    public boolean getDistinguishStoneVariants() {
        return distinguishStoneVariants;
    }

    public void setDistinguishStoneVariants(boolean distinguishStoneVariants) {
        this.distinguishStoneVariants = distinguishStoneVariants;
    }
}
