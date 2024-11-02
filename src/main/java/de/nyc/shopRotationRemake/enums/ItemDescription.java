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

    CHEST_HAS_NO_ACTIVE_ITEM("&cDiese srChest hat aktuell kein aktives Item!"),
    CHEST_HAS_NO_ACTIVE_ITEM_LORE_1(" "),
    CHEST_HAS_NO_ACTIVE_ITEM_LORE_2("&7Bitte komme zu einem späteren Zeitpunkt zurück"),

    ITEM_ENABLE_DISABLE_ALL("&aEnable all&6 / &4Disable all"),
    ITEM_ENABLE_DISABLE_ALL_LORE_1(" "),
    ITEM_ENABLE_DISABLE_ALL_LORE_2("&6Links-Klick &7um alle srChests zu &a&laktivieren&r&7!"),
    ITEM_ENABLE_DISABLE_ALL_LORE_3("&6Rechts-Klick &7um alle srChests zu &4&ldeaktivieren&r&7!"),

    AS_DESCRIPTION_NAME(Utils.getPrefix() + "&6AdminSettings"),
    AS_DESCRIPTION_LORE_1(" "),
    AS_DESCRIPTION_LORE_2("&7Hier kannst du verschiedene Einstellungen tätigen:"),

    ITEM_ACTION_HISTORY_NAME("&eZeigt die letzten srChest &6Aktionen&e an: "),

    ITEM_CHANGE_TITLE("&eKlicke hier um den &6Titel &ezu ändern!"),

    ITEM_CHANGE_CHEST_TYPE("&eKlicke hier um den &6Block &eder srChest zu ändern!"),
    ITEM_CHANGE_CHEST_TYPE_LORE_1(" "),
    ITEM_CHANGE_CHEST_TYPE_LORE_2("&eAktueller BlockType: &6\"Material.%type&e\""),

    ITEM_COMING_SOON("&c&lCOMING SOON"),

    ITEM_MODIFY_ITEMS("&eKlicke hier um die Items dieser &6Chest &eanzupassen:"),
    ITEM_MODIFY_ITEMS_LORE_1(" "),
    ITEM_MODIFY_ITEMS_LORE_2("&7Hier kannst du verschiedene Einstellungen zu jedem Item tätigen!"),

    ITEM_OPEN_AS("&eKlicke hier um die &6Einstellungen &ezu öffnen:"),

    ITEM_ADD_ITEM_TO_IV("&eKlicke hier um neue &6Items &ezu dieser &6Chest &ehinzuzufügen: "),
    ITEM_ADD_ITEM_TO_IV_LORE_1(" "),
    ITEM_ADD_ITEM_TO_IV_LORE_2("&7Hier können weitere Einstellungen zu dem jeweiligem Item getätigt werden."),

    ITEM_DELETE_ALL_ITEMS("&cKlicke hier um alle &4&lITEMS &r&cdieser Chest zu löschen!"),
    ITEM_DELETE_ALL_ITEMS_LORE_1(" "),
    ITEM_DELETE_ALL_ITEMS_LORE_2("&cWARNING&7: Hiermit setzt du alle Items zurück!")

    ;

    private final String text;

    ItemDescription(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getText() {
        return text;
    }
}
