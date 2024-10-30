package de.nyc.shopRotationRemake.database;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.objects.Quadruple;
import de.nyc.shopRotationRemake.enums.HologramStyle;
import de.nyc.shopRotationRemake.enums.SrAction;
import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SrDatabase {

    private final Connection connection;
    private final Main main;

    public SrDatabase(String path, Main main) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        this.main = main;
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "holdingamount INTEGER NOT NULL, " +
                    "requiredamount INTEGER NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS rewards (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "amount INTEGER NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS chest (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "location TEXT NOT NULL, " +
                    "enabled TEXT NOT NULL, " +
                    "type TEXT NOT NULL, " +
                    "hologram TEXT NOT NULL, " +
                    "hologramstyle TEXT NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS currentitem (" +
                   "uuid TEXT PRIMARY KEY, " +
                    "item TEXT NOT NULL, " +
                    "amount INT NOT NULL, " +
                    "holdingamount INT NOT NULL, " +
                    "completed TEXT)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT NOT NULL, " +
                    "timestamp TEXT NOT NULL, " +
                    "player TEXT NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "amount INTEGER NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS actionhistory (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT, " +
                    "timestamp TEXT NOT NULL, " +
                    "player TEXT NOT NULL, " +
                    "action TEXT NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS player (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "item TEXT NOT NULL, " +
                    "player TEXT NOT NULL, " +
                    "givenamount INTEGER, " +
                    "maxamount INTEGER NOT NULL)");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void createChest(UUID uuid, String name, Location location, Boolean enabled, Material type, Boolean hologram, Player player, HologramStyle style) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO chest (uuid, name, location, enabled, type, hologram, hologramstyle) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, location.toString());
            preparedStatement.setString(4,enabled.toString());
            preparedStatement.setString(5, type.toString());
            preparedStatement.setString(6, hologram.toString());
            preparedStatement.setString(7, style.getName());
            preparedStatement.executeUpdate();
        }
        this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_CREATED, uuid);
    }

    public List<String> processAllChestUuids() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid, name FROM chest GROUP BY name")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if(isTableEmpty("chest")) {
                Bukkit.getLogger().severe("The SQL-LITE table \"chest\" has no entries!");
                return null;
            }

            main.getUuidList().clear();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                if(main.getUuidList().contains(uuid)) {
                    continue;
                }
                main.getUuidList().add(uuid);
            }
            return main.getUuidList();
        }
    }

    public void addFromDBtoList() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid, name FROM chest GROUP BY uuid")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if(isTableEmpty("chest")) {
                Bukkit.getLogger().severe("[28:10:01:26] SQL-Table \"chest\" is empty!");
                return;
            }
            main.getUuidList().clear();
            main.getChestNames().clear();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                if(!main.getUuidList().contains(uuid)) {
                    main.getUuidList().add(uuid);
                }

                String name = resultSet.getString("name");
                if(!main.getChestNames().contains(name)) {
                    main.getChestNames().add(name);
                }
            }
        }
    }

    public void deleteChestByUuid(String input, Player player) throws SQLException {
        UUID uuid = getUuidByInput(input);
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());

            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0) {
                Bukkit.getLogger().severe("[28:98:12] Removed entry from SQL - DB!");
                this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_DELETED, uuid);
                return;
            }
            Bukkit.getLogger().warning("[28:98:12] No entry found to remove SQL - DB!");
        }
    }

    public boolean locationExistsInDB(Location location) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM chest where location = ?")) {
            preparedStatement.setString(1, location.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public Location getLocationOfChest(String input) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT location FROM chest WHERE name = ? OR uuid = ?")) {
            preparedStatement.setString(1, input);
            preparedStatement.setString(2, input);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                String loc = resultSet.getString("location");
                return Utils.stringToLocation(loc);
            }
            return null;
        }
    }

    public String getChestByLocation(Location location) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid FROM chest WHERE location = ?")) {
            preparedStatement.setString(1, location.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!locationExistsInDB(location)) {
                Bukkit.getLogger().info("[65:87:14] Location does not exist in DB!");
                return null;
            }
            return resultSet.getString("uuid");
        }
    }

    public String getTypeOfChest(String input) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT type FROM chest WHERE name = ? OR uuid = ?")) {
            preparedStatement.setString(1, input);
            preparedStatement.setString(2, input);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("type");
            }
            return null;
        }
    }

    public boolean chestIsEnabled(String input) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT enabled FROM chest WHERE name = ? OR uuid = ?")) {
            preparedStatement.setString(1, input);
            preparedStatement.setString(2, input);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                if(!chestExistsInDB(input)) {
                    Bukkit.getLogger().info("[87:36:55] The Chest " + input + " does not exists!");
                    return false;
                }
                return resultSet.getString("enabled").equals("true");
            }
            return false;
        }
    }

    public boolean chestExistsInDB(String input) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM chest WHERE name = ? OR uuid = ?")) {
            preparedStatement.setString(1, input);
            preparedStatement.setString(2, input);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    private boolean isTableEmpty(String tableName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT count(*) AS rowcount FROM " + tableName)){
            ResultSet resultSet = preparedStatement.executeQuery();
            int row = resultSet.getInt("rowcount");
            if(row == 0) {
                return true;
            }
            return false;
        }
    }

    public String getNameOfChest(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("name");
            }
            return null;
        }
    }

    public UUID getUuidByInput(String input) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid FROM chest WHERE uuid = ? OR name = ?")) {
            preparedStatement.setString(1, input);
            preparedStatement.setString(2, input);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("uuid"));
            }
            return null;
        }
    }

    public String getCurrentItem(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT item FROM currentitem WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("item");
            }
            return null;
        }
    }

    public void addItemToCurrentItem(UUID uuid, String item, Integer amount, Boolean completed, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO currentitem (uuid, item, amount, completed, holdingamount) VALUES (?, ? , ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, item);
            preparedStatement.setInt(3, amount);
            preparedStatement.setString(4, completed.toString());
            preparedStatement.setInt(5, 0);
            preparedStatement.executeUpdate();
        }
        addItemToItemsDB(uuid, item, amount, player);
    }

    public void addItemToItemsDB(UUID uuid, String item, Integer amount, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO items (uuid, item, requiredamount, holdingamount) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, item);
            preparedStatement.setInt(3, amount);
            preparedStatement.setInt(4,0);
            preparedStatement.executeUpdate();
        }

        this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.ITEM_ADD, uuid);
    }

    public void deleteItems(UUID uuid, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM items WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0) {
                Bukkit.getLogger().severe("[90:09:12] Removed all items from \"" + uuid + "\".");
                this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.ALL_ITEMS_REMOVED, uuid);
                return;
            }
            Bukkit.getLogger().warning("[90:66:55] \"" + uuid + "\" has no items!");
        }
    }

    public boolean getChestEnabled(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT enabled FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("enabled").equals("true");
            }
            return false;
        }
    }

    public void changeEnabledOfChest(UUID uuid, boolean enabled, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE chest SET enabled = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, String.valueOf(enabled));
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        }
        if(enabled) {
            this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_ENABLED, uuid);
        } else {
            this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_DISABLED, uuid);
        }
    }

    public void saveAction(String timestamp, Player player, SrAction action, UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO actionhistory (uuid, timestamp, player, action) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, timestamp);
            preparedStatement.setString(3, player.getName());
            preparedStatement.setString(4, action.getMessage());
            preparedStatement.executeUpdate();
        }
    }

    public Map<Integer, Quadruple> getLastActions() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM actionhistory ORDER BY id DESC LIMIT 20")) {
            ResultSet resultSet = preparedStatement.executeQuery();

            Map<Integer, Quadruple> map = new HashMap<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String uuid = resultSet.getString("uuid");
                String timestamp = resultSet.getString("timestamp");
                String playerName = resultSet.getString("player");
                String action = resultSet.getString("action");

                map.put(id, new Quadruple(uuid, timestamp, playerName, action));
            }
            return map;
        }
    }

    public HologramStyle getHologramStyle(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT hologramstyle FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return HologramStyle.valueOf(resultSet.getString("hologramstyle"));
            } else {
                return null;
            }
        }
    }
}
