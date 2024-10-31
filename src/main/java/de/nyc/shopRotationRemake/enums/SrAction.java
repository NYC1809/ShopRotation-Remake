package de.nyc.shopRotationRemake.enums;

import org.bukkit.ChatColor;

public enum SrAction {

    CHEST_CREATED("Action.Chest.CREATED"),
    CHEST_DELETED("Action.Chest.DELETED"),
    CHEST_ENABLED("Action.Chest.ENABLED"),
    CHEST_DISABLED("Action.Chest.DISABLED"),
    CHEST_ALL_ENABLED("Action.Chest.ENABLED_ALL"),
    CHEST_ALL_DISABLED("Action.Chest.DISABLED_ALL"),
    CHEST_ITEM_GOAL_FINISHED("Action.Chest.Item.GOAL_FINISHED"),

    ITEM_ADD("Action.Chest.Item.ADDED"),
    ITEM_REMOVED("Action.Chest.Item.REMOVED"),
    ALL_ITEMS_REMOVED("Action.Chest.Item.REMOVED_ALL"),
    REWARD_ADD("Action.Chest.Reward.ADDED"),
    REWARD_REMOVED("Action.Chest.Reward.REMOVED"),
    ITEM_LIMIT_PER_PERSON_CHANGED("Action.Chest.Item.Limit_per_person.CHANGED"),

    //srChest Settings
    CHEST_TITLE_CHANGED("Action.Chest.Settings.TITLE_CHANGED"),
    CHEST_HOLOGRAM_ENABLED("Action.Chest.Settings.HOLOGRAM_ENABLED"),
    CHEST_HOLOGRAM_DISABLED("Action.Chest.Settings.HOLOGRAM_DISABLED"),
    CHEST_TYPE_CHANGED("Action.Chest.Settings.BLOCK_TYPE_CHANGED"),

    //srChest player history:
    ITEM_GIVEN("Action.Chest.Item.ITEM_GIVEN"),
    PLAYER_REACHED_LIMIT("Action.Chest.Item.PLAYER_LIMIT_REACHED")

    ;

    private final String action;

    SrAction(String action) {
        this.action = ChatColor.translateAlternateColorCodes('&', action);
    }

    public String getMessage() {
        return action;
    }
}
