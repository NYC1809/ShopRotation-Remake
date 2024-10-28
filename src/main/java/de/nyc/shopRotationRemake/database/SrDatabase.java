package de.nyc.shopRotationRemake.database;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.Messages;
import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.*;
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
                    "items TEXT NOT NULL," +
                    "requiredamount INTEGER NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS rewards (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT NOT NULL, " +
                    "items TEXT NOT NULL, " +
                    "amount INTEGER NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS chest (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "location TEXT NOT NULL, " +
                    "enabled TEXT NOT NULL, " +
                    "type TEXT NOT NULL, " +
                    "hologram TEXT NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS currentitem (" +
                   "uuid TEXT PRIMARY KEY, " +
                    "item TEXT NOT NULL, " +
                    "amount INT NOT NULL, " +
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
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void createChest(UUID uuid, String name, Location location, Boolean enabled, Material type, Boolean hologram) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO chest (uuid, name, location, enabled, type, hologram) VALUES (?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, location.toString());
            preparedStatement.setString(4,enabled.toString());
            preparedStatement.setString(5, type.toString());
            preparedStatement.setString(6, hologram.toString());
            preparedStatement.executeUpdate();
        }
    }

    public void processAllChestUuids(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid, name FROM chest GROUP BY uuid")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if(isTableEmpty("chest")) {
                Bukkit.getLogger().severe("The SQL-LITE table \"chest\" has no entries!");
                player.sendMessage(Messages.CHEST_DOES_NOT_EXISITS.getMessage());
                return;
            }
            player.sendMessage(Messages.GET_UUID_INFO_LINE_1.getMessage());
            main.getUuidList().clear();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String name = resultSet.getString("name");
                if(main.getUuidList().contains(uuid)) {
                    break;
                }
                main.getUuidList().add(uuid);
                Utils.coloredCopyToClipboard(player, uuid);

                //player.sendMessage("[23:29:43] " + uuid + " / " + name);
            }
        }
    }

    public void addFromDBtoList() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid FROM chest GROUP BY uuid")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if(isTableEmpty("chest")) {
                Bukkit.getLogger().severe("[28:10:01:26] SQL-Table \"chest\" is empty!");
                return;
            }
            main.getUuidList().clear();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                if(main.getUuidList().contains(uuid)) {
                    break;
                }
                main.getUuidList().add(uuid);
            }
        }
    }

    public void deleteChestByUuid(String input) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM chest WHERE name = ? OR uuid = ?")) {
            preparedStatement.setString(1, input);
            preparedStatement.setString(2, input);

            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0) {
                Bukkit.getLogger().severe("[28:98:12] Removed entry from SQL - DB!");
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
                Bukkit.getLogger().info("[65:87:14] Location doesnt exists in DB!");
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

    public boolean chestIsEnabled(String input) throws SQLException{
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT enabled FROM chest WHERE name = ? OR uuid = ?")) {
            preparedStatement.setString(1, input);
            preparedStatement.setString(2, input);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                if(chestExistsInDB(input)) {
                    Bukkit.getLogger().info("[87:36:55] The Chest " + input + " does not exists!");
                    return false;
                }
                return resultSet.getString("enabled").equals("true");
            }
            return false;
        }
    }

    public boolean chestExistsInDB(String input) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, input);
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

}
