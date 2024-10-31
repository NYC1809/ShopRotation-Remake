package de.nyc.shopRotationRemake.enums;

import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.ChatColor;

public enum ItemDescription {

    ITEM_ENABLED("&eDiese srChest ist aktuell &aaktiviert&e!"),
    ITEM_ENABLED_LORE_1(" "),
    ITEM_ENABLED_LORE_2("&7Klicke hier um diese zu &cdeaktivieren&7!"),

    ITEM_DISABLED("&eDiese srChest ist aktuell &cdeaktiviert&e!"),
    ITEM_DISABLED_LORE_1(" "),
    ITEM_DISABLED_LORE_2("&7Klicke hier um diese zu &aaktivieren&7!"),

    ITEM_ENABLE_DISABLE_ALL("&eKlicke um alle srChests zu &aaktivieren&e!"),
    ITEM_ENABLE_DISABLE_ALL_LORE_1(" "),
    ITEM_ENABLE_DISABLE_ALL_LORE_2("&eShift-Klicke um alle srChests zu &cdeaktivieren&e!"),

    AS_DESCRIPTION_NAME(Utils.getPrefix() + "&6AdminSettings"),
    AS_DESCRIPTION_LORE_1(" "),
    AS_DESCRIPTION_LORE_2("&7Hier kannst du verschiedene Einstellungen zu dieser srChest tätigen:")

    ;

    private final String text;

    ItemDescription(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getText() {
        return text;
    }
}
