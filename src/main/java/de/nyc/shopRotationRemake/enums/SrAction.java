package de.nyc.shopRotationRemake.enums;

import org.bukkit.ChatColor;

public enum SrAction {

    CHEST_CREATED("Action.Chest.&2CREATED"),
    CHEST_DELETED("Action.Chest.&4DELETED"),
    CHEST_ENABLED("Action.Chest.&aENABLED"),
    CHEST_DISABLED("Action.Chest.&cDISABLED"),
    CHEST_ALL_ENABLED("Action.Chest.&aENABLED_ALL"),
    CHEST_ALL_DISABLED("Action.Chest.&cDISABLED_ALL"),
    CHEST_ITEM_GOAL_FINISHED("Action.Chest.Item.&dGOAL_FINISHED"),

    ITEM_ADD("Action.Chest.Item.&2ADDED"),
    ITEM_REMOVED("Action.Chest.Item.&4REMOVED"),
    ITEM_ENABLED("Action.Item.&aENABLED"),
    ITEM_DISABLED("Action.Item.&cDISABLED"),
    ALL_ITEMS_REMOVED("Action.Chest.Item.&4REMOVED_ALL"),
    REWARD_ADD("Action.Chest.Reward.&2ADDED"),
    REWARD_REMOVED("Action.Chest.Reward.&4REMOVED"),
    ITEM_LIMIT_PER_PERSON_CHANGED("Action.Chest.Item.Limit_per_person.&dCHANGED"),

    //srChest Settings
    CHEST_TITLE_CHANGED("Action.Chest.Settings.&dTITLE_CHANGED"),
    CHEST_HOLOGRAM_ENABLED("Action.Chest.Settings.Hologram.&aENABLED"),
    CHEST_HOLOGRAM_DISABLED("Action.Chest.Settings.Hologram.&cDISABLED"),
    CHEST_TYPE_CHANGED("Action.Chest.Settings.&dBLOCK_TYPE_CHANGED"),
    CHEST_HOLOGRAM_STYLE_CHANGED("Action.Chest.Settings.Hologram.&dCHANGED"),

    //srChest player history:
    ITEM_GIVEN("Action.Chest.Item.&5ITEM_GIVEN"),
    PLAYER_REACHED_LIMIT("Action.Chest.Item.&5PLAYER_LIMIT_REACHED")

    ;

    private final String action;

    SrAction(String action) {
        this.action = ChatColor.translateAlternateColorCodes('&', action);
    }

    public String getMessage() {
        return action;
    }
}
