package de.nyc.shopRotationRemake.util;

import de.leonheuer.mcguiapi.gui.GUI;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.ItemDescription;
import de.nyc.shopRotationRemake.enums.Messages;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class RewardsInventory {

    private static final Main main = Main.getInstance();

    public static void openRewardsInventory(Player player, UUID uuid, UUID itemUuid) throws SQLException {
        //&TODO: permission system
        if(!player.isOp()) {
            player.sendMessage(Messages.NO_PERMS_ERROR.getMessage());
            return;
        }
        GUI gui = main.getGuiFactory().createGUI(6, Utils.setColorInMessage("&6Belohnungen für: &a" + itemUuid));

        //START OF CREATING GLASS BORDER
        for (int i=0; i<54; i++) {
            if(i>0 && i <8) { continue; }
            if(i>9 && i <17) { continue; }
            if(i>18 && i <26) { continue; }
            if(i>27 && i <35) { continue; }

            gui.setItem(i, ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItem());
        }
        //Set the pink glass:
        gui.setItem(2, ItemBuilder.of(Material.PINK_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(11, ItemBuilder.of(Material.PINK_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(20, ItemBuilder.of(Material.PINK_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(29, ItemBuilder.of(Material.PINK_STAINED_GLASS_PANE).name(" ").asItem());
        //END OF CREATING GLASS BORDER

        String itemString = main.getSrDatabase().getItemString(itemUuid);
        Material itemMaterial = ItemUtils.getItemMaterial(itemString);
        if(itemMaterial == null) {
            itemMaterial = Material.BARRIER;
        }
        //Back to the item gui - button:
        gui.setItem(45, ItemBuilder.of(itemMaterial).name(ItemDescription.ITEM_EXIT_REWARDS_ITEM.getText()).description(ItemDescription.ITEM_EXIT_REWARDS_ITEM_LORE_1.getText(), ItemDescription.ITEM_EXIT_REWARDS_ITEM_LORE_2.getText()).asItem(), event -> {
            try {
                InventoryManager.modifyItemInventory(player, uuid, itemUuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //Delete all rewards item:
        gui.setItem(51, ItemBuilder.of(Material.REDSTONE_BLOCK).name(ItemDescription.ITEM_REWARDS_REMOVE_ALL.getText()).description(ItemDescription.ITEM_REWARDS_REMOVE_ALL_LORE_1.getText(), ItemDescription.ITEM_REWARDS_REMOVE_ALL_LORE_2.getText()).asItem(), event -> {
            try {
                removeAllRewards(player, uuid, itemUuid);
                openRewardsInventory(player, uuid, itemUuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //Set add new reward Item:
        gui.setItem(48, ItemBuilder.of(Material.CRAFTER).name(ItemDescription.REWARDS_ADD_NEW_ITEM.getText()).description(ItemDescription.REWARDS_ADD_NEW_ITEM_LORE_1.getText(), ItemDescription.REWARDS_ADD_NEW_ITEM_LORE_2.getText()).asItem(), event -> {
            try {
                inputNewReward(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier das &6Item &ean, welches hinzugefügt werden soll..."));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //SET COMING SOON ITEM:
        gui.setItem(47, ItemBuilder.of(Material.GRAY_DYE).name(ItemDescription.ITEM_COMING_SOON.getText()).asItem());
        gui.setItem(50, ItemBuilder.of(Material.GRAY_DYE).name(ItemDescription.ITEM_COMING_SOON.getText()).asItem());

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.show(player);
    }

    private static void removeAllRewards(Player player, UUID uuid, UUID itemUuid) throws SQLException {
        main.getSrDatabase().deleteAllRewardsOfItem(uuid, itemUuid, player);
        player.sendMessage(Messages.All_REWARDS_REMOVED.getMessage().replace("%itemuuid", itemUuid.toString()));
    }

    private static void inputNewReward(Player player, UUID uuid, UUID itemUuid, String title) throws SQLException {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    try {
                        openRewardsInventory(player, uuid, itemUuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String input = stateSnapshot.getText();
                    if(input.equals("Material.")) {
                        player.sendMessage(Messages.ADD_REWARD_CANCEL.getMessage());
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    if(!Utils.isMaterial(input)) {
                        player.sendMessage(Messages.MATERIAL_WRONG.getMessage().replace("%input", "Material." + input));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Material."));
                    }
                    Material material = Utils.getMaterialType(input);
                    String item = ItemUtils.createItemString(material.name(), material, null, null);
                    try {
                        main.getSrDatabase().addReward(uuid, itemUuid, item, 1, player);
                        player.sendMessage(Messages.REWARD_ADDED_SUCCESS.getMessage().replace("%item", "Material." + input));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .preventClose()
                .text("Material.")
                .title(title)
                .plugin(main)
                .open(player);
    }
}
