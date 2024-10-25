package de.nyc.shopRotationRemake.enums;

public enum Messages {

    //Global messages
    NO_PERMS("&cDu hast keine Rechte für diesen Befehl."),
    INVALID_NUMBER("&c%number ist keine gültige Zahl."),
    INVALID_MATERIAL("&cBitte gib ein gültiges Material an."),
    NO_PLAYER("&cDieser Befehl ist nur für Spieler."),
    MESSAGE_UNKNOWN("&cUnbekannter Befehl. Siehe /srChest help"),

    //Global sr-messages
    UNKNOWN_INVENTORY("&cDas angefragte Inventar existiert nicht."),
    NOT_ENOUGH_ARGUMENTS("&cZu wenige Argumente! Siehe /srChest help"),
    MATERIAL_AIR_NOT_ALLOWED("&cDas Material \"AIR\" ist nicht erlaubt."),

    //srChest messages
    CHEST_UNKNOWN("&cDie Chest mit der UUID: \"%uuid\" wurde nicht gefunden."),
    CHEST_REMOVE_SUCCESS("&aDie Chest wurde erfolgreich entfernt."),
    CHEST_NO_ITEMS("&cEs wurden keine Items in \"%uuid\" festgelegt."),

    //srChest help messages
    CHEST_SET("&e/srChest set &8» &8Erstellen einer srChest"),
    CHEST_GET("&e/srChest get &8» &8Zeigt alle srChests an"),
    CHEST_REMOVE("&e/srChest remove &8» &8Entfernen einer srChest"),
    CHEST_ADMINSETTINGS("&e/srChest adminsettings &8» &8Öffnet das Inventar der srChest"),
    CHEST_HELP("&e/srChest help &8» &8Zeigt diesen Text an"),

    //Enable-disable messages
    SET_ENABLED_SUCCESS("&7Die Chest wurde erfolgreich &cAktiviert&7."),
    ENABLED_ALL("&7Alle Chests wurden &aaktiviert&7."),
    SET_DISABLED_SUCCESS("&7Die Chest wurde erfolgreich &cDeaktiviert&7."),
    DISABLED_ALL("&7Alle Chests wurden &cdeaktiviert&7."),
    ALREADY_ENABLED("&cDie Chest ist schon &aAktiviert&c."),
    ALREADY_DISABLED("&cDie Chest ist schon &4Deaktiviert&c."),

    //TODO: Messages for lootpool and the rewards etc.
    ;

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}