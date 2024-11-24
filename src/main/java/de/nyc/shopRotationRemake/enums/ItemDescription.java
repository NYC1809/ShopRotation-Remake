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

    ITEM_CHANGE_TITLE("&eKlicke hier um den &6Titel &ezu ändern!"),

    ITEM_PLAYER_HISTORY_NAME("&eZeigt eine &6Liste &eder letzten &635 &eabgegebenen &6Items &ean:"),

    ITEM_CHANGE_CHEST_TYPE("&eKlicke hier um den &6Block &eder srChest zu ändern!"),
    ITEM_CHANGE_CHEST_TYPE_LORE_1(" "),
    ITEM_CHANGE_CHEST_TYPE_LORE_2("&7Aktueller BlockType: \"&6Material.%type&7\""),
    ITEM_CHANGE_CHEST_TYPE_LORE_3("&cAchtung&7: Die &6BlockDirektion &7wird in die Richtung des &6Spielers &7gesetzt."),

    ITEM_CHANGE_HOLOGRAM_STYLE("&eKlicke hier um den &6Stil &edes &6Holograms &ezu ändern!"),
    ITEM_CHANGE_HOLOGRAM_STYLE_LORE_1(" "),
    ITEM_CHANGE_HOLOGRAM_STYLE_LORE_2("&7Dies öffnet ein weiteres Inventar mit weiteren Einstellungen."),

    HOLOGRAM_STYLE_1_ACTIVE("&dDieser &6Stil &dist gerade &aaktiv&d!"),
    HOLOGRAM_STYLE_1_ACTIVE_LORE("  &9» &e\"&bHOLOGRAM_ITEM&e\""),

    HOLOGRAM_STYLE_2_ACTIVE("&dDieser &6Stil &dist gerade &aaktiv&d!"),
    HOLOGRAM_STYLE_2_ACTIVE_LORE("  &9» &e\"&bHOLOGRAM_ITEM_NAME&e\""),

    HOLOGRAM_STYLE_3_ACTIVE("&dDieser &6Stil &dist gerade &aaktiv&d!"),
    HOLOGRAM_STYLE_3_ACTIVE_LORE("  &9» &e\"&bHOLOGRAM_ITEM_NAME_PROGRESS&e\""),

    HOLOGRAM_STYLE_4_ACTIVE("&dDieser &6Stil &dist gerade &aaktiv&d!"),
    HOLOGRAM_STYLE_4_ACTIVE_LORE("  &9» &e\"&bHOLOGRAM_NAME_PROGRESS&e\""),

    HOLOGRAM_STYLE_5_ACTIVE("&dDieser &6Stil &dist gerade &aaktiv&d!"),
    HOLOGRAM_STYLE_5_ACTIVE_LORE("  &9» &e\"&bHOLOGRAM_ITEM_PROGRESS&e\""),

    HOLOGRAM_STYLE_SPACE_HOLDER(" "),

    HOLOGRAM_STYLE_1("&eKlicke hier um diesen &6Holgram-Stil &eauszuwählen!"),
    HOLOGRAM_STYLE_1_LORE("&eKlicke hier um den Stil: &9» &e\"&bHOLOGRAM_ITEM&e\" auszuwählen."),

    HOLOGRAM_STYLE_2("&eKlicke hier um diesen &6Holgram-Stil &eauszuwählen!"),
    HOLOGRAM_STYLE_2_LORE("&eKlicke hier um den Stil: &9» &e\"&bHOLOGRAM_ITEM_NAME&e\" auszuwählen."),

    HOLOGRAM_STYLE_3("&eKlicke hier um diesen &6Holgram-Stil &eauszuwählen!"),
    HOLOGRAM_STYLE_3_LORE("&eKlicke hier um den Stil: &9» &e\"&bHOLOGRAM_ITEM_NAME_PROGRESS&e\" auszuwählen."),

    HOLOGRAM_STYLE_4("&eKlicke hier um diesen &6Holgram-Stil &eauszuwählen!"),
    HOLOGRAM_STYLE_4_LORE("&eKlicke hier um den Stil: &9» &e\"&bHOLOGRAM_NAME_PROGRESS&e\" auszuwählen."),

    HOLOGRAM_STYLE_5("&eKlicke hier um diesen &6Holgram-Stil &eauszuwählen!"),
    HOLOGRAM_STYLE_5_LORE("&eKlicke hier um den Stil: &9» &e\"&bHOLOGRAM_ITEM_PROGRESS&e\" auszuwählen."),

    HOLOGRAM_ENABLE_TEXT("&eKlicke hier um das &6Hologram &ezu &aaktivieren&7/&cdeaktivieren&e!"),
    HOLOGRAM_ENABLE_TEXT_LORE_1(" "),
    HOLOGRAM_ENABLE_TEXT_LORE_2("&6Links-Klick &7um das Hologram zu &a&laktivieren&r&7!"),
    HOLOGRAM_ENABLE_TEXT_LORE_3("&6Rechts-Klick &7um das Hologram zu &4&ldeaktivieren&r&7!"),

    ITEM_COMING_SOON("&c&lCOMING SOON"),

    ITEM_MODIFY_ITEMS("&eKlicke hier um die Items dieser &6Chest &eanzupassen:"),
    ITEM_MODIFY_ITEMS_LORE_1(" "),
    ITEM_MODIFY_ITEMS_LORE_2("&7Hier kannst du die &bItems &7und die &bBelohnungen &7anpassen!"),

    ITEM_OPEN_AS("&eKlicke hier um die &6Einstellungen &ezu öffnen:"),

    ITEM_ADD_ITEM_TO_IV("&eKlicke hier um neue &6Items &ezu dieser &6Chest &ehinzuzufügen: "),
    ITEM_ADD_ITEM_TO_IV_LORE_1(" "),
    ITEM_ADD_ITEM_TO_IV_LORE_2("&7Hier können weitere Einstellungen zu dem jeweiligem Item getätigt werden."),

    ITEM_ADD_ITEM_BY_DRAG("&eZiehe &6Items &ehier darauf um diese &6hinzuzufügen&e: "),
    ITEM_ADD_ITEM_BY_DRAG_LORE_1(" "),
    ITEM_ADD_ITEM_BY_DRAG_LORE_2("&eDadurch werden die &6Item Properties &edes Items direkt übernommen."),

    ITEM_DELETE_ALL_ITEMS("&cKlicke hier um alle &4&lITEMS &r&cdieser Chest zu löschen!"),
    ITEM_DELETE_ALL_ITEMS_LORE_1(" "),
    ITEM_DELETE_ALL_ITEMS_LORE_2("&6&lDouble-Klick &7um diese Aktion auszuführen!"),
    ITEM_DELETE_ALL_ITEMS_LORE_3("&cWARNING&7: Hiermit setzt du alle Items zurück!"),

    ITEM_BACK_TO_ADD_ITEM_TO_INV("&7Klicke hier um zurück zu allen &6Items &7zu gelangen."),

    ITEM_IS_ENABLED("&eAktuell ist dieses Item &aaktiviert&e!"),
    ITEM_IS_ENABLED_LORE_1(" "),
    ITEM_IS_ENABLED_LORE_2("&7Klicke hier um dieses zu &4deaktivieren&7!"),
    ITEM_IS_DISABLED("&eAktuell ist dieses Item &4deaktiviert&e!"),
    ITEM_IS_DISABLED_LORE_1(" "),
    ITEM_IS_DISABLED_LORE_2("&7Klicke hier um dieses zu &aaktivieren&7!"),

    ITEM_DELETE_ITEM("&cKlicke hier um dieses &4&lITEM &r&czu löschen!"),
    ITEM_DELETE_ITEM_LORE_1(" "),
    ITEM_DELETE_ITEM_LORE_2("&cWARNING&7: Diese Aktion kann &cNICHT &7rückgängig gemacht werden!"),

    ITEM_CHANGE_REQUIRED_AMOUNT("&eKlicke hier um die &6Anzahl &eder &6benötigten &eItems zu ändern:"),
    ITEM_CHANGE_REQUIRED_AMOUNT_LORE_1(" "),
    ITEM_CHANGE_REQUIRED_AMOUNT_LORE_2("&b» Aktueller Wert: &3%amount"),

    ITEM_CHANGE_HOLDING_AMOUNT("&eKlicke hier um die &6Anzahl &eder &6bereits besitzenden &eItem zu ändern:"),
    ITEM_CHANGE_HOLDING_AMOUNT_LORE_1(" "),
    ITEM_CHANGE_HOLDING_AMOUNT_LORE_2("&b» Aktueller Wert: &3%amount"),

    ITEM_RESET_ALL_PLAYERS_ITEM_LIMIT("&eKlicke hier um das &6Item-Limit &efür jeden &6Spieler &ezu &6resetten&e!"),
    ITEM_RESET_ALL_PLAYERS_ITEM_LIMIT_LORE_1(" "),
    ITEM_RESET_ALL_PLAYERS_ITEM_LIMIT_LORE_2("&6&lDouble-Klick &7um diese Aktion auszuführen!"),
    ITEM_RESET_ALL_PLAYERS_ITEM_LIMIT_LORE_3("&7Diese Aktion setzt den &6Counter &7des &6Item-Limits &7für alle Spieler &6zurück&7!"),
    ITEM_RESET_ALL_PLAYERS_ITEM_LIMIT_LORE_4("&7Dies löscht aber &cNICHT &7die Items des Item-Ziels!"),


    ITEM_OPEN_REWARD_GUI("&eKlicke hier um diesem &6Item Belohnungen &ebei &6Abschluss &ehinzuzufügen."),

    ITEM_EXIT_REWARDS_ITEM("&eKlicke hier um zurück zu dem &6Item &ezu gehen!"),
    ITEM_EXIT_REWARDS_ITEM_LORE_1(" "),
    ITEM_EXIT_REWARDS_ITEM_LORE_2("&7Hier kommst du zu dem &6Item &7zurück welches aktuell &6modifiziert &7wird."),

    ITEM_ADD_REWARD_ITEM("&eKlicke hier um eine neue &6Belohnung &ehinzuzufügen"),
    ITEM_ADD_REWARD_ITEM_LORE_1(" "),
    ITEM_ADD_REWARD_ITEM_LORE_2("&7Hier kannst du eine &6Belohnung hinzufügen &7und &6modifizieren&7!"),
    ITEM_ADD_REWARD_ITEM_LORE_3("&7Du kannst auch mehrere &6Belohnungen &7für ein &6Item erstellen&7!"),

    ITEM_REWARDS_REMOVE_ALL("&cKlicke hier um alle &4&lBELOHNUNGEN &r&cdieses Items zu löschen!"),
    ITEM_REWARDS_REMOVE_ALL_LORE_1(" "),
    ITEM_REWARDS_REMOVE_ALL_LORE_2("&6&lDouble-Klick &7um diese Aktion auszuführen!"),
    ITEM_REWARDS_REMOVE_ALL_LORE_3("&cWARNING&7: Diese Aktion kann &cNICHT &7rückgängig gemacht werden!"),

    REWARDS_ADD_NEW_ITEM("&eKlicke hier um eine &6neue Belohnung &ehinzuzufügen:"),
    REWARDS_ADD_NEW_ITEM_LORE_1(" "),
    REWARDS_ADD_NEW_ITEM_LORE_2("&7Daraufhin kannst du weitere Einstellungen zu dieser Belohnung tätigen!"),

    REWARDS_CHANGE_AMOUNT("&eKlicke hier um die &6Anzahl &edes &6Items &ezu verändern:"),
    REWARDS_CHANGE_AMOUNT_LORE_1(" "),
    REWARDS_CHANGE_AMOUNT_LORE_2("&7Diese Zahl bestimmt wie oft dieses Item als Belohnung herausgegeben wird!"),
    REWARDS_CHANGE_AMOUNT_LORE_3(" "),
    REWARDS_CHANGE_AMOUNT_LORE_4("&3» &bAktuelle Zahl: &3%number"),

    REWARDS_ITEM_DELETE("&cKlicke hier um diese &4&lBelohnung &r&czu löschen!"),
    REWARDS_ITEM_DELETE_LORE_1(" "),
    REWARDS_ITEM_DELETE_LORE_2("&cWARNING&7: Diese Aktion kann &cNICHT &7rückgängig gemacht werden!"),

    REWARD_ADD_ITEM_BY_DRAG("&eZiehe &6Items &ehier darauf um diese &6hinzuzufügen&e: "),
    REWARD_ADD_ITEM_BY_DRAG_LORE_1(" "),
    REWARD_ADD_ITEM_BY_DRAG_LORE_2("&eDadurch werden die &6Item Properties &edes Items direkt übernommen."),

    REWARD_CHANGE_ITEM_NAME("&eKlicke hier um den &6Namen &eder &6Belohnung &ezu ändern!"),
    REWARD_CHANGE_ITEM_NAME_LORE_1(" "),
    REWARD_CHANGE_ITEM_NAME_LORE_2("&7Dies ändert den Namen der ausgegeben Belohnung."),

    REWARD_CHANGE_ITEM_LORE("&eKlicke hier um die &6Beschreibung &edes &6Items &eanzupassen."),
    REWARD_CHANGE_ITEM_LORE_LORE_1(" "),
    REWARD_CHANGE_ITEM_LORE_LORE_2("&7Dies öffnet ein weiteres Inventar wo weitere Einstellungen getätigt werden können!"),

    REWARD_LORE_NETHER_STAR("&eDieser Hilfe-Text befindet sich aktuell noch in Arbeit!"),
    REWARD_LORE_NETHER_STAR_LORE_1(" "),
    REWARD_LORE_NETHER_STAR_LORE_2("&c&lCOMING SOON"),

    LORE_EXIT_REWARDS_ITEM("&eKlicke hier um zurück zu dem &6Item &ezu gehen!"),
    LORE_EXIT_REWARDS_ITEM_LORE_1(" "),
    LORE_EXIT_REWARDS_ITEM_LORE_2("&7Hier kommst du zu dem &6Item &7zurück welches aktuell &6modifiziert &7wird."),

    REWARDS_LORE_NAME("&eZeile [&6%number&e] &6» &eBeschreibung des Items:"),
    REWARDS_LORE_LORE_1(" "),
    REWARDS_LORE_LORE_2("   &3» &bAktueller Text: &e\"&r%text&e\""),
    REWARDS_LORE_LORE_3(" "),
    REWARDS_LORE_LORE_4("&6Klicke &ehier um diese &6Zeile &ezu ändern!"),
    REWARDS_LORE_LORE_5("&6Shift-Klicke &ehier um diese &6Zeile &ezu &clöschen&e!"),

    REWARD_ENCHANTMENTS_NETHER_STAR("&eDieser Hilfe-Text befindet sich aktuell noch in Arbeit!"),
    REWARD_ENCHANTMENTS_NETHER_STAR_LORE_1(" "),
    REWARD_ENCHANTMENTS_NETHER_STAR_LORE_2("&c&lCOMING SOON"),

    REWARD_ENCHANTMENTS_CONFIRMATION("&a&lKlicke hier um zu bestätigen!"),
    REWARD_ENCHANTMENTS_CONFIRMATION_LORE_1(" "),
    REWARD_ENCHANTMENTS_CONFIRMATION_LORE_2("&eDies fügt dein &6Enchantment &edem &6Item &ehinzu!"),
    REWARD_ENCHANTMENTS_CONFIRMATION_LORE_3("&6Shift-Klicke &eum diese Aktion &cabzubrechen&e!"),

    REWARD_ENCHANTMENTS_ADD("&eKlicke hier um ein &6Enchantment &ehinzuzufügen!"),
    REWARD_ENCHANTMENTS_ADD_LORE_1(" "),
    REWARD_ENCHANTMENTS_ADD_LORE_2("&3» &bAktuelles Enchantment: &3%name"),
    REWARD_ENCHANTMENTS_ADD_LORE_3("&7Dies öffnet ein Textfenster zum eingeben."),

    REWARD_ENCHANTMENTS_ADD_LEVEL("&eKlicke hier um das dazugehörige &6Level &eeinzugeben!"),
    REWARD_ENCHANTMENTS_ADD_LEVEL_LORE_1(" "),
    REWARD_ENCHANTMENTS_ADD_LEVEL_LORE_2("&3» &bAktuelle Zahl: &3%level"),
    REWARD_ENCHANTMENTS_ADD_LEVEL_LORE_3("&7Dies öffnet ein Textfenster zum eingeben."),

    REWARD_ITEM_ADD_ENCHANTMENTS("&eKlicke hier um ein &6Enchantment &ehinzuzufügen!"),
    REWARD_ITEM_ADD_ENCHANTMENTS_LORE_1(" "),
    REWARD_ITEM_ADD_ENCHANTMENTS_LORE_2("&eDies öffnet ein weiteres &6Inventar &emit weiteren &6Einstellungen&e."),

    ITEM_HELP("&eDieser Hilfe-Text befindet sich aktuell noch in Arbeit!"),
    ITEM_HELP_LORE_1(" "),
    ITEM_HELP_LORE_2("&c&lCOMING SOON"),

    ITEM_HOPPER("&eKlicke hier um zu diesem &6Ziel &edeine Items &6abzugeben&e!"),
    ITEM_HOPPER_LORE_1(" "),
    ITEM_HOPPER_LORE_2("&7Diese Aktion entfernt alle deine Items aus deinem Inventar"),
    ITEM_HOPPER_LORE_3("&7und fügt sie dem Item-Ziel hinzu!"),

    CHEST_HAS_NO_MORE_ACTIVE_ITEMS("&eAktuell gibt es &ckeine &eweiteren &6Item-Ziele&e!"),
    CHEST_HAS_NO_MORE_ACTIVE_ITEMS_LORE_1(" "),
    CHEST_HAS_NO_MORE_ACTIVE_ITEMS_LORE_2("&7Bitte komme zu einem späteren Zeitpunkt zurück!"),

    CHEST_HAS_NO_ACTIVE_ITEM("&eAktuell gibt es &ckein &eaktives &6Item-Ziel&e!"),
    CHEST_HAS_NO_ACTIVE_ITEM_LORE_1(" "),
    CHEST_HAS_NO_ACTIVE_ITEM_LORE_2("&7Bitte komme zu einem späteren Zeitpunkt zurück!"),

    CHANGE_LIMIT_PER_PERSON("&eKlicke hier um das &6Limit Pro Person &eeinzustellen!"),
    CHANGE_LIMIT_PER_PERSON_LORE_1(" "),
    CHANGE_LIMIT_PER_PERSON_LORE_2("&e\"<zahl>\" &7ändert die Anzahl der Items, die jeder Spieler maximal abgeben kann!"),
    CHANGE_LIMIT_PER_PERSON_LORE_3(" "),
    CHANGE_LIMIT_PER_PERSON_LORE_4("&7Bei der &6Eingabe &7von &e\"<zahl> %\" &7wird das Item-Limit pro Spieler"),
    CHANGE_LIMIT_PER_PERSON_LORE_5("&7von der Anzahl der benötigten Items &eprozentuell abhängig &7gemacht!"),

    ;

    private final String text;

    ItemDescription(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getText() {
        return text;
    }
}
