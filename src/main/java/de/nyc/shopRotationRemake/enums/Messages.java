package de.nyc.shopRotationRemake.enums;

import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.ChatColor;

public enum Messages {

    //Global messages
    NO_PERMS("&cDu hast keine Rechte für diesen Befehl."),
    NO_PLAYER("&cDieser Befehl ist nur für Spieler."),
    MESSAGE_UNKNOWN("&cUnbekannter Befehl. Siehe /srChest help"),
    NO_PERMS_ERROR("&cERROR: Du hast keine Rechte darauf zuzugreifen!"),

    //Global sr-messages
    NOT_ENOUGH_ARGUMENTS("&cZu wenige Argumente! Siehe /srChest help"),
    TOO_MUCH_ARGUMENTS("&cZu viele Argumente! Siehe /srChest help"),
    LOCATION_HAS_TO_BE_AIR("&cWARNING: Du kannst hier keine Chest erstellen! (Benötigt \"Material.AIR\")"),
    CHEST_DOES_NOT_EXISITS("&cDie Chest \"%name\" existiert nicht!"),
    NO_CHEST_EXISTS("&cEs existiert aktuell keine srChest!"),
    CANNOT_BREAK_BLOCK("&cDu kannst diesen Block nicht zerstören! \nNutze /srChest remove <chest>"),
    IS_NOT_NUMERIC("&c\"%input\" ist keine gültige Zahl!"),
    IS_NOT_ENCHANTMENT("&c\"%input\" ist kein gültiges Enchantment!"),
    NO_ITEM_IN_MAIN_HAND("&cDu hast aktuell kein Item in der Hand!"),

    //srChest messages
    CHEST_REMOVE_SUCCESS("&7Die Chest \"&a%chest&7\" wurde erfolgreich &centfernt&7."),
    CHEST_NO_ITEMS("&cEs wurden keine Items in \"%uuid\" festgelegt."),

    MATERIAL_WRONG("&cDas Material \"%input\" ist kein valides Material."),

    CHEST_IS_DISABLED("&cDie srChest ist aktuell &4Deaktiviert&c."),
    CHEST_CREATE_SUCCESS("&7Du hast &aerfolgreich &7die srChest \"&a%uuid&7\" erstellt!"),
    CHEST_NAME_ALREADY_EXISTS("&cEine srChest mit dem Namen: \"%name\" existiert bereits!"),
    CHEST_CHANGED_NAME_SUCCESS("&7Du hast &aerfolgreich &7den &6Namen &7der srChest zu: \"%name&7\" geändert."),
    CHEST_CHANGED_NAME_CANCEL("&7Die srChest hat bereits den &6Titel&7: \"&a%name&7\""),
    CHEST_CHANGED_TYPE_SUCCESS("&7Du hast &aerfolgreich &7den &6BlockType&7 der srChest zu \"&a%type&7\" geändert."),
    CHEST_CHANGED_TYPE_CANCEL("&7Die srChest hat bereits den &6BlockType&7: \"&aMaterial.%type&7\""),
    CHEST_CHANGED_ITEM_LIMIT_SUCCESS("&7Du hast &aerfolgreich &7das &6Item-Limit pro Person &7zu \"&a%number&7\" geändert."),
    CHEST_CHANGED_ITEM_LIMIT_SUCCESS_PERCENTAGE("&7Du hast &aerfolgreich &7das &6Item-Limit pro Person &7zu \"&a%number %\" geändert."),
    CHEST_CHANGED_ITEM_LIMIT_CANCEL("&7Das Limit ist bereits auf \"&a%number&7\" festgelegt."),
    CHEST_CHANGED_ITEM_LIMIT_NOTIFICATION_1("&7Du kannst entwerder &e\"&a<zahl>&e\" &7um das Item-Limit pro Person "),
    CHEST_CHANGED_ITEM_LIMIT_NOTIFICATION_2("&7auf eine Zahl festzulegen oder &e\"&a<zahl> %&e\" &7um das Limit pro"),
    CHEST_CHANGED_ITEM_LIMIT_NOTIFICATION_3("&7Person prozentuell abhängig von dem benötigten Wert einzustellen."),
    CHEST_CHANGED_ITEM_LIMIT_WRONG_INPUT("&7\"&a%input&7\" ist das falsche String-Format."),
    CHEST_CHANGED_ITEM_LIMIT_PERCENTAGE_CANNOT_BE_OVER_100("&7\"&a%input&7\" kann nicht größer als &a100 Prozent &7sein!"),
    CHEST_CHANGED_MINIMUM_REQUIREMENT_CANNOT_BE_OVER_100("&7\"&a%input&7\" kann nicht größer als &a100 Prozent &7sein!"),
    CHEST_CHANGED_MINIMUM_REQUIREMENT_SUCCESS("&7Du hast &aerfolgreich den &6minimalen Wert &7an benötigten Items zu \"&a%number %\" &7geändert."),
    CHEST_CHANGED_MINIMUM_REQUIREMENT_CANCEL("&7Der minimale Wert ist bereits auf \"&a%number %\" &7festgelegt."),

