package de.nyc.shopRotationRemake.objects;

import de.leonheuer.mcguiapi.gui.GUI;
import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.Messages;
import de.nyc.shopRotationRemake.util.HologramUtils;
import de.nyc.shopRotationRemake.util.ItemUtils;
import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CurrentItem {
    private static final Main main = Main.getInstance();

    public static boolean calculateCurrentItem(UUID uuid) throws SQLException {
        if(!main.getSrDatabase().hasCurrentItem(uuid)) {
            return createNewCurrentItem(uuid);
        }
        UUID itemUuid = getCurrentItemUuid(uuid);
        if(itemUuid == null) {
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
            return false;
        }
        UUID nextItemUuid = UUID.fromString(possibleItems.getFirst());
        int holdingAmount = main.getSrDatabase().getholdingItemAmountByItemUuid(nextItemUuid);
        int requiredAmount = main.getSrDatabase().getrequiredItemAmountByItemUuid(nextItemUuid);
        String itemString = main.getSrDatabase().getItemStringByItemUuid(nextItemUuid);
        main.getSrDatabase().addItemToCurrentItems(uuid, nextItemUuid, itemString, requiredAmount, holdingAmount);
        HologramUtils.updateSpecificHologram(uuid);
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
                createNewCurrentItem(uuid);
                HologramUtils.updateSpecificHologram(uuid);
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
                main.getSrDatabase().addItemToCurrentItems(uuid, itemUuid, itemString, requiredAmount, holdingAmount);
                HologramUtils.updateSpecificHologram(uuid);
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

    public static void giveItemsToChest(UUID uuid, Player player, GUI gui) throws SQLException {
        UUID itemUuid = getCurrentItemUuid(uuid);
        if(itemUuid == null) {
            return;
        }

        int requiredAmount = main.getSrDatabase().getrequiredItemAmountByItemUuid(itemUuid);
        int holdingAmount = main.getSrDatabase().getholdingItemAmountByItemUuid(itemUuid);

        String targetItemString = getItemString(uuid);
        String targetItemName = ItemUtils.getItemName(targetItemString);
        Material targetItemMaterial = ItemUtils.getItemMaterial(targetItemString);
        List<String> targetItemDescription = ItemUtils.getItemDescription(targetItemString);
        Map<Enchantment, Integer> targetItemEnchantments = ItemUtils.getItemEnchantments(targetItemString);

        int maxPlayerItemLimit;
        boolean isItemLimitPercentageinUse = main.getSrDatabase().getItemLimitPercentage(uuid);
        int limitPerPerson = main.getSrDatabase().getItemLimit(uuid);

        if(isItemLimitPercentageinUse) {
            maxPlayerItemLimit = Utils.calculatePercentageAmount(requiredAmount, limitPerPerson);
        } else {
            maxPlayerItemLimit = limitPerPerson;
        }

        ItemStack requiredItemStack = ItemUtils.createItemStack(targetItemMaterial, targetItemName, targetItemEnchantments, targetItemDescription);

        int alreadyGivenAmount = main.getSrDatabase().getGivenAmountFromPlayer(uuid, itemUuid, player);

        if(alreadyGivenAmount >= maxPlayerItemLimit) {
            player.sendMessage(Messages.PLAYER_GIVE_ITEM_MAX_VALUE_REACHED.getMessage());
            return;
        }

        int amountOfItemsInPlayersInventory = 0;
        for(ItemStack item : player.getInventory().getContents()) {
            if(item == null) {
                continue;
            }
            boolean isItemEqual = ItemUtils.compareItemStacks(item, requiredItemStack);

            if(isItemEqual) {
                amountOfItemsInPlayersInventory += item.getAmount();
            }
        }
        if(amountOfItemsInPlayersInventory == 0) {
            return;
        }
        int amountOfNeededItems = requiredAmount - holdingAmount;
        int remainingItemsPlayerAllowedToGive = maxPlayerItemLimit - alreadyGivenAmount;

        int remainingItemsPlayerCanGive = Math.min(remainingItemsPlayerAllowedToGive, amountOfItemsInPlayersInventory);

        if(amountOfNeededItems <= 0) {
            return;
        }
        if(remainingItemsPlayerCanGive <= 0) {
            player.sendMessage(Messages.PLAYER_GIVE_ITEM_MAX_VALUE_REACHED.getMessage());
            return;
        }
        int amountOfRemovedItems;
        if(amountOfNeededItems > remainingItemsPlayerCanGive) {
            Utils.removeItemsFromInventory(player, targetItemString, remainingItemsPlayerCanGive);
            player.sendMessage(Messages.PLAYER_GIVE_ITEM_SUCCES.getMessage().replace("%number", String.valueOf(remainingItemsPlayerCanGive)));

            main.getSrDatabase().setholdingAmountByItemUuid(itemUuid, holdingAmount + remainingItemsPlayerCanGive);

            amountOfRemovedItems = remainingItemsPlayerCanGive;
        } else {
            //ItemGoal is finished:
            Utils.removeItemsFromInventory(player, targetItemString, amountOfNeededItems);
            player.sendMessage(Messages.PLAYER_GIVE_ITEM_SUCCES.getMessage().replace("%number", String.valueOf(amountOfNeededItems)));

            main.getSrDatabase().setholdingAmountByItemUuid(itemUuid, holdingAmount + amountOfNeededItems);

            amountOfRemovedItems = amountOfNeededItems;
        }

        int newAmount = alreadyGivenAmount + amountOfRemovedItems;

        main.getSrDatabase().addPlayerToHistory(uuid, itemUuid, targetItemString, Utils.createTimestamp(), player, amountOfRemovedItems);

        main.getSrDatabase().addGivenAmount(uuid, itemUuid, player, newAmount);
        Bukkit.getLogger().severe("[12:96:26] Added new addGivenAmount for player: " + newAmount);
        Bukkit.getLogger().severe("[28:59:19] Removed " + amountOfRemovedItems + " items from " + player.getName());

        for(Player viewingPlayers : gui.getViewersList()) {
            viewingPlayers.updateInventory();
            HologramUtils.updateSpecificHologram(uuid);
            Bukkit.getLogger().info("[28:63:97] Updated Inventory for " + viewingPlayers.getName());
        }
    }
}
