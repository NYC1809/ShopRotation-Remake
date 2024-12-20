package de.nyc.shopRotationRemake.enums;

import org.bukkit.ChatColor;

public enum SrAction {

    CHEST_CREATED("Action.Chest.&2CREATED"),
    CHEST_DELETED("Action.Chest.&4DELETED"),
    CHEST_ENABLED("Action.Chest.&aENABLED"),
    CHEST_DISABLED("Action.Chest.&cDISABLED"),
    CHEST_ALL_ENABLED("Action.Chest.&aENABLED_ALL"),
    CHEST_ALL_DISABLED("Action.Chest.&cDISABLED_ALL"),
    CHEST_ITEM_GOAL_FINISHED("Action.Chest.&6Item&d.&dGOAL_FINISHED"),

    ITEM_ADD("Action.Chest.&6Item&d.&2ADDED"),
    ITEM_REMOVED("Action.Chest.&6Item&d.&4REMOVED"),
    ITEM_ENABLED("Action.&6Item&d.&aENABLED"),
    ITEM_DISABLED("Action.&6Item&d.&cDISABLED"),
    ALL_ITEMS_REMOVED("Action.Chest.&6Item&d.&4REMOVED_ALL"),
    REWARD_ADD("Action.Chest.&9Reward&d.&2ADDED"),
    REWARD_REMOVED("Action.Chest.&9Reward&d.&4REMOVED"),
    REWARD__ALL_REMOVED("Action.Chest.&9Reward&d.&4REMOVED_ALL"),
    ITEM_LIMIT_PER_PERSON_CHANGED("Action.Chest.&6Item&d.Limit_per_person.&dCHANGED"), //TODO: IMPLEMENT THIS

    //srChest Settings
    CHEST_TITLE_CHANGED("Action.Chest.Settings.&dTITLE_CHANGED"),
    CHEST_HOLOGRAM_ENABLED("Action.Chest.Settings.Hologram.&aENABLED"),
    CHEST_HOLOGRAM_DISABLED("Action.Chest.Settings.Hologram.&cDISABLED"),
    CHEST_TYPE_CHANGED("Action.Chest.Settings.&dBLOCK_TYPE_CHANGED"),
    CHEST_HOLOGRAM_STYLE_CHANGED("Action.Chest.Settings.Hologram.&dCHANGED"),
    CHEST_ITEM_LIMIT_CHANGED("Action.Chest.Settings.PlayerItemLimit.&dCHANGED"),
    CHEST_ITEM_MINIMUM_REQUIREMENT_CHANGED("Action.Chest.Settings.MinimumRequirement.&dCHANGED"),

    //srChest player history:
    ITEM_GIVEN("Action.Chest.&6Item&d.&5ITEM_GIVEN"), //TODO: IMPLEMENT THIS
    PLAYER_REACHED_LIMIT("Action.Chest.&6Item&d.&5PLAYER_LIMIT_REACHED"), //TODO: IMPLEMENT THIS

    //TODO: Add srAction - Rewards
    ;

    private final String action;

    SrAction(String action) {
        this.action = ChatColor.translateAlternateColorCodes('&', action);
    }

    public String getMessage() {
        return action;
    }
}