    ITEM_ADD_CANCELED("&7Du hast die &6Aktion&7: \"Item hinzufügen\" &cabgebrochen&7."),
    ITEM_ADDED_SUCCESS("&7Du hast &aerfolgreich &7das Item \"&a%item&7\" hinzugefügt."),
    ITEM_MODIFICATE_FOR_CHANGES("&6Modifiziere &7das &6Item &7in der Chest um noch mehr &6Eigenschaften &7anzupassen!"),

    ITEMS_REMOVED_SUCCESS("&7Du hast &aerfolgreich &c&lALLE &r&7Items aus der Chest: \"&a%name&7\" &centfernt&7!"),
    ITEM_CHANGED_AMOUNT_SUCCESS("&7Du hast &aerfolgreich &7den Wert zu \"&a%number&7\" geändert!"),
    TOO_MANY_ITEMS_IN_CHEST("&7Diese Chest enthält zu viele Items. Es werden nur die ersten 28 Items dargestellt!"),

    //srChest help messages
    CHEST_CREATE("&6⁜ &e/srChest create &6» &7Erstellen einer srChest"),
    CHEST_GET("&6⁜ &e/srChest get &6» &7Zeigt alle srChests an"),
    CHEST_REMOVE("&6⁜ &e/srChest remove &6» &7Entfernen einer srChest"),
    CHEST_ADMINSETTINGS("&6⁜ &e/srChest adminsettings &6» &7Öffnet das Inventar der srChest"),
    CHEST_ADD_ITEM("&6⁜ &e/srChest add &6» &7Fügt Items der srChest hinzu"),
    CHEST_DEBUG("&6⁜ &e/srChest debug &6» &7WARNING: Nur für Debugging!"),
    CHEST_HELP("&6⁜ &e/srChest help &6» &7Zeigt diesen Text an"),

    //Enable-disable messages
    SET_ENABLED_SUCCESS("&7Die Chest wurde erfolgreich &aAktiviert&7."),
    ENABLED_ALL("&7Alle Chests wurden &aaktiviert&7."),
    SET_DISABLED_SUCCESS("&7Die Chest wurde erfolgreich &cDeaktiviert&7."),
    DISABLED_ALL("&7Alle Chests wurden &cdeaktiviert&7."),
    ALREADY_ENABLED("&cDie Chest ist schon &aAktiviert&c."),
    ALREADY_DISABLED("&cDie Chest ist schon &4Deaktiviert&c."),

    //Reward messages
    ITEM_DISABLED_SUCCESS("&7Du hast &aerfolgreich &7das Item: \"&a%itemuuid&7\" &cdeaktiviert&7!"),
    ITEM_ENABLED_SUCCES("&7Du hast &aerfolgreich &7das Item: \"&a%itemuuid&7\" &aaktiviert&7!"),
    ITEM_REMOVED_SUCCES("&7Du hast &aerfolgreich &7das Item: \"&a%itemuuid&7\" &cgelöscht&7!"),

    REWARD_REMOVED_SUCCESS("&7Du hast &aerfolgreich &7diese &aBelohnung &cgelöscht&7!"),
    LAST_REWARD_DIDNT_EXIST("&7Dieses Item besitzt aktuell &ckeine &7Belohnungen!"),
    All_REWARDS_REMOVED("&7Du hast &aerfolgreich &c&lALLE &r&7Belohnungen von dem Item: \"&a%itemuuid&7\" &centfernt&7!"),

    ADD_REWARD_CANCEL("&7Du hast die &6Aktion&7: \"Belohnung hinzufügen\" &cabgebrochen&7."),
    REWARD_ADDED_SUCCESS("&7Du hast &aerfolgreich &7die Belohnung \"&a%item&7\" hinzugefügt."),

