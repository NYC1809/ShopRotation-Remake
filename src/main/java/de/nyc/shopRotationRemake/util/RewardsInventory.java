package de.nyc.shopRotationRemake.util;

import de.leonheuer.mcguiapi.gui.GUI;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.ItemDescription;
import de.nyc.shopRotationRemake.enums.Messages;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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

        String itemString = main.getSrDatabase().getItemStringByItemUuid(itemUuid);
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
        gui.setItem(51, ItemBuilder.of(Material.REDSTONE_BLOCK).name(ItemDescription.ITEM_REWARDS_REMOVE_ALL.getText()).description(ItemDescription.ITEM_REWARDS_REMOVE_ALL_LORE_1.getText(), ItemDescription.ITEM_REWARDS_REMOVE_ALL_LORE_2.getText(), ItemDescription.ITEM_REWARDS_REMOVE_ALL_LORE_3.getText()).asItem(), event -> {
            if(event.getClick().equals(ClickType.DOUBLE_CLICK)) {
                try {
                    removeAllRewards(player, uuid, itemUuid);
                    openRewardsInventory(player, uuid, itemUuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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

            String firstItemString = main.getSrDatabase().getRewardsItemStringByRowID(firstID);
            String firstItemName = ItemUtils.getItemName(firstItemString);

            //Set the change name item for the first reward
            gui.setItem(5, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_NAME.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_2.getText()).asItem(), event -> {
                try {
                    changeItemName(player, uuid, itemUuid, Utils.setColorInMessage("&eNeuen &6Namen &eeingeben..."), firstItemName, firstID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the change Lore item for the first reward:
            gui.setItem(6, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_LORE.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_LORE_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_LORE_LORE_2.getText()).asItem(), event -> {
                try {
                    changeRewardsLoreInventory(player, uuid, itemUuid, firstID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the add enchantment item for the first reward:
            gui.setItem(4, ItemBuilder.of(Material.BOOK).name(ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS.getText()).description(ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS_LORE_1.getText(), ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS_LORE_2.getText()).asItem(), event -> {
                try {
                    addEnchantment(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier das neue &6Enchantment &eund &6Level &eein..."), Utils.setColorInMessage("&f"), firstID);
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

            String secondItemString = main.getSrDatabase().getRewardsItemStringByRowID(secondID);
            String secondItemName = ItemUtils.getItemName(secondItemString);

            //Set the change name item for the second reward
            gui.setItem(14, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_NAME.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_2.getText()).asItem(), event -> {
                try {
                    changeItemName(player, uuid, itemUuid, Utils.setColorInMessage("&eNeuen &6Namen &eeingeben..."), secondItemName, secondID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the change Lore item for the second reward:
            gui.setItem(15, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_LORE.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_LORE_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_LORE_LORE_2.getText()).asItem(), event -> {
                try {
                    changeRewardsLoreInventory(player, uuid, itemUuid, secondID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the add enchantment item for the second reward:
            gui.setItem(13, ItemBuilder.of(Material.BOOK).name(ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS.getText()).description(ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS_LORE_1.getText(), ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS_LORE_2.getText()).asItem(), event -> {
                try {
                    addEnchantment(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier das neue &6Enchantment &eund &6Level &eein..."), Utils.setColorInMessage("&f"), secondID);
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

            String thirdItemString = main.getSrDatabase().getRewardsItemStringByRowID(thirdID);
            String thirdItemName = ItemUtils.getItemName(thirdItemString);

            //Set the change name item for the third reward
            gui.setItem(23, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_NAME.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_2.getText()).asItem(), event -> {
                try {
                    changeItemName(player, uuid, itemUuid, Utils.setColorInMessage("&eNeuen &6Namen &eeingeben..."), thirdItemName, thirdID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the change Lore item for the third reward:
            gui.setItem(24, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_LORE.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_LORE_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_LORE_LORE_2.getText()).asItem(), event -> {
                try {
                    changeRewardsLoreInventory(player, uuid, itemUuid, thirdID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the add enchantment item for the third reward:
            gui.setItem(22, ItemBuilder.of(Material.BOOK).name(ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS.getText()).description(ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS_LORE_1.getText(), ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS_LORE_2.getText()).asItem(), event -> {
                try {
                    addEnchantment(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier das neue &6Enchantment &eund &6Level &eein..."), Utils.setColorInMessage("&f"), thirdID);
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

            String fourthItemString = main.getSrDatabase().getRewardsItemStringByRowID(fourthID);
            String fourthItemName = ItemUtils.getItemName(fourthItemString);

            //Set the change name item for the fourth reward
            gui.setItem(32, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_NAME.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_NAME_LORE_2.getText()).asItem(), event -> {
                try {
                    changeItemName(player, uuid, itemUuid, Utils.setColorInMessage("&eNeuen &6Namen &eeingeben..."), fourthItemName, fourthID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the change Lore item for the first reward:
            gui.setItem(33, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARD_CHANGE_ITEM_LORE.getText()).description(ItemDescription.REWARD_CHANGE_ITEM_LORE_LORE_1.getText(), ItemDescription.REWARD_CHANGE_ITEM_LORE_LORE_2.getText()).asItem(), event -> {
                try {
                    changeRewardsLoreInventory(player, uuid, itemUuid, fourthID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Set the add enchantment item for the fourth reward:
            gui.setItem(31, ItemBuilder.of(Material.BOOK).name(ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS.getText()).description(ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS_LORE_1.getText(), ItemDescription.REWARD_ITEM_ADD_ENCHANTMENTS_LORE_2.getText()).asItem(), event -> {
                try {
                    addEnchantment(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier das neue &6Enchantment &eund &6Level &eein..."), Utils.setColorInMessage("&f"), fourthID);
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
                        player.sendMessage(Messages.MATERIAL_WRONG.getMessage().replace("%input", input));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Material."));
                    }
                    Material material = Utils.getMaterialType(input);
                    String item = ItemUtils.createItemString(material.name(), material, null, null);
                    try {
                        main.getSrDatabase().addReward(uuid, itemUuid, item, 1, player);
                        player.sendMessage(Messages.REWARD_ADDED_SUCCESS.getMessage().replace("%item", input));
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
        String itemString = main.getSrDatabase().getRewardsItemStringByRowID(rowID);

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
                        String itemString = main.getSrDatabase().getRewardsItemStringByRowID(rowID);
                        Material material = ItemUtils.getItemMaterial(itemString);
                        List<String> itemDescription = ItemUtils.getItemDescription(itemString);
                        Map<Enchantment, Integer> itemEnchantmentMap = ItemUtils.getItemEnchantments(itemString);
                        String newItemString = ItemUtils.createItemString(Utils.setColorInMessage(input), material, itemEnchantmentMap, itemDescription);

                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_ITEM_CHANGE_NAME_SUCCESS.getMessage().replace("%name", Utils.setColorInMessage(input)));
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

    private static void changeRewardsLoreInventory(Player player, UUID uuid, UUID itemUuid, Integer rowID) throws SQLException {
        //&TODO: permission system
        if(!player.isOp()) {
            player.sendMessage(Messages.NO_PERMS_ERROR.getMessage());
            return;
        }
        GUI gui = main.getGuiFactory().createGUI(6, Utils.setColorInMessage("&6Belohnungen für: &a" + itemUuid));

        //Create the black Glass border:
        for(int i=0; i<54; i++) {
            gui.setItem(i, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asItem());
        }
        //Set item into Slot:
        ItemStack item = createRewardItemStack(rowID);

        gui.setItem(20, item);

        //Set nether star into Slot:
        gui.setItem(29, ItemBuilder.of(Material.NETHER_STAR).name(ItemDescription.REWARD_LORE_NETHER_STAR.getText()).description(ItemDescription.REWARD_LORE_NETHER_STAR_LORE_1.getText(), ItemDescription.REWARD_LORE_NETHER_STAR_LORE_2.getText()).asItem());

        //Create the blue glass border:
        for(int i=10; i<13; i++) {
            gui.setItem(i, ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItem());
        }
        for(int i=37; i<40; i++) {
            gui.setItem(i, ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItem());
        }
        gui.setItem(19, ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(28, ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(21, ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(30, ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItem());
        //End of blue glass border

        //Create back to the rewards page item:
        gui.setItem(45, ItemBuilder.of(item.getType()).name(ItemDescription.LORE_EXIT_REWARDS_ITEM.getText()).description(ItemDescription.LORE_EXIT_REWARDS_ITEM_LORE_1.getText(), ItemDescription.LORE_EXIT_REWARDS_ITEM_LORE_2.getText()).asItem(), event -> {
            try {
                openRewardsInventory(player, uuid, itemUuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //Set the nametags:
        String itemString = main.getSrDatabase().getRewardsItemStringByRowID(rowID);
        String itemName = ItemUtils.getItemName(itemString);
        Material itemMaterial = ItemUtils.getItemMaterial(itemString);
        Map<Enchantment, Integer> itemEnchantments = ItemUtils.getItemEnchantments(itemString);

        List<String> itemLore = ItemUtils.getItemDescription(itemString);

        //Set the first item:
        String lore_1;
        if(itemLore != null && !itemLore.isEmpty()) {
            lore_1 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", itemLore.getFirst());
            gui.setItem(14, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "1")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_1, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                if(event.getClick() == ClickType.SHIFT_LEFT) {
                    String lineToRemove = itemLore.getFirst();
                    itemLore.removeFirst();
                    String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, itemLore);
                    try {
                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_LORE_LINE_REMOVED_SUCCESS.getMessage().replace("%text", lineToRemove));
                        changeRewardsLoreInventory(player, uuid, itemUuid, rowID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(event.getClick().equals(ClickType.LEFT)) {
                    try {
                        changeItemLoreLineA(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), itemLore.getFirst(), rowID, itemLore, 1);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            lore_1 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", "&d[ NONE ]");
            gui.setItem(14, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "1")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_1, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                try {
                    changeItemLoreLineB(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), rowID, itemLore);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //Set the second item:
        String lore_2;
        if(itemLore != null && itemLore.size() >= 2) {
            lore_2 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", itemLore.get(1));
            gui.setItem(23, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "2")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_2, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                if(event.getClick() == ClickType.SHIFT_LEFT) {
                    String lineToRemove = itemLore.get(1);
                    itemLore.remove(1);
                    String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, itemLore);
                    try {
                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_LORE_LINE_REMOVED_SUCCESS.getMessage().replace("%text", lineToRemove));
                        changeRewardsLoreInventory(player, uuid, itemUuid, rowID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(event.getClick().equals(ClickType.LEFT)) {
                    try {
                        changeItemLoreLineA(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), itemLore.get(1), rowID, itemLore, 2);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            lore_2 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", "&d[ NONE ]");
            gui.setItem(23, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "2")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_2, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                try {
                    changeItemLoreLineB(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), rowID, itemLore);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //Set the third item:
        String lore_3;
        if(itemLore != null && itemLore.size() >= 3) {
            lore_3 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", itemLore.get(2));
            gui.setItem(32, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "3")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_3, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                if(event.getClick() == ClickType.SHIFT_LEFT) {
                    String lineToRemove = itemLore.get(2);
                    itemLore.remove(2);
                    String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, itemLore);
                    try {
                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_LORE_LINE_REMOVED_SUCCESS.getMessage().replace("%text", lineToRemove));
                        changeRewardsLoreInventory(player, uuid, itemUuid, rowID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(event.getClick().equals(ClickType.LEFT)) {
                    try {
                        changeItemLoreLineA(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), itemLore.get(2), rowID, itemLore, 3);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            lore_3 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", "&d[ NONE ]");
            gui.setItem(32, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "3")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_3, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                try {
                    changeItemLoreLineB(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), rowID, itemLore);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //Set the fourth item:
        String lore_4;
        if(itemLore != null && itemLore.size() >= 4) {
            lore_4 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", itemLore.get(3));
            gui.setItem(41, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "4")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_4, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                if(event.getClick() == ClickType.SHIFT_LEFT) {
                    String lineToRemove = itemLore.get(3);
                    itemLore.remove(3);
                    String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, itemLore);
                    try {
                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_LORE_LINE_REMOVED_SUCCESS.getMessage().replace("%text", lineToRemove));
                        changeRewardsLoreInventory(player, uuid, itemUuid, rowID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(event.getClick().equals(ClickType.LEFT)) {
                    try {
                        changeItemLoreLineA(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), itemLore.get(3), rowID, itemLore, 4);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            lore_4 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", "&d[ NONE ]");
            gui.setItem(41, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "4")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_4, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                try {
                    changeItemLoreLineB(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), rowID, itemLore);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //Set the fifth item:
        String lore_5;
        if(itemLore != null && itemLore.size() >= 5) {
            lore_5 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", itemLore.get(4));
            gui.setItem(16, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "5")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_5, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                if(event.getClick() == ClickType.SHIFT_LEFT) {
                    String lineToRemove = itemLore.get(4);
                    itemLore.remove(4);
                    String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, itemLore);
                    try {
                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_LORE_LINE_REMOVED_SUCCESS.getMessage().replace("%text", lineToRemove));
                        changeRewardsLoreInventory(player, uuid, itemUuid, rowID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(event.getClick().equals(ClickType.LEFT)) {
                    try {
                        changeItemLoreLineA(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), itemLore.get(4), rowID, itemLore, 5);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            lore_5 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", "&d[ NONE ]");
            gui.setItem(16, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "5")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_5, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                try {
                    changeItemLoreLineB(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), rowID, itemLore);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //Set the sixth item:
        String lore_6;
        if(itemLore != null && itemLore.size() >= 6) {
            lore_6 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", itemLore.get(5));
            gui.setItem(25, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "6")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_6, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                if(event.getClick() == ClickType.SHIFT_LEFT) {
                    String lineToRemove = itemLore.get(5);
                    itemLore.remove(5);
                    String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, itemLore);
                    try {
                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_LORE_LINE_REMOVED_SUCCESS.getMessage().replace("%text", lineToRemove));
                        changeRewardsLoreInventory(player, uuid, itemUuid, rowID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(event.getClick().equals(ClickType.LEFT)) {
                    try {
                        changeItemLoreLineA(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), itemLore.get(5), rowID, itemLore, 6);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            lore_6 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", "&d[ NONE ]");
            gui.setItem(25, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "6")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_6, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                try {
                    changeItemLoreLineB(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), rowID, itemLore);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //Set the seventh item:
        String lore_7;
        if(itemLore != null && itemLore.size() >= 7) {
            lore_7 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", itemLore.get(6));
            gui.setItem(34, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "7")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_7, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                if(event.getClick() == ClickType.SHIFT_LEFT) {
                    String lineToRemove = itemLore.get(6);
                    itemLore.remove(6);
                    String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, itemLore);
                    try {
                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_LORE_LINE_REMOVED_SUCCESS.getMessage().replace("%text", lineToRemove));
                        changeRewardsLoreInventory(player, uuid, itemUuid, rowID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(event.getClick().equals(ClickType.LEFT)) {
                    try {
                        changeItemLoreLineA(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), itemLore.get(6), rowID, itemLore, 7);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            lore_7 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", "&d[ NONE ]");
            gui.setItem(34, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "7")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_7, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                try {
                    changeItemLoreLineB(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), rowID, itemLore);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //Set the eigth item:
        String lore_8;
        if(itemLore != null && itemLore.size() >= 8) {
            lore_8 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", itemLore.get(7));
            gui.setItem(43, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "8")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_8, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                if(event.getClick() == ClickType.SHIFT_LEFT) {
                    String lineToRemove = itemLore.get(7);
                    itemLore.remove(7);
                    String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, itemLore);
                    try {
                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_LORE_LINE_REMOVED_SUCCESS.getMessage().replace("%text", lineToRemove));
                        changeRewardsLoreInventory(player, uuid, itemUuid, rowID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(event.getClick().equals(ClickType.LEFT)) {
                    try {
                        changeItemLoreLineA(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), itemLore.get(7), rowID, itemLore, 8);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            lore_8 = ItemDescription.REWARDS_LORE_LORE_2.getText().replace("%text", "&d[ NONE ]");
            gui.setItem(43, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.REWARDS_LORE_NAME.getText().replace("%number", "8")).description(ItemDescription.REWARDS_LORE_LORE_1.getText(), lore_8, ItemDescription.REWARDS_LORE_LORE_3.getText(), ItemDescription.REWARDS_LORE_LORE_4.getText(), ItemDescription.REWARDS_LORE_LORE_5.getText()).asItem(), event -> {
                try {
                    changeItemLoreLineB(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Text &eein..."), rowID, itemLore);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.show(player);
    }

    private static void changeItemLoreLineA(Player player, UUID uuid, UUID itemUuid, String title, String loreText, Integer rowID, List<String> description, Integer loreNumber) throws SQLException {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    try {
                        changeRewardsLoreInventory(player, uuid, itemUuid, rowID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onClick((slot, statesnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String input = statesnapshot.getText();
                    if(input.equals(loreText.replace("§", "&"))) {
                        player.sendMessage(Messages.REWARD_LORE_ALREADY_EXISTS.getMessage().replace("%input", Utils.setColorInMessage(input)));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    if(description.size() >= loreNumber) {
                        description.set(loreNumber - 1, Utils.setColorInMessage(input));
                    } else {
                        description.add(Utils.setColorInMessage(input));
                    }

                    try {
                        String itemString = main.getSrDatabase().getRewardsItemStringByRowID(rowID);
                        String itemName = ItemUtils.getItemName(itemString);
                        Material itemMaterial = ItemUtils.getItemMaterial(itemString);
                        Map<Enchantment, Integer> itemEnchantments = ItemUtils.getItemEnchantments(itemString);

                        String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, description);
                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);
                        player.sendMessage(Messages.REWARD_LORE_LINE_CHANGED_SUCCESS.getMessage().replace("%text", Utils.setColorInMessage(input)).replace("%line", String.valueOf(loreNumber)));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .preventClose()
                .text(loreText.replace("§", "&"))
                .title(title)
                .plugin(main)
                .open(player);
    }

    private static void changeItemLoreLineB(Player player, UUID uuid, UUID itemUuid, String title, Integer rowID, List<String> description) throws SQLException {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    try {
                        changeRewardsLoreInventory(player, uuid, itemUuid, rowID);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onClick((slot, statesnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String input = statesnapshot.getText();
                    if(input == null || input.equals("§f")) {
                        player.sendMessage(Messages.REWARD_LORE_ADD_CANCEL.getMessage());
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    try {
                        String itemString = main.getSrDatabase().getRewardsItemStringByRowID(rowID);
                        String itemName = ItemUtils.getItemName(itemString);
                        Material itemMaterial = ItemUtils.getItemMaterial(itemString);
                        Map<Enchantment, Integer> itemEnchantments = ItemUtils.getItemEnchantments(itemString);

                        if(description == null) {
                            List<String> newDescription = new ArrayList<>();
                            newDescription.add(Utils.setColorInMessage(input));

                            String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, newDescription);
                            main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);

                            String lineNumber = String.valueOf(newDescription.size());
                            player.sendMessage(Messages.REWARD_LORE_LINE_CHANGED_SUCCESS.getMessage().replace("%text", Utils.setColorInMessage(input)).replace("%line", lineNumber));
                        } else {
                            description.add(Utils.setColorInMessage(input));

                            String newItemString = ItemUtils.createItemString(itemName, itemMaterial, itemEnchantments, description);
                            main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);

                            String lineNumber = String.valueOf(description.size());
                            player.sendMessage(Messages.REWARD_LORE_LINE_CHANGED_SUCCESS.getMessage().replace("%text", Utils.setColorInMessage(input)).replace("%line", lineNumber));
                        }
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .preventClose()
                .text("§f")
                .title(title)
                .plugin(main)
                .open(player);

    }

    private static void addEnchantment(Player player, UUID uuid, UUID itemUuid, String title, String itemText, Integer rowID) throws SQLException {
        ItemStack exampleItem = ItemBuilder.of(Material.BOOK).description("&dExample Input:", " &f", "&b\"&eprotection &64&b\"", "&b\"&efrost_walker &61&b\"", "&b\"&epower &65&b\"", "&b\"&efeather_falling &62&b\"").asItem();

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
                    if(input.equals(itemText)) {
                        player.sendMessage(Messages.REWARD_ENCHANTMENTS_CANCEL.getMessage());
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    String[] parts = input.split(" ");
                    if(parts.length != 2) {
                        player.sendMessage(Messages.REWARD_ENCHANTMENTS_ADD_WRONG_FORMAT.getMessage().replace("%input", input));
                        return Arrays.asList(AnvilGUI.ResponseAction.updateTitle(Utils.setColorInMessage("&eSiehe &&example input &e- Item"), true));
                    }
                    String enchantmentPart = parts[0].toLowerCase();
                    if(!Utils.isEnchantment(enchantmentPart)) {
                        player.sendMessage(Messages.IS_NOT_ENCHANTMENT.getMessage().replace("%input", enchantmentPart));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText(itemText));
                    }
                    if(!Utils.isNumeric(parts[1]) || !(Integer.parseInt(parts[1]) > 0)) {
                        player.sendMessage(Messages.IS_NOT_NUMERIC.getMessage().replace("%input", parts[1]));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText(itemText));
                    }
                    Integer level = Integer.valueOf(parts[1]);
                    Enchantment enchantment = ItemUtils.getEnchantment(enchantmentPart);

                    try {
                        String itemString = main.getSrDatabase().getRewardsItemStringByRowID(rowID);
                        String name = ItemUtils.getItemName(itemString);
                        Material material = ItemUtils.getItemMaterial(itemString);
                        List<String> itemDescription = ItemUtils.getItemDescription(itemString);
                        Map<Enchantment, Integer> itemEnchantmentMap = ItemUtils.getItemEnchantments(itemString);

                        itemEnchantmentMap.put(enchantment, level);

                        String newItemString = ItemUtils.createItemString(name, material, itemEnchantmentMap, itemDescription);
                        Bukkit.getLogger().warning("[23:21:01] Enchantment output: " + enchantment + " //// " + level);

                        main.getSrDatabase().setNewRewardsItemStringByRowID(rowID, newItemString);

                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .preventClose()
                .text(itemText)
                .itemLeft(exampleItem)
                .title(title)
                .plugin(main)
                .open(player);
    }
}
