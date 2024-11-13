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

    public static void updateCurrentItem(UUID itemUuid) throws SQLException {
        UUID uuid = main.getSrDatabase().getChestUuidFromItemUuid(itemUuid);

        if(main.getSrDatabase().hasCurrentItemUuid(itemUuid)) {
            //Item with itemUuid got changed -> writing new currentitem:
            boolean enabled = main.getSrDatabase().getEnabledOfItem(itemUuid);
            if(!enabled) {
                main.getSrDatabase().removeCurrentItem(uuid);
                Bukkit.getLogger().warning("[28:62:01] item with itemuuid \"" + itemUuid + "\" got disabled -> removing currentitem");
                return;
            }
            Bukkit.getLogger().warning("[32:16:19] Item with itemUuid got changed -> writing new currentitem:");
            main.getSrDatabase().removeCurrentItem(uuid);
            int requiredAmount = main.getSrDatabase().getrequiredItemAmountByItemUuid(itemUuid);
            int holdingAmount = main.getSrDatabase().getholdingItemAmountByItemUuid(itemUuid);
            if(holdingAmount >= requiredAmount) {
                createNewCurrentItem(uuid);
            } else {
                String itemString = main.getSrDatabase().getItemStringByItemUuid(itemUuid);
                main.getSrDatabase().addItemToCurrentItems(uuid, itemUuid, itemString, requiredAmount, holdingAmount, false);
            }
        }
    }

    public static void deleteCurrentItem(UUID uuid, UUID itemUuid) throws SQLException {
        if(!main.getSrDatabase().hasCurrentItemUuid(itemUuid)) {
            Bukkit.getLogger().warning("[89:29:19] an item got deleted but its not matching with the currentitem!");
            return;
        }

        if(main.getSrDatabase().hasCurrentItem(uuid)) {
            main.getSrDatabase().removeCurrentItem(uuid);
            Bukkit.getLogger().warning("[22:33:02] itemUuid got deleted -> deleting currentItem aswell!");
        }
    }

    public static void deleteCurrentItemAll(UUID uuid) throws SQLException {
        if(main.getSrDatabase().hasCurrentItem(uuid)) {
            main.getSrDatabase().removeCurrentItem(uuid);
            Bukkit.getLogger().info("[11:99:01] All items deleted from \"" + uuid + "\"! deleted currentitem aswell!");
        }
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
            Bukkit.getLogger().severe("[45:12:11] nextItemUuid is null!");
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