    TOO_MANY_REWARDS("&7Dieses Item hat zu viele Belohnungen. Es werden nur die ersten 4 dargestellt!"),
    REWARD_AMOUNT_CHANGED_SUCCESS("&7Du hast &aerfolgreich &7den Wert zu \"&a%number&7\" geändert!"),
    REWARD_ITEM_CHANGE_NAME_CANCEL("&7Diese Belohnung hat bereits den &6Titel&7: \"&a%name&7\""),
    REWARD_ITEM_CHANGE_NAME_SUCCESS("&7Du hast &aerfolgreich &7den &6Namen &7der Belohnung zu: \"&a%name&7\" geändert."),

    REWARD_MODIFICATE_FOR_CHANGES("&6Modifiziere &7das &6Item &7um noch mehr &6Eigenschaften &7anzupassen!"),
    REWARD_LORE_LINE_REMOVED_SUCCESS("&7Du hast &aerfolgreich &7die Zeile: \"&a%text&7\" &centfernt&7!"),
    REWARD_LORE_LINE_CHANGED_SUCCESS("&7Du hast &aerfolgreich &7die Zeile &6%line &7zu \"&a%text&7\" &ageändert&7!"),
    REWARD_LORE_ALREADY_EXISTS("&7Diese Zeile hat bereits den Text: \"&a%input&7\"."),
    REWARD_LORE_ADD_CANCEL("&7Du hast die &6Aktion&7: \"Beschreibung hinzufügen\" &cabgebrochen&7."),

    REWARD_ENCHANTMENTS_CANCEL("&7Du hast die &6Aktion&7: \"Enchantment hinzufügen\" &cabgebrochen&7."),
    REWARD_ENCHANTMENTS_ADD_SUCCESS("&7Du hast &aerfolgreich &7das Enchantment: \"&a%enchantment &7 -- Level: &a%level&7\" hinzugefügt."),
    REWARD_ENCHANTMENTS_ADD_WRONG_FORMAT("&7\"&a%input&7\" ist das falsche String-Format."),

    HOLOGRAM_CHANGE_STYLE_SUCCES("&7Du hast &aerfolgreich &7den Hologram-Stil zu: \"&a%style&7\" &ageändert&7!"),
    HOLOGRAM_ALREADY_ENABLED("&7Das Hologram ist bereits &aaktiviert&7!"),
    HOLOGRAM_ALREADY_DISABLED("&7Das Hologram ist bereits &cdeaktiviert&7!"),
    HOLOGRAM_ENABLED_SUCCESS("&7Du hast &aerfolgreich &7das Hologram &aaktiviert&7!"),
    HOLOGRAM_DISABLED_SUCCESS("&7Du hast &aerfolgreich &7das Hologram &cdeaktiviert&7!"),

    PLAYER_GIVE_ITEM_MAX_VALUE_REACHED("&cDu kannst keine weiteren Items zu diesem Ziel mehr abgeben!"),
    PLAYER_GIVE_ITEM_SUCCES("&7Du hast &aerfolgreich &7\"&a%number&7\" Items zu dem aktuellen Item-Ziel beigetragen!"),

    PLAYER_ITEM_LIMIT_RESET_ZERO("&cAktuell haben noch keine Spieler ein Item zu diesem Item-Ziel beigetragen!"),
    PLAYER_ITEM_LIMIT_RESET_SUCCESS("&7Du hast &aerfolgreich &7das &6Item-Limit &7für &6%number &7Spieler &azurückgesetzt!"),

    //srChest processGivingRewards Messages
    PLAYER_DID_NOT_GIVE_ENOUGH_ITEMS("&cDu hattest leider nicht genug Items abgegeben! &7(&e%givenamount&6/&e%requiredamount&7)"),
    PLAYER_REWARD_RECIEVED_1(Utils.getPrefix() + "&7Du hast %amountx &6%itemname &7als &dBelohnung &7erhalten!"),
    PLAYER_REWARD_RECIEVED_2(Utils.getPrefix() + "&7Du hast %amountx &6%itemname &b(&9%itemmaterial&b) &7als &dBelohnung &7erhalten!"),
    PLAYER_HAS_NOT_ENOUGH_INVENTORY_SPACE("&cDein Inventar ist Voll! Die fehlenden Belohnungen werden dir beim nächsten Server-Login übergeben!"),

    //srChest get messages
    GET_UUID_INFO_LINE_1(Utils.getPrefix() + "&2Anklicken zum kopieren:"),

    ;

    private final String message;

    Messages(String message) {
        this.message = ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage() {
        return message;
    }
}
