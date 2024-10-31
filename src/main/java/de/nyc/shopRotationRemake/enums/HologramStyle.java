package de.nyc.shopRotationRemake.enums;

public enum HologramStyle {

    HOLOGRAM_ITEM("hologram_item"),
    HOLOGRAM_ITEM_NAME("hologram_item_name"),
    HOLOGRAM_ITEM_NAME_PROGRESS("hologram_item_name_progress"),
    HOLOGRAM_NAME_PROGRESS("hologram_name_progress"),
    HOLOGRAM_ITEM_PROGRESS("hologram_item_progress")

    ;

    private final String key;

    HologramStyle(String key) {
        this.key = key;
    }

    public String getName() {
        return key;
    }
}