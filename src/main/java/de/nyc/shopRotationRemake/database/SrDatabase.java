package de.nyc.shopRotationRemake.database;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.HologramStyle;
import de.nyc.shopRotationRemake.enums.SrAction;
import de.nyc.shopRotationRemake.objects.CurrentItem;
import de.nyc.shopRotationRemake.objects.Quadruple;
import de.nyc.shopRotationRemake.objects.Quintuple;
import de.nyc.shopRotationRemake.objects.Sextuple;
import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

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
                    "itemuuid TEXT NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "holdingamount INTEGER NOT NULL, " +
                    "requiredamount INTEGER NOT NULL," +
                    "enabled TEXT NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS rewards (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT NOT NULL, " +
                    "itemuuid TEXT NOT NULL, " +
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
                    "hologramstyle TEXT NOT NULL, " +
                    "itemlimit INTEGER NOT NULL, " +
                    "itemlimitpercentage TEXT NOT NULL, " +
                    "minimumamount INTEGER NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS currentitem (" +
                   "uuid TEXT PRIMARY KEY, " +
                    "itemuuid TEXT NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "amount INT NOT NULL, " +
                    "holdingamount INT NOT NULL)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT NOT NULL, " +
                    "itemuuid TEXT NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "timestamp TEXT NOT NULL, " +
                    "player TEXT NOT NULL, " +
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
                    "itemuuid TEXT NOT NULL, " +
                    "player TEXT NOT NULL, " +
                    "givenamount INTEGER)");
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS pendingrewards (" +
                    "playeruuid TEXT PRIMARY KEY, " +
                    "playername TEXT NOT NULL, " +
                    "itemuuid TEXT NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "amount INTEGER NOT NULL, " +
                    "minimumamount INTEGER NOT NULL)");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void createChest(UUID uuid, String name, Location location, Boolean enabled, Material type, Boolean hologram, Player player, HologramStyle style, Integer itemLimit, boolean percentage, Integer minimumAmount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO chest (uuid, name, location, enabled, type, hologram, hologramstyle, itemlimit, itemlimitpercentage, minimumamount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, location.toString());
            preparedStatement.setString(4,enabled.toString());
            preparedStatement.setString(5, type.toString());
            preparedStatement.setString(6, hologram.toString());
            preparedStatement.setString(7, style.getName());
            preparedStatement.setInt(8, itemLimit);
            preparedStatement.setString(9, String.valueOf(percentage));
            preparedStatement.setInt(10,  minimumAmount);
            preparedStatement.executeUpdate();
        }
        this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_CREATED, uuid);
    }

    public List<String> processAllChestUuids() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid, name FROM chest ORDER BY name")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if(isTableEmpty("chest")) {
                Bukkit.getLogger().severe("The SQL-LITE table \"chest\" has no entries!");
                return null;
            }

            main.getUuidList().clear();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
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

    public void addItemToItemsDB(UUID uuid,UUID itemuuid, String item, Integer amount, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO items (uuid, itemuuid, item, requiredamount, holdingamount, enabled) VALUES (?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, itemuuid.toString());
            preparedStatement.setString(3, item);
            preparedStatement.setInt(4, amount);
            preparedStatement.setInt(5,0);
            preparedStatement.setString(6, "true");
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
                this.main.getSrDatabase().deleteAllRewardsByAllItemsDeletion(uuid);
                this.main.getSrDatabase().removeDBPlayerEntryAfterAllItemsDeletion(uuid);
                CurrentItem.deleteCurrentItemAll(uuid);
                return;
            }
            Bukkit.getLogger().warning("[90:66:55] \"" + uuid + "\" had no items!");
        }

    }

    public void deleteItemByItemUuid(UUID uuid, UUID itemuuid, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM items WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemuuid.toString());
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0) {
                Bukkit.getLogger().severe("[76:58:29] Removed item with the itemuuid: \"" + itemuuid + "\".");
                this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.ITEM_REMOVED, uuid);
                this.main.getSrDatabase().deleteAllRewardsByItemDeletion(itemuuid);
                this.main.getSrDatabase().removeDBPlayerEntryAfterItemDeletion(itemuuid);

                CurrentItem.deleteCurrentItem(uuid, itemuuid);
                return;
            }
            Bukkit.getLogger().warning("[76:89:16] \"" + itemuuid + "\" was not found in the table!");
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

    public void changeEnabledOfItem(UUID uuid, UUID itemuuid, boolean enabled, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE items SET enabled = ? WHERE itemuuid = ?")) {
            preparedStatement.setString(1, String.valueOf(enabled));
            preparedStatement.setString(2, itemuuid.toString());
            preparedStatement.executeUpdate();
        }
        if(enabled) {
            this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.ITEM_ENABLED, uuid);
            CurrentItem.updateCurrentItem(itemuuid);
        } else {
            this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.ITEM_DISABLED, uuid);
            CurrentItem.updateCurrentItem(itemuuid);
        }
    }

    public boolean getEnabledOfItem(UUID itemuuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT enabled FROM items WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemuuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getString("enabled").equals("true");
            }
            return false;
        }
    }

    public void changeNameOfChest(UUID uuid, String name, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE chest SET name = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        }
        this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_TITLE_CHANGED, uuid);
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

    public String getHologramStyle(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT hologramstyle FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getString("hologramstyle");
            } else {
                return null;
            }
        }
    }

    public void disableAllChests(UUID uuid, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE chest SET enabled = ?")) {
            preparedStatement.setString(1, "false");
            preparedStatement.executeUpdate();

            this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_ALL_DISABLED, uuid);
        }
    }

    public void enableAllChests(UUID uuid, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE chest SET enabled = ?")) {
            preparedStatement.setString(1, "true");
            preparedStatement.executeUpdate();

            this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_ALL_ENABLED, uuid);
        }
    }

    public void setTypeOfChest(UUID uuid, Player player, String material) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE chest SET type = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, material);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        }
        this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_TYPE_CHANGED, uuid);
    }

    public String getItemStringByItemUuid(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT item FROM items WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemUuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getString("item");
            } else {
                return null;
            }
        }
    }

    public Integer getrequiredItemAmountByItemUuid(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT requiredamount FROM items WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemUuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt("requiredamount");
            } else {
                return null;
            }
        }
    }

    public Integer getholdingItemAmountByItemUuid(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT holdingamount FROM items WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemUuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt("holdingamount");
            } else {
                return null;
            }
        }
    }

    public void setrequiredAmountByItemUuid(UUID itemUuid, Integer amount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE items SET requiredamount = ? WHERE itemuuid = ?")) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, itemUuid.toString());
            preparedStatement.executeUpdate();
        }
        CurrentItem.updateCurrentItem(itemUuid);
    }

    public void setholdingAmountByItemUuid(UUID itemUuid, Integer amount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE items SET holdingamount = ? WHERE itemuuid = ?")) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, itemUuid.toString());
            preparedStatement.executeUpdate();
        }
        CurrentItem.updateCurrentItem(itemUuid);
    }

    public Integer getAmountOfItemsOfChest(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS row_count FROM items WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt("row_count");
            } else {
                return 0;
            }
        }
    }

    public List<String> getListOfItems(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM items WHERE uuid = ? ORDER BY id ASC LIMIT 28")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> itemListWithItemUuids = new ArrayList<>();
            while (resultSet.next()) {
                String itemuuid = resultSet.getString("itemuuid");
                itemListWithItemUuids.add(itemuuid);
                //Bukkit.getLogger().warning("[DEBUG: 23:56:22] added item " + itemuuid + " to list.");
            }
            return itemListWithItemUuids;
        }
    }

    public void deleteAllRewardsOfItem(UUID uuid, UUID itemUuid, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM rewards WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemUuid.toString());
            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0) {
                Bukkit.getLogger().severe("[28:28:00] Removed all rewards from SQL - DB!");
                this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.REWARD__ALL_REMOVED, uuid);
                return;
            }
            Bukkit.getLogger().warning("[13:99:12] No entry found to remove from rewards SQL - DB!");
        }
    }

    public void addReward(UUID uuid, UUID itemUuid, String item, Integer amount, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO rewards (uuid, itemuuid, item, amount) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, itemUuid.toString());
            preparedStatement.setString(3, item);
            preparedStatement.setInt(4, amount);
            preparedStatement.executeUpdate();
        }
        this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.REWARD_ADD, uuid);
    }

    public List<Integer> getIdsFromItemUuidRewards(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM rewards WHERE itemuuid = ? ORDER BY id ASC")) {
            preparedStatement.setString(1, itemUuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Integer> ids = new ArrayList<>();
            while (resultSet.next()) {
                ids.add(resultSet.getInt("id"));
            }
            return ids;
        }
    }

    public String getRewardsItemStringByRowID(Integer rowID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT item FROM rewards WHERE id = ?")) {
            preparedStatement.setInt(1, rowID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getString("item");
            }
            return null;
        }
    }

    private void deleteAllRewardsByItemDeletion(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM rewards WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemUuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    private void deleteAllRewardsByAllItemsDeletion(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM rewards WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    public void setAmountOfRewardByID(Integer rowID, Integer amount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE rewards SET amount = ? WHERE id = ?")) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setInt(2, rowID);
            preparedStatement.executeUpdate();
        }
    }

    public Integer getAmountOfRewardByID(Integer rowID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT amount FROM rewards WHERE id = ?")) {
            preparedStatement.setInt(1, rowID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt("amount");
            }
            return null;
        }
    }

    public void deleteRewardByRowID(Integer rowID, Player player, UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM rewards WHERE id = ?")) {
            preparedStatement.setInt(1, rowID);
            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0) {
                this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.REWARD_REMOVED, uuid);
            }
        }
    }

    public void setNewRewardsItemStringByRowID(Integer rowID, String item) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE rewards SET item = ? WHERE id = ?")) {
            preparedStatement.setString(1, item);
            preparedStatement.setInt(2, rowID);
            preparedStatement.executeUpdate();
        }
    }

    public void setHologramStyle(UUID uuid, String hologramStyle, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE chest SET hologramstyle = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, hologramStyle);
            preparedStatement.setString(2, uuid.toString());
            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0) {
                this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_HOLOGRAM_STYLE_CHANGED, uuid);
            }
        }
    }

    public void setHologramEnabled(UUID uuid, Boolean enabled, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE chest SET hologram = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, enabled.toString());
            preparedStatement.setString(2, uuid.toString());
            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0) {
                if(enabled) {
                    this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_HOLOGRAM_ENABLED, uuid);
                } else {
                    this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_HOLOGRAM_DISABLED, uuid);
                }
            }
        }
    }

    public boolean getHologramEnabled(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT hologram FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getString("hologram").equals("true");
            }
            return false;
        }
    }

    public boolean hasCurrentItem(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currentitem WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void removeCurrentItem(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM currentitem WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    public String getCurrentItemString(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT item FROM currentitem WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getString("item");
            }
            return null;
        }
    }

    public void addItemToCurrentItems(UUID uuid, UUID itemUuid, String item, Integer amount, Integer holdingAmount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO currentitem (uuid, itemuuid, item, amount, holdingamount) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, itemUuid.toString());
            preparedStatement.setString(3, item);
            preparedStatement.setInt(4, amount);
            preparedStatement.setInt(5, holdingAmount);
            preparedStatement.executeUpdate();
        }
    }

    public UUID getItemUuidFromCurrentItems(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT itemuuid FROM currentitem WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return UUID.fromString(resultSet.getString("itemuuid"));
            }
            return null;
        }
    }

    public UUID getChestUuidFromItemUuid(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid FROM items WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemUuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return UUID.fromString(resultSet.getString("uuid"));
            }
            return null;
        }
    }

    public UUID getItemUuidFromCurrentItemUuid(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT itemuuid FROM currentitem WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return UUID.fromString(resultSet.getString("itemuuid"));
            }
            return null;
        }
    }

    public boolean hasCurrentItemUuid(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currentitem WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemUuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public Integer getItemLimit(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT itemlimit FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt("itemlimit");
            }
            return 0;
        }
    }

    public void setItemLimit(UUID uuid, Integer itemLimit, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE chest SET itemlimit = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, itemLimit);
            preparedStatement.setString(2, uuid.toString());
            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0) {
                this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_ITEM_LIMIT_CHANGED, uuid);
            }
        }
    }

    public void setItemLimitPercentage(UUID uuid, boolean usePercentage, Player player) throws SQLException {
        try (PreparedStatement  preparedStatement = connection.prepareStatement("UPDATE chest SET itemlimitpercentage = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, String.valueOf(usePercentage));
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    public boolean getItemLimitPercentage(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT itemlimitpercentage FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getString("itemlimitpercentage").equals("true");
            }
            return false;
        }
    }

    public void addGivenAmount(UUID uuid, UUID itemUuid, Player player, Integer givenAmount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO player (uuid, itemuuid, player, givenamount) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, itemUuid.toString());
            preparedStatement.setString(3, player.getName());
            preparedStatement.setInt(4, givenAmount);
            preparedStatement.executeUpdate();
        }
    }

    public Integer getGivenAmountFromPlayerName(UUID uuid, UUID itemUuid, String playerName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT givenamount FROM player WHERE uuid = ? AND itemuuid = ? AND player = ?")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, itemUuid.toString());
            preparedStatement.setString(3, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt("givenamount");
            }
            return 0;
        }
    }

    public void removeDBPlayerEntryAfterItemDeletion(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM player WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemUuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    public void removeDBPlayerEntryAfterAllItemsDeletion(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM player WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    public Integer removeAllPlayersItemLimitCounterForItem(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM player WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemUuid.toString());
            int rowsAffected = preparedStatement.executeUpdate();

            return Math.max(rowsAffected, 0);
        }
    }

    public void addPlayerToHistory(UUID uuid, UUID itemUuid, String itemString, String timestamp, Player player, Integer amount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO history (uuid, itemuuid, item, timestamp, player, amount) VALUES (?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, itemUuid.toString());
            preparedStatement.setString(3, itemString);
            preparedStatement.setString(4, timestamp);
            preparedStatement.setString(5, player.getName());
            preparedStatement.setInt(6, amount);
            preparedStatement.executeUpdate();
        }
    }

    public Map<Integer, Sextuple> getPlayerHistoryOfItem(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM history WHERE itemuuid = ? ORDER BY id DESC LIMIT 35")) {
            preparedStatement.setString(1, itemUuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            Map<Integer, Sextuple> map = new HashMap<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String uuid = resultSet.getString("uuid");

                String itemString = resultSet.getString("item");
                String timestamp = resultSet.getString("timestamp");
                String playerName = resultSet.getString("player");
                Integer amount = resultSet.getInt("amount");

                map.put(id, new Sextuple(uuid, String.valueOf(itemUuid), itemString, timestamp, playerName, String.valueOf(amount)));
            }
            return map;
        }
    }

    public Map<Integer, Sextuple> getPlayerHistoryOfChest(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM history WHERE uuid = ? ORDER BY id DESC LIMIT 35")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            Map<Integer, Sextuple> map = new HashMap<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String itemUuid = resultSet.getString("itemuuid");
                String itemString = resultSet.getString("item");
                String timestamp = resultSet.getString("timestamp");
                String playerName = resultSet.getString("player");
                Integer amount = resultSet.getInt("amount");

                map.put(id, new Sextuple(String.valueOf(uuid), itemUuid, itemString, timestamp, playerName, String.valueOf(amount)));
            }
            return map;
        }
    }

    public Integer getMinimumAmountOfChest(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT minimumamount FROM chest WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getInt("minimumamount");
            }
        }
        return 0;
    }

    public void setMinimumAmountOfChest(UUID uuid, Integer minimumAmount, Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE chest SET minimumamount = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, minimumAmount);
            preparedStatement.setString(2, uuid.toString());
            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0) {
                this.main.getSrDatabase().saveAction(Utils.createTimestamp(), player, SrAction.CHEST_ITEM_MINIMUM_REQUIREMENT_CHANGED, uuid);
            }
        }
    }

    public void addPendingRewardEntry(Player player, UUID itemUuid, String itemString, Integer amount, Integer minimumAmount) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO pendingrewards (playeruuid, playername, itemuuid, item, amount, minimumamount) VALUES (?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, String.valueOf(player.getUniqueId()));
            preparedStatement.setString(2, player.getName());
            preparedStatement.setString(3, itemUuid.toString());
            preparedStatement.setString(4, itemString);
            preparedStatement.setInt(5, amount);
            preparedStatement.setInt(6, minimumAmount);
            preparedStatement.executeUpdate();
        }
    }
    public Map<UUID, Quintuple> getAllPendingRewardEntries() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM pendingrewards")) {
            ResultSet resultSet = preparedStatement.executeQuery();

            Map<UUID, Quintuple> entryMap = new HashMap<>();

            while (resultSet.next()) {
                String playeruuid = resultSet.getString("playeruuid");
                String playerName = resultSet.getString("playername");
                String itemuuid = resultSet.getString("itemuuid");
                String itemString = resultSet.getString("item");
                Integer amount = resultSet.getInt("amount");
                Integer minimumamount = resultSet.getInt("minimumamount");

                Quintuple values = new Quintuple(playerName, itemuuid, itemString, String.valueOf(amount), String.valueOf(minimumamount));

                entryMap.put(UUID.fromString(playeruuid), values);
            }
            return entryMap;
        }
    }

    public List<String> getAllPlayersFromSpecificItem(UUID itemUuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT player FROM player WHERE itemuuid = ?")) {
            preparedStatement.setString(1, itemUuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> playerList = new ArrayList<>();

            while(resultSet.next()) {
                String playerName = resultSet.getString("player");
                playerList.add(playerName);
            }
            return playerList;
        }
    }
}
