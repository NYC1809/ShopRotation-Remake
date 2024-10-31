package de.nyc.shopRotationRemake.enums;

import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.ChatColor;

public enum ItemDescription {

    ITEM_ENABLED("&eDiese srChest ist aktuell &aaktiviert&e!"),
    ITEM_ENABLED_LORE_1(" "),
    ITEM_ENABLED_LORE_2("&7Klicke hier um diese zu &4deaktivieren&7!"),

    ITEM_DISABLED("&eDiese srChest ist aktuell &4deaktiviert&e!"),
    ITEM_DISABLED_LORE_1(" "),
    ITEM_DISABLED_LORE_2("&7Klicke hier um diese zu &aaktivieren&7!"),

    ITEM_ENABLE_DISABLE_ALL("&aEnable all&6 / &4Disable all"),
    ITEM_ENABLE_DISABLE_ALL_LORE_1(" "),
    ITEM_ENABLE_DISABLE_ALL_LORE_2("&6Links-Klick &7um alle srChests zu &a&laktivieren&r&7!"),
    ITEM_ENABLE_DISABLE_ALL_LORE_3("&6Rechts-Klick &7um alle srChests zu &4&ldeaktivieren&r&7!"),

    AS_DESCRIPTION_NAME(Utils.getPrefix() + "&6AdminSettings"),
    AS_DESCRIPTION_LORE_1(" "),
    AS_DESCRIPTION_LORE_2("&7Hier kannst du verschiedene Einstellungen tätigen:"),

    ITEM_ACTION_HISTORY_NAME("&eZeigt die letzten srChest &6Aktionen&e an: "),
    ITEM_ACTION_HISTORY_LORE1(" "),

    ITEM_CHANGE_TITLE("&eKlicke hier um den &6Titel &ezu ändern!")

    ;

    private final String text;

    ItemDescription(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getText() {
        return text;
    }
}
