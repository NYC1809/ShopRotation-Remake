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
    ITEM_CHANGE_CHEST_TYPE_LORE_2("&7Aktueller BlockType: \"&6Material.%type&7\""),
    ITEM_CHANGE_CHEST_TYPE_LORE_3("&cAchtung&7: Die &6BlockDirektion &7wird in die Richtung des &6Spielers &7gesetzt."),

    ITEM_COMING_SOON("&c&lCOMING SOON"),

    ITEM_MODIFY_ITEMS("&eKlicke hier um die Items dieser &6Chest &eanzupassen:"),
    ITEM_MODIFY_ITEMS_LORE_1(" "),
    ITEM_MODIFY_ITEMS_LORE_2("&7Hier kannst du verschiedene Einstellungen zu jedem Item tätigen!"),

    ITEM_OPEN_AS("&eKlicke hier um die &6Einstellungen &ezu öffnen:"),

    ITEM_ADD_ITEM_TO_IV("&eKlicke hier um neue &6Items &ezu dieser &6Chest &ehinzuzufügen: "),
    ITEM_ADD_ITEM_TO_IV_LORE_1(" "),
    ITEM_ADD_ITEM_TO_IV_LORE_2("&7Hier können weitere Einstellungen zu dem jeweiligem Item getätigt werden."),

    ITEM_ADD_ITEM_BY_DRAG("&eZiehe &6Items &ehier darauf um diese &6hinzuzufügen&e: "),
    ITEM_ADD_ITEM_BY_DRAG_LORE_1(" "),
    ITEM_ADD_ITEM_BY_DRAG_LORE_2("&eDadurch werden die &6Item Properties &edes Items direkt übernommen."),

    ITEM_DELETE_ALL_ITEMS("&cKlicke hier um alle &4&lITEMS &r&cdieser Chest zu löschen!"),
    ITEM_DELETE_ALL_ITEMS_LORE_1(" "),
    ITEM_DELETE_ALL_ITEMS_LORE_2("&cWARNING&7: Hiermit setzt du alle Items zurück!"),

    ITEM_BACK_TO_ADD_ITEM_TO_INV("&7Klicke hier um zurück zu allen &6Items &7zu gelangen."),

    ITEM_IS_ENABLED("&eAktuell ist dieses Item &aaktiviert&e!"),
    ITEM_IS_ENABLED_LORE_1(" "),
    ITEM_IS_ENABLED_LORE_2("&7Klicke hier um dieses zu &4deaktivieren&7!"),
    ITEM_IS_DISABLED("&eAktuell ist dieses Item &4deaktiviert&e!"),
    ITEM_IS_DISABLED_LORE_1(" "),
    ITEM_IS_DISABLED_LORE_2("&7Klicke hier um dieses zu &aaktivieren&7!"),

    ITEM_DELETE_ITEM("&cKlicke hier um dieses &4&lITEM &r&czu löschen!"),
    ITEM_DELETE_ITEM_LORE_1(" "),
    ITEM_DELETE_ITEM_LORE_2("&cWARNING&7: Diese Aktion kann &cNICHT &7rückgängig gemacht werden!" ),

    ITEM_CHANGE_REQUIRED_AMOUNT("&eKlicke hier um die &6Anzahl &eder &6benötigten &eItems zu ändern:"),
    ITEM_CHANGE_REQUIRED_AMOUNT_LORE_1(" "),
    ITEM_CHANGE_REQUIRED_AMOUNT_LORE_2("&b» Aktueller Wert: &3%amount"),

    ITEM_CHANGE_HOLDING_AMOUNT("&eKlicke hier um die &6Anzahl &eder &6bereits besitzenden &eItem zu ändern:"),
    ITEM_CHANGE_HOLDING_AMOUNT_LORE_1(" "),
    ITEM_CHANGE_HOLDING_AMOUNT_LORE_2("&b» Aktueller Wert: &3%amount"),

    ITEM_OPEN_REWARD_GUI("&eKlicke hier um diesem &6Item Belohnungen &ebei &6Abschluss &ehinzuzufügen."),
    ITEM_OPEN_REWARD_GUI_LORE_1(" "),
    //TODO: Show current rewards in Item Lore:
    ITEM_OPEN_REWARD_GUI_LORE_2("&b» Aktuelle Belohnungen: &3%rewards"),

    ITEM_EXIT_REWARDS_ITEM("&eKlicke hier um zurück zu dem &6Item zu gehen!"),
    ITEM_EXIT_REWARDS_ITEM_LORE_1(" "),
    ITEM_EXIT_REWARDS_ITEM_LORE_2("&7Hier kommst du zu dem &6Item &7zurück welches aktuell &6modifiziert &7wird."),

    ITEM_ADD_REWARD_ITEM("&eKlicke hier um eine neue &6Belohnung &ehinzuzufügen"),
    ITEM_ADD_REWARD_ITEM_LORE_1(" "),
    ITEM_ADD_REWARD_ITEM_LORE_2("&7Hier kannst du eine &6Belohnung hinzufügen &7und &6modifizieren&7!"),
    ITEM_ADD_REWARD_ITEM_LORE_3("&7Du kannst auch mehrere &6Belohnungen &7für ein &6Item erstellen&7!"),

    ITEM_REWARDS_REMOVE_LAST("&eKlicke hier um die letzte &6Belohnung &edes Items zu &centfernen&e!"),
    ITEM_REWARDS_REMOVE_LAST_LORE_1(" "),
    ITEM_REWARDS_REMOVE_LAST_LORE_2("&cWARNING&7: Diese Aktion kann &cNICHT &7rückgängig gemacht werden!"),

    ITEM_REWARDS_REMOVE_ALL("&cKlicke hier um alle &4&lBELOHNUNGEN &r&cdieses Items zu löschen!"),
    ITEM_REWARDS_REMOVE_ALL_LORE_1(" "),
    ITEM_REWARDS_REMOVE_ALL_LORE_2("&cWARNING&7: Diese Aktion kann &cNICHT &7rückgängig gemacht werden!"),

    ITEM_REWARDS_HELP("&eDieser Hilfe-Text befindet sich aktuell noch in Arbeit!"),
    ITEM_REWARDS_HELP_LORE_1(" "),
    ITEM_REWARDS_HELP_LORE_2("&c&lCOMING SOON"),

    REWARDS_DESCRIPTION_NAME(Utils.getPrefix() + "&6Rewards"),
    REWARDS_DESCRIPTION_LORE_1(" "),
    REWARDS_DESCRIPTION_LORE_2("&7Hier kannst du verschiedene Einstellungen tätigen:"),

    REWARDS_ADD_NEW_ITEM("&eKlicke hier um eine &6neue Belohnung &ehinzuzufügen:"),
    REWARDS_ADD_NEW_ITEM_LORE_1(" "),
    REWARDS_ADD_NEW_ITEM_LORE_2("&7Daraufhin kannst du weitere Einstellungen zu dieser Belohnung tätigen!"),

   ;

    private final String text;

    ItemDescription(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getText() {
        return text;
    }
}
