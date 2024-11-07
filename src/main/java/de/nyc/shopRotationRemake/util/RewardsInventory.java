package de.nyc.shopRotationRemake.util;

import de.leonheuer.mcguiapi.gui.GUI;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.ItemDescription;
import de.nyc.shopRotationRemake.enums.Messages;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.*;

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

        //Set addthis item:
        gui.setItem(49, ItemBuilder.of(Material.BRUSH).name(ItemDescription.REWARD_ADD_ITEM_BY_DRAG.getText()).description(ItemDescription.REWARD_ADD_ITEM_BY_DRAG_LORE_1.getText(), ItemDescription.REWARD_ADD_ITEM_BY_DRAG_LORE_2.getText()).asItem(), event -> {
            ItemStack itemOnCursor = event.getCursor();

            if(itemOnCursor != null) {
                if(!itemOnCursor.getType().equals(Material.AIR)) {
                    if(itemOnCursor.hasItemMeta()) {
                        ItemMeta itemMeta = itemOnCursor.getItemMeta();
                        if(itemMeta != null) {
                            String displayName;
                            if(itemMeta.getDisplayName().isEmpty()) {
                                displayName = itemOnCursor.getType().name();
                            } else {
                                displayName = itemMeta.getDisplayName();
                            }
                            Material material = itemOnCursor.getType();
                            Map<Enchantment, Integer> enchantmentList = null;
                            List<String> itemLore = null;

                            if(itemMeta.hasEnchants()) {
                                enchantmentList = itemMeta.getEnchants();
                            }
                            if(itemMeta.hasLore()) {
                                itemLore = itemMeta.getLore();
                            }
                            String itemOnCursorString = ItemUtils.createItemString(displayName, material, enchantmentList, itemLore);
                            try {
                                main.getSrDatabase().addReward(uuid, itemUuid, itemOnCursorString, 1, player);
                                player.sendMessage(Messages.REWARD_ADDED_SUCCESS.getMessage().replace("%item", displayName));
                                player.sendMessage(Messages.REWARD_MODIFICATE_FOR_CHANGES.getMessage());
                                openRewardsInventory(player, uuid, itemUuid);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                }
            }
        });

        //Set the rewards to the gui:
        List<Integer> rowIDs = main.getSrDatabase().getIdsFromItemUuidRewards(itemUuid);
        //First reward:
        if(!rowIDs.isEmpty()) {
            Integer firstID = rowIDs.getFirst();
            ItemStack firstItem = createRewardItemStack(firstID);
            gui.setItem(1, firstItem);

            Integer firstAmount = main.getSrDatabase().getAmountOfRewardByID(firstID);
            //Set the changeAmount Item for the first reward
            gui.setItem(3, ItemBuilder.of(Material.WRITABLE_BOOK).name(ItemDescription.REWARDS_CHANGE_AMOUNT.getText()).description(ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_1.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_2.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_3.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_4.getText().replace("%number", String.valueOf(firstAmount))).asItem(), event -> {
                try {
                    changeAmountOfReward(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Wert ein..."), firstID, firstAmount);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the delete this reward item for the first reward
            gui.setItem(7, ItemBuilder.of(Material.REDSTONE).name(ItemDescription.REWARDS_ITEM_DELETE.getText()).description(ItemDescription.REWARDS_ITEM_DELETE_LORE_1.getText(), ItemDescription.REWARDS_ITEM_DELETE_LORE_2.getText()).asItem(), event -> {
                try {
                    main.getSrDatabase().deleteRewardByRowID(firstID, player, uuid);
                    player.sendMessage(Messages.REWARD_REMOVED_SUCCESS.getMessage());
                    openRewardsInventory(player, uuid, itemUuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            String firstItemString = main.getSrDatabase().getRewardsItemStringByRewardID(firstID);
            String firstItemName = ItemUtils.getItemName(firstItemString);

            //Set the change name item for the first reward
            gui.setItem(5, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_NAME.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_2.getText()).asItem(), event -> {
                try {
                    changeItemName(player, uuid, itemUuid, Utils.setColorInMessage("&eNeuen &6Namen &eeingeben..."), firstItemName, firstID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        //Second reward:
        if(rowIDs.size() >= 2) {
            Integer secondID = rowIDs.get(1);
            ItemStack secondItem = createRewardItemStack(secondID);
            gui.setItem(10, secondItem);

            Integer secondAmount = main.getSrDatabase().getAmountOfRewardByID(secondID);
            //Set the changeAmount Item for the second reward
            gui.setItem(12, ItemBuilder.of(Material.WRITABLE_BOOK).name(ItemDescription.REWARDS_CHANGE_AMOUNT.getText()).description(ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_1.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_2.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_3.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_4.getText().replace("%number", String.valueOf(secondAmount))).asItem(), event -> {
                try {
                    changeAmountOfReward(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Wert ein..."), secondID, secondAmount);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the delete this reward item for the second reward
            gui.setItem(16, ItemBuilder.of(Material.REDSTONE).name(ItemDescription.REWARDS_ITEM_DELETE.getText()).description(ItemDescription.REWARDS_ITEM_DELETE_LORE_1.getText(), ItemDescription.REWARDS_ITEM_DELETE_LORE_2.getText()).asItem(), event -> {
                try {
                    main.getSrDatabase().deleteRewardByRowID(secondID, player, uuid);
                    player.sendMessage(Messages.REWARD_REMOVED_SUCCESS.getMessage());
                    openRewardsInventory(player, uuid, itemUuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            String secondItemString = main.getSrDatabase().getRewardsItemStringByRewardID(secondID);
            String secondItemName = ItemUtils.getItemName(secondItemString);

            //Set the change name item for the second reward
            gui.setItem(14, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_NAME.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_2.getText()).asItem(), event -> {
                try {
                    changeItemName(player, uuid, itemUuid, Utils.setColorInMessage("&eNeuen &6Namen &eeingeben..."), secondItemName, secondID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        //Third reward:
        if(rowIDs.size() >= 3) {
            Integer thirdID = rowIDs.get(2);
            ItemStack thirdItem = createRewardItemStack(thirdID);
            gui.setItem(19, thirdItem);

            Integer thirdAmount = main.getSrDatabase().getAmountOfRewardByID(thirdID);
            //Set the changeAmount Item for the third reward
            gui.setItem(21, ItemBuilder.of(Material.WRITABLE_BOOK).name(ItemDescription.REWARDS_CHANGE_AMOUNT.getText()).description(ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_1.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_2.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_3.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_4.getText().replace("%number", String.valueOf(thirdAmount))).asItem(), event -> {
                try {
                    changeAmountOfReward(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Wert ein..."), thirdID, thirdAmount);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the delete this reward item for the third reward
            gui.setItem(25, ItemBuilder.of(Material.REDSTONE).name(ItemDescription.REWARDS_ITEM_DELETE.getText()).description(ItemDescription.REWARDS_ITEM_DELETE_LORE_1.getText(), ItemDescription.REWARDS_ITEM_DELETE_LORE_2.getText()).asItem(), event -> {
                try {
                    main.getSrDatabase().deleteRewardByRowID(thirdID, player, uuid);
                    player.sendMessage(Messages.REWARD_REMOVED_SUCCESS.getMessage());
                    openRewardsInventory(player, uuid, itemUuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            String thirdItemString = main.getSrDatabase().getRewardsItemStringByRewardID(thirdID);
            String thirdItemName = ItemUtils.getItemName(thirdItemString);

            //Set the change name item for the third reward
            gui.setItem(23, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_NAME.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_2.getText()).asItem(), event -> {
                try {
                    changeItemName(player, uuid, itemUuid, Utils.setColorInMessage("&eNeuen &6Namen &eeingeben..."), thirdItemName, thirdID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        //Fourth reward:
        if(rowIDs.size() >= 4) {
            Integer fourthID = rowIDs.get(3);
            ItemStack fourthItem = createRewardItemStack(fourthID);
            gui.setItem(28, fourthItem);

            Integer fourthAmount = main.getSrDatabase().getAmountOfRewardByID(fourthID);

            //Set the changeAmount Item for the fourth reward
            gui.setItem(30, ItemBuilder.of(Material.WRITABLE_BOOK).name(ItemDescription.REWARDS_CHANGE_AMOUNT.getText()).description(ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_1.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_2.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_3.getText(), ItemDescription.REWARDS_CHANGE_AMOUNT_LORE_4.getText().replace("%number", String.valueOf(fourthAmount))).asItem(), event -> {
                try {
                    changeAmountOfReward(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Wert ein..."), fourthID, fourthAmount);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the delete this reward item for the fourth reward
            gui.setItem(34, ItemBuilder.of(Material.REDSTONE).name(ItemDescription.REWARDS_ITEM_DELETE.getText()).description(ItemDescription.REWARDS_ITEM_DELETE_LORE_1.getText(), ItemDescription.REWARDS_ITEM_DELETE_LORE_2.getText()).asItem(), event -> {
                try {
                    main.getSrDatabase().deleteRewardByRowID(fourthID, player, uuid);
                    player.sendMessage(Messages.REWARD_REMOVED_SUCCESS.getMessage());
                    openRewardsInventory(player, uuid, itemUuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            String fourthItemString = main.getSrDatabase().getRewardsItemStringByRewardID(fourthID);
            String fourthItemName = ItemUtils.getItemName(fourthItemString);

            //Set the change name item for the fourth reward
            gui.setItem(23, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_NAME.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_2.getText()).asItem(), event -> {
                try {
                    changeItemName(player, uuid, itemUuid, Utils.setColorInMessage("&eNeuen &6Namen &eeingeben..."), fourthItemName, fourthID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        if(rowIDs.size() > 4) {
            player.sendMessage(Messages.TOO_MANY_REWARDS.getMessage());
        }

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

    private static ItemStack createRewardItemStack(Integer rowID) throws SQLException {
        String itemString = main.getSrDatabase().getRewardsItemStringByRewardID(rowID);

        Material material = ItemUtils.getItemMaterial(itemString);
        String itemName = ItemUtils.getItemName(itemString);
        List<String> itemDescription = ItemUtils.getItemDescription(itemString);

        Map<Enchantment, Integer> itemEnchantmentMap = ItemUtils.getItemEnchantments(itemString);

        Integer amount = main.getSrDatabase().getAmountOfRewardByID(rowID);

        return createRewardDescription(material, itemName, itemEnchantmentMap, itemDescription, amount);
    }

    private static ItemStack createRewardDescription(Material material, String itemName, Map<Enchantment, Integer> itemEnchantmentMap, List<String> itemDescription, Integer amount) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.setColorInMessage("&dBelohnung: "));

        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("&3Aktuelle Item Properties:");
        lore.add(" ");
        lore.add("  &9» [&bName&9] &d" + itemName);
        if(itemDescription == null) {
            lore.add("  &9» [&bDescription&9] &d" + "[ NONE ]");
        } else {
            lore.add("  &9» [&bDescription&9] &d");
            for(String line : itemDescription) {
                lore.add("               &9» &f" + line + "&r");
            }
        }
        if(itemEnchantmentMap.isEmpty()) {
            lore.add("  &9» [&bEnchantments&9] &d" + "[ NONE ]");
        } else {
            lore.add("  &9» [&bEnchantments&9] &d");
            for(Map.Entry<Enchantment, Integer> entry : itemEnchantmentMap.entrySet()) {
                Enchantment enchantment = entry.getKey();
                Integer level = entry.getValue();
                lore.add("               &9» &b" + enchantment.toString() + " &dLevel: &3" + level);
            }
        }
        lore.add(" ");
        lore.add("  &9» [&bAnzahl des Items&9] &d" + amount);
        lore.add(" ");

        itemMeta.setLore(Utils.setColorInList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

    private static void changeAmountOfReward(Player player, UUID uuid, UUID itemuuid, String title, Integer rowID, Integer currentAmount) throws SQLException {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    try {
                        openRewardsInventory(player, uuid, itemuuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String input = stateSnapshot.getText();
                    if(!Utils.isNumeric(input)) {
                        player.sendMessage(Messages.IS_NOT_NUMERIC.getMessage().replace("%input", input));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText(currentAmount.toString()));
                    }
                    try {
                        main.getSrDatabase().setAmountOfRewardByID(rowID, Integer.parseInt(input));
                        player.sendMessage(Messages.REWARD_AMOUNT_CHANGED_SUCCESS.getMessage().replace("%number", input));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .preventClose()
                .text(currentAmount.toString())
                .title(title)
                .plugin(main)
                .open(player);
    }

    private static void changeItemName(Player player, UUID uuid, UUID itemUuid, String title, String itemName, Integer rowID) throws SQLException {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    try {
                        openRewardsInventory(player, uuid, itemUuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onClick((slot, statesnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String input = statesnapshot.getText();
                    if(input.equals(itemName.replace("§", "&"))) {
                        player.sendMessage(Messages.REWARD_ITEM_CHANGE_NAME_CANCEL.getMessage().replace("%name", input));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    try {
                        String itemString = main.getSrDatabase().getRewardsItemStringByRewardID(rowID);
                        Material material = ItemUtils.getItemMaterial(itemString);
                        List<String> itemDescription = ItemUtils.getItemDescription(itemString);
                        Map<Enchantment, Integer> itemEnchantmentMap = ItemUtils.getItemEnchantments(itemString);
                        String newItemString = ItemUtils.createItemString(Utils.setColorInMessage(input), material, itemEnchantmentMap, itemDescription);

                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_ITEM_CHANGE_NAME_SUCCESS.getMessage().replace("%name", input));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .preventClose()
                .text(itemName.replace("§", "&"))
                .title(title)
                .plugin(main)
                .open(player);
    }
}
