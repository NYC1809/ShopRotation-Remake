package de.nyc.shopRotationRemake.objects;

import de.nyc.shopRotationRemake.Main;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurrentItem {
    private static final Main main = Main.getInstance();

    public static boolean calculateCurrentItem(UUID uuid) throws SQLException {
        if(!main.getSrDatabase().hasCurrentItem(uuid)) {
            Bukkit.getLogger().severe("[45:12243:16] Chest has no currentitem yet - generating one!");
            return createNewCurrentItem(uuid);
        }
        if(main.getSrDatabase().currentItemIsCompleted(uuid)) {
            main.getSrDatabase().removeCurrentItem(uuid);
            return createNewCurrentItem(uuid);
        }
        UUID itemUuid = getCurrentItemUuid(uuid);
        if(itemUuid == null) {
            Bukkit.getLogger().severe("[45:12:16123] nextItemUuid is null!");
            return false;
        }
        int requiredAmount = main.getSrDatabase().getrequiredItemAmountByItemUuid(itemUuid);
        int holdingAmount = main.getSrDatabase().getholdingItemAmountByItemUuid(itemUuid);
        if(holdingAmount >= requiredAmount) {
            main.getSrDatabase().removeCurrentItem(uuid);
            return createNewCurrentItem(uuid);
        }
        return true;
    }

    public static boolean createNewCurrentItem(UUID uuid) throws SQLException {
        List<String> itemList = main.getSrDatabase().getListOfItems(uuid);
        List<String> possibleItems = new ArrayList<>();

        if(itemList == null || itemList.isEmpty()) {
            Bukkit.getLogger().severe("[25:88:91] No possible currentitem!");
            return false;
        }

        for(String itemUuid : itemList) {
            if(main.getSrDatabase().getEnabledOfItem(UUID.fromString(itemUuid))) {
                int requiredAmount = main.getSrDatabase().getrequiredItemAmountByItemUuid(UUID.fromString(itemUuid));
                int holdingAmount = main.getSrDatabase().getholdingItemAmountByItemUuid(UUID.fromString(itemUuid));
                if(holdingAmount < requiredAmount) {
                    possibleItems.add(itemUuid);
                }
            }
        }
        if(possibleItems.isEmpty()) {
            Bukkit.getLogger().severe("[25:88:91] No possible currentitem!");
            return false;
        }
        UUID nextItemUuid = UUID.fromString(possibleItems.getFirst());
        int holdingAmount = main.getSrDatabase().getholdingItemAmountByItemUuid(nextItemUuid);
        int requiredAmount = main.getSrDatabase().getrequiredItemAmountByItemUuid(nextItemUuid);
        String itemString = main.getSrDatabase().getItemStringByItemUuid(nextItemUuid);
        main.getSrDatabase().addItemToCurrentItems(uuid, nextItemUuid, itemString, requiredAmount, holdingAmount, false);
        return true;
    }

    public static String getItemString(UUID uuid) throws SQLException{
        if(!main.getSrDatabase().hasCurrentItem(uuid)) {
            createNewCurrentItem(uuid);
        }
        return main.getSrDatabase().getCurrentItemString(uuid);
    }

    public static UUID getCurrentItemUuid(UUID uuid) throws SQLException {
        if(!main.getSrDatabase().hasCurrentItem(uuid)) {
            createNewCurrentItem(uuid);
        }
        UUID itemUuid = main.getSrDatabase().getItemUuidFromCurrentItems(uuid);
        if(itemUuid == null) {
            Bukkit.getLogger().severe("[45:12:15 adf6] nextItemUuid is null!");
            return null;
        }
        return itemUuid;
    }

    public static Integer getRequiredAmount(UUID uuid) throws SQLException {
        if(!main.getSrDatabase().hasCurrentItem(uuid)) {
            createNewCurrentItem(uuid);
        }
        UUID itemUuid = getCurrentItemUuid(uuid);
        return main.getSrDatabase().getrequiredItemAmountByItemUuid(itemUuid);
    }

    public static Integer getHoldingAmount(UUID uuid) throws SQLException {
        if(!main.getSrDatabase().hasCurrentItem(uuid)) {
            createNewCurrentItem(uuid);
        }
        UUID itemUuid = getCurrentItemUuid(uuid);
        return main.getSrDatabase().getholdingItemAmountByItemUuid(itemUuid);
    }
}
