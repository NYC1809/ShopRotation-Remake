package de.nyc.shopRotationRemake.database;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class SrDatabase {

    private final Connection connection;

    public SrDatabase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS items (" +
                    "id TEXT PRIMARY KEY, " +
                    "items TEXT)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS rewards (" +
                    "id TEXT PRIMARY KEY, " +
                    "items TEXT NOT NULL, " +
                    "FOREIGN KEY(id) REFERENCES items(id))");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS chest (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "location TEXT NOT NULL, " +
                    "enabled TEXT NOT NULL, " +
                    "FOREIGN KEY(uuid) REFERENCES items(id))");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS currentitem (" +
                   "uuid TEXT PRIMARY KEY, " +
                    "item TEXT NOT NULL, " +
                    "amount INT NOT NULL, " +
                    "alreadygifted INT NOT NULL, " +
                    "completed TEXT, " +
                    "FOREIGN KEY(uuid) REFERENCES chest(uuid))");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayer(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, username) VALUES (?, ?)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        }
    }

    public boolean playerExists(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void createChest(UUID uuid, String name, Location location, Boolean enabled) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO chest (uuid, name, location, enabled) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, location.toString());
            preparedStatement.setString(4,enabled.toString());
            preparedStatement.executeUpdate();
        }
    }

    public String getChestNameByUUID(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.toString();
        }
    }

    public String getChestLocationByUUID(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT location FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.toString();
        }
    }

    public boolean getChestIsEnabledByUUID(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT enabled FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); //TODO: ALL RESULTSETS ARE WRONG!! USE METHOD FROM FUNCTION BELOW:
        }
    }

    public String getChestIsEnabledByName(String name) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT enabled FROM chest WHERE name = ?")) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("enabled");
            }
            return null;
        }
    } //TODO:^^THIS FUNCTION IS CORRECT

    public String getChestUuids(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(DISTINCT uuid), name FROM chest")) {
            //DEBUG: PLAYER-SEND-MESSAGE
            ResultSet resultSet = preparedStatement.executeQuery();
            player.sendMessage("[12:35:28] " + resultSet.toString());
            return resultSet.toString(); //DEBUG
        }
    }

    //TODO: Functions for the other three SQL tables
    //TODO: Check in functions if given args exist in SQL DB

}
