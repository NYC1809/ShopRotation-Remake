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
                    "uuid PRIMARY KEY, " +
                    "id INTEGER AUTOINCREMENT, " +
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
                player.sendMessage(Messages.NO_CHEST_EXISTING.getMessage());
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

    //TODO: Functions for the other three SQL tables
    //TODO: Check in functions if given args exist in SQL DB (null-check)

}
