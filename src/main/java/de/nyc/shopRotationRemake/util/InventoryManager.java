package de.nyc.shopRotationRemake.util;

import de.leonheuer.mcguiapi.gui.GUI;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.HologramStyle;
import de.nyc.shopRotationRemake.enums.ItemDescription;
import de.nyc.shopRotationRemake.enums.Messages;
import de.nyc.shopRotationRemake.objects.Quadruple;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.*;

public class InventoryManager {

    //EASTER EGG: I've had the main class registered with a Listener implementation - I am horrible at coding
    private static final Main main = Main.getInstance();

    public static void createDefaultInventory(Player player, UUID uuid) throws SQLException {
        String title = main.getSrDatabase().getNameOfChest(uuid);
        if(title == null) {
            Bukkit.getLogger().severe("[23:55:76] uuid or title of the inventory is null! -> canceling...");
            return;
        }

        GUI gui = main.getGuiFactory().createGUI(6, Utils.setColorInMessage(title));
        for(int i=0; i<9; i++) {
            gui.setItem(i, ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name(" ").asItem());
        }
        for (int i=45; i<54; i++) {
            if(i == 49) { continue; }
            gui.setItem(i, ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name(" ").asItem());
        }
        for(int i=9; i<45; i++) {
            if(i == 10 || i == 13 || i == 15) { continue; }
            gui.setItem(i, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").asItem());
        }

        String item = getCurrentItemFromDB(uuid);
        if(item == null) {
            gui.setItem(13, ItemBuilder.of(Material.BARRIER).name(ItemDescription.CHEST_HAS_NO_ACTIVE_ITEM.getText()).description(ItemDescription.CHEST_HAS_NO_ACTIVE_ITEM_LORE_1.getText(), ItemDescription.CHEST_HAS_NO_ACTIVE_ITEM_LORE_2.getText()).asItem());
        } else {
            //TODO: HERE
        }
        //TODO: Permission System
        if(player.isOp()) {
            boolean isEnabled = main.getSrDatabase().getChestEnabled(uuid);
            if(isEnabled) {
                gui.setItem(53, ItemBuilder.of(Material.LIME_WOOL).name(ItemDescription.ITEM_ENABLED.getText()).description(ItemDescription.ITEM_ENABLED_LORE_1.getText(), ItemDescription.ITEM_ENABLED_LORE_2.getText()).asItem(), event -> {
                    try {
                        main.getSrDatabase().changeEnabledOfChest(uuid, false, player);
                        player.sendMessage(Messages.SET_DISABLED_SUCCESS.getMessage());
                        createDefaultInventory(player, uuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                gui.setItem(53, ItemBuilder.of(Material.RED_WOOL).name(ItemDescription.ITEM_DISABLED.getText()).description(ItemDescription.ITEM_DISABLED_LORE_1.getText(), ItemDescription.ITEM_DISABLED_LORE_2.getText()).asItem(), event -> {
                    try {
                        main.getSrDatabase().changeEnabledOfChest(uuid, true, player);
                        player.sendMessage(Messages.SET_ENABLED_SUCCESS.getMessage());
                        createDefaultInventory(player, uuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            gui.setItem(45, ItemBuilder.of(Material.COMMAND_BLOCK).name(ItemDescription.ITEM_OPEN_AS.getText()).asItem(), event -> {
                try {
                    createAdminSettingsInventory(player, uuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        gui.show(player);
    }

    public static void createAdminSettingsInventory(Player player, UUID uuid) throws SQLException {
        //&TODO: permission system
        if(!player.isOp()) {
            player.sendMessage(Messages.NO_PERMS_ERROR.getMessage());
            return;
        }
        String title = main.getSrDatabase().getNameOfChest(uuid);
        if(title == null) {
            Bukkit.getLogger().severe("[23:55:76] uuid or title of the inventory is null! -> canceling...");
            return;
        }

        GUI gui = main.getGuiFactory().createGUI(6, Utils.setColorInMessage(title));
        for(int i=0; i<9; i++) {
            gui.setItem(i, ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name(" ").asItem());
        }
        for (int i=45; i<54; i++) {
            if(i == 49) { continue; }
            gui.setItem(i, ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name(" ").asItem());
        }
        for(int i=9; i<19; i++) {
            gui.setItem(i, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").asItem());
        }
        gui.setItem(26, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(27, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").asItem());
        for(int i=35; i<45; i++) {
            gui.setItem(i, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").asItem());
        }
        gui.setItem(13, ItemBuilder.of(Material.ACACIA_HANGING_SIGN).name(ItemDescription.AS_DESCRIPTION_NAME.getText()).description(ItemDescription.AS_DESCRIPTION_LORE_1.getText(), ItemDescription.AS_DESCRIPTION_LORE_2.getText()).asItem());


        boolean isEnabled = main.getSrDatabase().getChestEnabled(uuid);
        if(isEnabled) {
            gui.setItem(53, ItemBuilder.of(Material.LIME_WOOL).name(ItemDescription.ITEM_ENABLED.getText()).description(ItemDescription.ITEM_ENABLED_LORE_1.getText(), ItemDescription.ITEM_ENABLED_LORE_2.getText()).asItem(), event -> {
                try {
                    main.getSrDatabase().changeEnabledOfChest(uuid, false, player);
                    player.sendMessage(Messages.SET_DISABLED_SUCCESS.getMessage());
                    createAdminSettingsInventory(player, uuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            gui.setItem(53, ItemBuilder.of(Material.RED_WOOL).name(ItemDescription.ITEM_DISABLED.getText()).description(ItemDescription.ITEM_DISABLED_LORE_1.getText(), ItemDescription.ITEM_DISABLED_LORE_2.getText()).asItem(), event -> {
                try {
                    main.getSrDatabase().changeEnabledOfChest(uuid, true, player);
                    player.sendMessage(Messages.SET_ENABLED_SUCCESS.getMessage());
                    createAdminSettingsInventory(player, uuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        gui.setItem(52, ItemBuilder.of(Material.YELLOW_WOOL).name(ItemDescription.ITEM_ENABLE_DISABLE_ALL.getText()).description(ItemDescription.ITEM_ENABLE_DISABLE_ALL_LORE_1.getText(), ItemDescription.ITEM_ENABLE_DISABLE_ALL_LORE_2.getText(), ItemDescription.ITEM_ENABLE_DISABLE_ALL_LORE_3.getText()).asItem(), event -> {
            if(event.getClick().equals(ClickType.LEFT)) {
                try {
                    main.getSrDatabase().enableAllChests(uuid, player);
                    player.sendMessage(Messages.ENABLED_ALL.getMessage());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(event.getClick().equals(ClickType.RIGHT)) {
                try {
                    main.getSrDatabase().disableAllChests(uuid, player);
                    player.sendMessage(Messages.DISABLED_ALL.getMessage());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                createAdminSettingsInventory(player, uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        Map<Integer, Quadruple> recentActions = main.getSrDatabase().getLastActions();
        Map<Integer, Quadruple> sortedMap = new TreeMap<>(recentActions);

        List<String> recentActionsList = new ArrayList<>();
        recentActionsList.add("&0 ");
        for(Map.Entry<Integer, Quadruple> entry : sortedMap.entrySet()) {
            StringBuilder stringBuilder = getStringBuilder(entry);
            recentActionsList.add(stringBuilder.toString());
        }

        gui.setItem(19, ItemBuilder.of(Material.ANVIL).name(ItemDescription.ITEM_ACTION_HISTORY_NAME.getText())
                .description(recentActionsList.toArray(new String[0])).asItem());

        //TODO: Create player Item history here

        gui.setItem(21, ItemBuilder.of(Material.WIND_CHARGE).name(ItemDescription.HOLOGRAM_ENABLE_TEXT.getText()).description(ItemDescription.HOLOGRAM_ENABLE_TEXT_LORE_1.getText(), ItemDescription.HOLOGRAM_ENABLE_TEXT_LORE_2.getText(), ItemDescription.HOLOGRAM_ENABLE_TEXT_LORE_3.getText()).asItem(), event -> {
            try {
                boolean hologramEnabled = main.getSrDatabase().getHologramEnabled(uuid);
                if(event.getClick().equals(ClickType.LEFT)) {
                    if(hologramEnabled) {
                        player.sendMessage(Messages.HOLOGRAM_ALREADY_ENABLED.getMessage());
                    } else {
                        main.getSrDatabase().setHologramEnabled(uuid, true, player);
                        player.sendMessage(Messages.HOLOGRAM_ENABLED_SUCCESS.getMessage());
                        main.updateHolograms();
                    }
                }
                if(event.getClick().equals(ClickType.RIGHT)) {
                    if(!hologramEnabled) {
                        player.sendMessage(Messages.HOLOGRAM_ALREADY_DISABLED.getMessage());
                    } else {
                        main.getSrDatabase().setHologramEnabled(uuid, false, player);
                        player.sendMessage(Messages.HOLOGRAM_DISABLED_SUCCESS.getMessage());
                        main.updateHolograms();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        gui.setItem(22, ItemBuilder.of(Material.ARMOR_STAND).name(ItemDescription.ITEM_CHANGE_HOLOGRAM_STYLE.getText()).description(ItemDescription.ITEM_CHANGE_HOLOGRAM_STYLE_LORE_1.getText(), ItemDescription.ITEM_CHANGE_HOLOGRAM_STYLE_LORE_2.getText()).asItem(), event -> {
            try {
                changeHologramStyleInventory(player, uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        gui.setItem(23, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.ITEM_CHANGE_TITLE.getText()).asItem(), event -> {
            try {
                changeTitleGUI(player, title, Utils.setColorInMessage("&eNeuen &6Titel &eeingeben..."), uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        String blockType = main.getSrDatabase().getTypeOfChest(uuid.toString());
        gui.setItem(24, ItemBuilder.of(Material.CHEST).name(ItemDescription.ITEM_CHANGE_CHEST_TYPE.getText()).description(ItemDescription.ITEM_CHANGE_CHEST_TYPE_LORE_1.getText(), ItemDescription.ITEM_CHANGE_CHEST_TYPE_LORE_2.getText().replace("%type", blockType), ItemDescription.ITEM_CHANGE_CHEST_TYPE_LORE_3.getText()).asItem(), event -> {
            try {
                changeBlockTypeGUI(player, blockType, Utils.setColorInMessage("&eNeuen BlockType &eeingeben..."), uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        for(int i=25; i<32; i++) {
            if(i == 26 || i == 27) { continue; }
            gui.setItem(i, ItemBuilder.of(Material.GRAY_DYE).name(ItemDescription.ITEM_COMING_SOON.getText()).asItem());
        }
        gui.setItem(32, ItemBuilder.of(Material.WRITABLE_BOOK).name(ItemDescription.ITEM_MODIFY_ITEMS.getText()).description(ItemDescription.ITEM_MODIFY_ITEMS_LORE_1.getText(), ItemDescription.ITEM_MODIFY_ITEMS_LORE_2.getText()).asItem(), event -> {
            try {
                createItemsInventory(player, uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        gui.setItem(33, ItemBuilder.of(Material.GRAY_DYE).name(ItemDescription.ITEM_COMING_SOON.getText()).asItem());
        gui.setItem(34, ItemBuilder.of(Material.GRAY_DYE).name(ItemDescription.ITEM_COMING_SOON.getText()).asItem());

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.show(player);
    }

    public static void createItemsInventory(Player player, UUID uuid) throws SQLException{
        //&TODO: permission system
        if(!player.isOp()) {
            player.sendMessage(Messages.NO_PERMS_ERROR.getMessage());
            return;
        }
        String title = main.getSrDatabase().getNameOfChest(uuid);
        if(title == null) {
            Bukkit.getLogger().severe("[23:55:76] uuid or title of the inventory is null! -> canceling...");
            return;
        }

        GUI gui = main.getGuiFactory().createGUI(6, Utils.setColorInMessage(title + " &6 - Items"));
        for(int i=0; i<10; i++) {
            gui.setItem(i, ItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").asItem());
        }
        gui.setItem(17, ItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(18, ItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(26, ItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(27, ItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(35, ItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(36, ItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").asItem());
        for(int i=44; i<54; i++) {
            if(i == 45 || i == 48 || i == 49 || i == 50) { continue; }
            gui.setItem(i, ItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").asItem());
        }

        gui.setItem(45, ItemBuilder.of(Material.COMMAND_BLOCK).name(ItemDescription.ITEM_OPEN_AS.getText()).asItem(), event -> {
            try {
                createAdminSettingsInventory(player, uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        gui.setItem(48, ItemBuilder.of(Material.WRITABLE_BOOK).name(ItemDescription.ITEM_ADD_ITEM_TO_IV.getText()).description(ItemDescription.ITEM_ADD_ITEM_TO_IV_LORE_1.getText(), ItemDescription.ITEM_ADD_ITEM_TO_IV_LORE_2.getText()).asItem(), event -> {
            try {
                addItemToInventory(player, uuid, Utils.setColorInMessage("&eGebe hier das &6Item &ean, welches hinzugefügt werden soll..."));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //Add itemOnCursor by clicking this item:
        gui.setItem(49, ItemBuilder.of(Material.BRUSH).name(ItemDescription.ITEM_ADD_ITEM_BY_DRAG.getText()).description(ItemDescription.ITEM_ADD_ITEM_BY_DRAG_LORE_1.getText(), ItemDescription.ITEM_ADD_ITEM_BY_DRAG_LORE_2.getText()).asItem(), event -> {
            ItemStack itemOnCursor = event.getCursor();

            if(itemOnCursor != null) {
                if(!itemOnCursor.getType().equals(Material.AIR)) {
                    if(itemOnCursor.hasItemMeta()) {
                        ItemMeta itemMeta = itemOnCursor.getItemMeta();
                        if(itemMeta != null) {
                            String displayName;
                            //Important: below
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
                            String itemString = ItemUtils.createItemString(displayName, material, enchantmentList, itemLore);
                            UUID randomItemUuid = UUID.randomUUID();
                            try {
                                main.getSrDatabase().addItemToItemsDB(uuid, randomItemUuid, itemString, 1, player);
                                player.sendMessage(Messages.ITEM_ADDED_SUCCESS.getMessage().replace("%item", displayName));
                                player.sendMessage(Messages.ITEM_MODIFICATE_FOR_CHANGES.getMessage());
                                createItemsInventory(player, uuid);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        });

        gui.setItem(50, ItemBuilder.of(Material.REDSTONE_BLOCK).name(ItemDescription.ITEM_DELETE_ALL_ITEMS.getText()).description(ItemDescription.ITEM_DELETE_ALL_ITEMS_LORE_1.getText(), ItemDescription.ITEM_DELETE_ALL_ITEMS_LORE_2.getText(), ItemDescription.ITEM_DELETE_ALL_ITEMS_LORE_3.getText()).asItem(), event -> {
            if(event.getClick().equals(ClickType.DOUBLE_CLICK)) {
                try {
                    deleteAllItems(player, uuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //Begin of items:
        Integer amountOfItemsInThisChest = main.getSrDatabase().getAmountOfItemsOfChest(uuid);
        if(amountOfItemsInThisChest > 0) {
            if(amountOfItemsInThisChest > 28) {
                player.sendMessage(Messages.TOO_MANY_ITEMS_IN_CHEST.getMessage());
            }
            List<String> itemUuids = main.getSrDatabase().getListOfItems(uuid);
            int counter = 10;
            for(String itemUuid : itemUuids) {

                ItemStack item = getItemStackFromItemUuid(itemUuid);

                //ItemStack item =  ItemUtils.createItemStack(itemMaterial, itemName, itemEnchantmentMap, itemDescription);
                if(counter == 17 || counter == 26 || counter == 35 || counter == 45) { counter = counter + 2; }

                gui.setItem(counter, item, event -> {
                    try {
                        modifyItemInventory(player, uuid, UUID.fromString(itemUuid));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                counter ++;
            }
        }

        //End of items:

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.show(player);
    }


    public static void modifyItemInventory(Player player, UUID uuid, UUID itemUuid) throws SQLException {
        //&TODO: permission system
        if(!player.isOp()) {
            player.sendMessage(Messages.NO_PERMS_ERROR.getMessage());
            return;
        }
        GUI gui = main.getGuiFactory().createGUI(6, Utils.setColorInMessage("&6Modify Item: &a" + itemUuid));
        //START OF CREATING GLASS BORDER
        for (int i=0; i<54; i++) {
            gui.setItem(i, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asItem());
        }
        //END OF CREATING GLASS BORDER
        gui.setItem(45, ItemBuilder.of(Material.WRITABLE_BOOK).name(ItemDescription.ITEM_BACK_TO_ADD_ITEM_TO_INV.getText()).asItem(), event -> {
            try {
                createItemsInventory(player, uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        //Begin of item modifications:
        //Begin of purple glass border:
        for(int i=10; i<13; i++) {
            gui.setItem(i, ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).name(" ").asItem());
        }
        gui.setItem(19, ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).name(" ").asItem());
        gui.setItem(21, ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).name(" ").asItem());
        for (int i=28; i<31; i++) {
            gui.setItem(i, ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).name(" ").asItem());
        }
        //End of purple glass border
        //Set modified item into gui:
        ItemStack item = getItemStackFromItemUuid(String.valueOf(itemUuid));

        gui.setItem(20, ItemBuilder.of(item).name(Utils.setColorInMessage("&a" + itemUuid)).asItem());

        //Start of item-settings
        //Enable-Disable item setting:
        boolean itemEnabled = main.getSrDatabase().getEnabledOfItem(itemUuid);
        if(itemEnabled) {
            gui.setItem(52, ItemBuilder.of(Material.LIME_WOOL).name(ItemDescription.ITEM_IS_ENABLED.getText()).description(ItemDescription.ITEM_IS_ENABLED_LORE_1.getText(), ItemDescription.ITEM_IS_ENABLED_LORE_2.getText()).asItem(), event -> {
                try {
                    main.getSrDatabase().changeEnabledOfItem(uuid, itemUuid, false, player);
                    player.sendMessage(Messages.ITEM_DISABLED_SUCCESS.getMessage().replace("%itemuuid", itemUuid.toString()));
                    modifyItemInventory(player, uuid, itemUuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            gui.setItem(52, ItemBuilder.of(Material.RED_WOOL).name(ItemDescription.ITEM_IS_DISABLED.getText()).description(ItemDescription.ITEM_IS_DISABLED_LORE_1.getText(), ItemDescription.ITEM_IS_DISABLED_LORE_2.getText()).asItem(), event -> {
                try {
                    main.getSrDatabase().changeEnabledOfItem(uuid, itemUuid, true, player);
                    player.sendMessage(Messages.ITEM_ENABLED_SUCCES.getMessage().replace("%itemuuid", itemUuid.toString()));
                    modifyItemInventory(player, uuid, itemUuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //Delete Item Setting:
        gui.setItem(50, ItemBuilder.of(Material.REDSTONE_BLOCK).name(ItemDescription.ITEM_DELETE_ITEM.getText()).description(ItemDescription.ITEM_DELETE_ITEM_LORE_1.getText(), ItemDescription.ITEM_DELETE_ITEM_LORE_2.getText()).asItem(), event -> {
            try {
                main.getSrDatabase().deleteItemByItemUuid(uuid, itemUuid, player);
                player.sendMessage(Messages.ITEM_REMOVED_SUCCES.getMessage().replace("%itemuuid", String.valueOf(itemUuid)));
                createItemsInventory(player, uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //Set requiredAmount Setting:
        int amountRequred = main.getSrDatabase().getrequiredItemAmountByItemUuid(itemUuid);
        gui.setItem(14, ItemBuilder.of(Material.WRITABLE_BOOK).name(ItemDescription.ITEM_CHANGE_REQUIRED_AMOUNT.getText()).description(ItemDescription.ITEM_CHANGE_REQUIRED_AMOUNT_LORE_1.getText(), ItemDescription.ITEM_CHANGE_REQUIRED_AMOUNT_LORE_2.getText().replace("%amount", String.valueOf(amountRequred))).asItem(), event -> {
            try {
                changeRequiredAmount(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Wert ein..."), amountRequred);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //Set holdingAmount Setting:
        int amountHolding = main.getSrDatabase().getholdingItemAmountByItemUuid(itemUuid);
        gui.setItem(15, ItemBuilder.of(Material.WRITABLE_BOOK).name(ItemDescription.ITEM_CHANGE_HOLDING_AMOUNT.getText()).description(ItemDescription.ITEM_CHANGE_HOLDING_AMOUNT_LORE_1.getText(), ItemDescription.ITEM_CHANGE_HOLDING_AMOUNT_LORE_2.getText().replace("%amount", String.valueOf(amountHolding))).asItem(), event -> {
            try {
                changeHoldingAmount(player, uuid, itemUuid, Utils.setColorInMessage("&eGebe hier den neuen &6Wert ein..."), amountHolding);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //Set Rewards Settings:

        gui.setItem(16, ItemBuilder.of(Material.DIAMOND).name(ItemDescription.ITEM_OPEN_REWARD_GUI.getText()).description(ItemDescription.ITEM_OPEN_REWARD_GUI_LORE_1.getText(), ItemDescription.ITEM_OPEN_REWARD_GUI_LORE_2.getText()).asItem(), event -> {
            try {
                RewardsInventory.openRewardsInventory(player, uuid, itemUuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.show(player);
    }

    private static StringBuilder getStringBuilder(Map.Entry<Integer, Quadruple> entry) {
        Integer id = entry.getKey();
        Quadruple values = entry.getValue();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&f&lID &7[");
        stringBuilder.append(id);
        stringBuilder.append("] &7 &l»» &6");
        stringBuilder.append(values.getValue2());
        stringBuilder.append(" &7&l»» &3");
        stringBuilder.append(values.getValue1());
        stringBuilder.append(" &7&l»» &a");
        stringBuilder.append(values.getValue3());
        stringBuilder.append(" &7&l»» &d[ ");
        stringBuilder.append(values.getValue4());
        stringBuilder.append(" &d]");
        return stringBuilder;
    }

    private static String getCurrentItemFromDB(UUID uuid) throws SQLException {
        //TODO: Move to CurrentItem Class
        String item = main.getSrDatabase().getCurrentItem(uuid);
        //TODO: generate the current Item
        return item;
    }

    private static void changeTitleGUI(Player player, String chestName, String title, UUID uuid) throws SQLException {
        final String[] newTitle = {chestName};
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    try {
                        createAdminSettingsInventory(player, uuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    newTitle[0] = stateSnapshot.getText();
                    if(chestName.equals(newTitle[0])) {
                        player.sendMessage(Messages.CHEST_CHANGED_NAME_CANCEL.getMessage().replace("%name", newTitle[0]));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    try {
                        main.getSrDatabase().processAllChestUuids();
                        for(String entry : main.getUuidList()) {
                            if(main.getSrDatabase().getNameOfChest(UUID.fromString(entry)).equals(newTitle[0])) {
                                player.sendMessage(Messages.CHEST_NAME_ALREADY_EXISTS.getMessage().replace("%name", newTitle[0]));
                                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText(newTitle[0]));
                            }
                        }
                        main.getSrDatabase().changeNameOfChest(uuid, newTitle[0], player);
                        player.sendMessage(Messages.CHEST_CHANGED_NAME_SUCCESS.getMessage().replace("%name", Utils.setColorInMessage(newTitle[0])));
                        main.updateHolograms();
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .preventClose()                     //prevents the inventory from being closed
                .text(chestName)                    //sets the text the GUI should start with
                .title(title)                       //set the title of the GUI (only works in 1.14+)
                .plugin(main)                       //set the plugin instance
                .open(player);                      //opens the GUI for the player provided
    }

    private static void changeBlockTypeGUI(Player player, String blockType, String title, UUID uuid) throws SQLException {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    try {
                        createAdminSettingsInventory(player, uuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String input = stateSnapshot.getText();
                    if(!Utils.isValidBlock(input)) {
                        player.sendMessage(Messages.MATERIAL_WRONG.getMessage().replace("%input", input));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Material." + blockType));
                    }
                    try {
                        String material = main.getSrDatabase().getTypeOfChest(uuid.toString());
                        if(input.equals("Material." + material)) {
                            player.sendMessage(Messages.CHEST_CHANGED_TYPE_CANCEL.getMessage().replace("%type", material));
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Material material = Utils.getMaterialType(input);
                        main.getSrDatabase().setTypeOfChest(uuid, player, String.valueOf(material));
                        player.sendMessage(Messages.CHEST_CHANGED_TYPE_SUCCESS.getMessage().replace("%type", input));

                        Location blockLocation = main.getSrDatabase().getLocationOfChest(uuid.toString());
                        Block block = blockLocation.getBlock();
                        block.setType(material);
                        if(block.getBlockData() instanceof Directional directional) {
                            directional.setFacing(Utils.getFacingDirection(player.getLocation()));
                            block.setBlockData(directional);
                            Bukkit.getLogger().info("[02:31:23] " + "Directional facing - " + Utils.getFacingDirection(player.getLocation()));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .preventClose()
                .text("Material." + blockType)
                .title(title)
                .plugin(main)
                .open(player);
    }

    private static void deleteAllItems(Player player, UUID uuid) throws SQLException {
        main.getSrDatabase().deleteItems(uuid, player);
        player.sendMessage(Messages.ITEMS_REMOVED_SUCCESS.getMessage().replace("%name", uuid.toString()));
        createItemsInventory(player, uuid);
    }

    private static void addItemToInventory(Player player, UUID uuid, String title) throws SQLException {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    try {
                        createItemsInventory(player, uuid);
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
                        player.sendMessage(Messages.ITEM_ADD_CANCELED.getMessage());
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    if(!Utils.isMaterial(input)) {
                        player.sendMessage(Messages.MATERIAL_WRONG.getMessage().replace("%input", "Material." + input));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Material."));
                    }
                    Material material = Utils.getMaterialType(input);
                    String item = ItemUtils.createItemString(material.name(), material, null, null);
                    UUID randomItemUuid = UUID.randomUUID();
                    try {
                        main.getSrDatabase().addItemToItemsDB(uuid, randomItemUuid, item, 0, player);
                        player.sendMessage(Messages.ITEM_ADDED_SUCCESS.getMessage().replace("%item", "Material." + input));
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

    private static void changeRequiredAmount(Player player, UUID uuid, UUID itemUuid,  String title, Integer currentAmount) throws SQLException {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    try {
                        modifyItemInventory(player, uuid, itemUuid);
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
                        main.getSrDatabase().setrequiredAmountByItemUuid(itemUuid, Integer.valueOf(input));
                        player.sendMessage(Messages.ITEM_CHANGED_AMOUNT_SUCCESS.getMessage().replace("%number", input));
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

    private static void changeHoldingAmount(Player player, UUID uuid, UUID itemUuid,  String title, Integer currentAmount) throws SQLException {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    try {
                        modifyItemInventory(player, uuid, itemUuid);
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
                        main.getSrDatabase().setholdingAmountByItemUuid(itemUuid, Integer.valueOf(input));
                        player.sendMessage(Messages.ITEM_CHANGED_AMOUNT_SUCCESS.getMessage().replace("%number", input));
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

    private static ItemStack getItemStackFromItemUuid(String itemUuid) throws SQLException {
        String itemString = main.getSrDatabase().getItemString(UUID.fromString(itemUuid));
        Material itemMaterial = ItemUtils.getItemMaterial(itemString);
        String itemName = ItemUtils.getItemName(itemString);
        List<String> itemDescription = ItemUtils.getItemDescription(itemString);

        Map<Enchantment, Integer> itemEnchantmentMap = ItemUtils.getItemEnchantments(itemString);

        Integer requiredAmount = main.getSrDatabase().getrequiredItemAmountByItemUuid(UUID.fromString(itemUuid));
        Integer holdingAmount = main.getSrDatabase().getholdingItemAmountByItemUuid(UUID.fromString(itemUuid));
        Boolean itemEnabled = main.getSrDatabase().getEnabledOfItem(UUID.fromString(itemUuid));

        return createItemDescription(itemMaterial, itemName, itemEnchantmentMap, itemDescription, requiredAmount, holdingAmount, itemEnabled);
    }

    private static ItemStack createItemDescription(Material itemMaterial, String itemName, Map<Enchantment, Integer> itemEnchantments, List<String> itemDescription, Integer requiredAmount, Integer holdingAmount, boolean itemEnabled) {
        //TODO: Add rewards into description when implemented

        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.setColorInMessage("&eKlicke hier um dieses &6Item &ezu modifizieren"));

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
        if(itemEnchantments.isEmpty()) {
            lore.add("  &9» [&bEnchantments&9] &d" + "[ NONE ]");
        } else {
            lore.add("  &9» [&bEnchantments&9] &d");
            for(Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                Integer level = entry.getValue();
                lore.add("               &9» &b" + enchantment.toString() + " &dLevel: &3" + level);
            }
        }
        lore.add(" ");
        lore.add("  &9» [&bBenötigte Zahl an Items&9] &d" + requiredAmount);
        lore.add(" ");
        lore.add("  &9» [&bBereits besitzende Zahl an Items&9] &d" + holdingAmount);
        lore.add(" ");
        if(itemEnabled) {
            lore.add("  &9» [&bStatus&9] &aEnabled");
        } else {
            lore.add("  &9» [&bStatus&9] &cDisabled");
        }
        itemMeta.setLore(Utils.setColorInList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

    private static void changeHologramStyleInventory(Player player, UUID uuid) throws SQLException {
        //&TODO: permission system
        if(!player.isOp()) {
            player.sendMessage(Messages.NO_PERMS_ERROR.getMessage());
            return;
        }

        GUI gui = main.getGuiFactory().createGUI(1, Utils.setColorInMessage("&eKlicke auf den neuen &6Hologram-Stil&e!"));

        //Create glass border:
        for(int i=0; i<9; i++) {
            gui.setItem(i, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asItem());
        }

        String hologramStyle = main.getSrDatabase().getHologramStyle(uuid);

        if(hologramStyle.equals(HologramStyle.HOLOGRAM_ITEM.getName())) {
            gui.setItem(0, ItemBuilder.of(Material.ARMOR_STAND)
                    .name(ItemDescription.HOLOGRAM_STYLE_1_ACTIVE.getText())
                    .description(ItemDescription.HOLOGRAM_STYLE_SPACE_HOLDER.getText(), ItemDescription.HOLOGRAM_STYLE_1_ACTIVE_LORE.getText())
                    .enchant(Enchantment.MENDING, 1)
                    .addFlags(ItemFlag.HIDE_ENCHANTS)
                    .asItem());
        } else {
            gui.setItem(0, ItemBuilder.of(Material.ARMOR_STAND)
                    .name(ItemDescription.HOLOGRAM_STYLE_1.getText())
                    .description(ItemDescription.HOLOGRAM_STYLE_SPACE_HOLDER.getText(), ItemDescription.HOLOGRAM_STYLE_1_LORE.getText())
                    .asItem(), event -> {
                try {
                    main.getSrDatabase().setHologramStyle(uuid, HologramStyle.HOLOGRAM_ITEM.getName(), player);
                    player.sendMessage(Messages.HOLOGRAM_CHANGE_STYLE_SUCCES.getMessage().replace("%style", HologramStyle.HOLOGRAM_ITEM.getUpperCaseName()));
                    changeHologramStyleInventory(player, uuid);
                    main.updateHolograms();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        if(hologramStyle.equals(HologramStyle.HOLOGRAM_ITEM_NAME.getName())) {
            gui.setItem(2, ItemBuilder.of(Material.ARMOR_STAND)
                    .name(ItemDescription.HOLOGRAM_STYLE_2_ACTIVE.getText())
                    .description(ItemDescription.HOLOGRAM_STYLE_SPACE_HOLDER.getText(), ItemDescription.HOLOGRAM_STYLE_2_ACTIVE_LORE.getText())
                    .enchant(Enchantment.MENDING, 1)
                    .addFlags(ItemFlag.HIDE_ENCHANTS)
                    .asItem());
        } else {
            gui.setItem(2, ItemBuilder.of(Material.ARMOR_STAND)
                    .name(ItemDescription.HOLOGRAM_STYLE_2.getText())
                    .description(ItemDescription.HOLOGRAM_STYLE_SPACE_HOLDER.getText(), ItemDescription.HOLOGRAM_STYLE_2_LORE.getText())
                    .asItem(), event -> {
                try {
                    main.getSrDatabase().setHologramStyle(uuid, HologramStyle.HOLOGRAM_ITEM_NAME.getName(), player);
                    player.sendMessage(Messages.HOLOGRAM_CHANGE_STYLE_SUCCES.getMessage().replace("%style", HologramStyle.HOLOGRAM_ITEM_NAME.getUpperCaseName()));
                    changeHologramStyleInventory(player, uuid);
                    main.updateHolograms();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        if(hologramStyle.equals(HologramStyle.HOLOGRAM_ITEM_NAME_PROGRESS.getName())) {
            gui.setItem(4, ItemBuilder.of(Material.ARMOR_STAND)
                    .name(ItemDescription.HOLOGRAM_STYLE_3_ACTIVE.getText())
                    .description(ItemDescription.HOLOGRAM_STYLE_SPACE_HOLDER.getText(), ItemDescription.HOLOGRAM_STYLE_3_ACTIVE_LORE.getText())
                    .enchant(Enchantment.MENDING, 1)
                    .addFlags(ItemFlag.HIDE_ENCHANTS)
                    .asItem());
        } else {
            gui.setItem(4, ItemBuilder.of(Material.ARMOR_STAND)
                    .name(ItemDescription.HOLOGRAM_STYLE_3.getText())
                    .description(ItemDescription.HOLOGRAM_STYLE_SPACE_HOLDER.getText(), ItemDescription.HOLOGRAM_STYLE_3_LORE.getText())
                    .asItem(), event -> {
                try {
                    main.getSrDatabase().setHologramStyle(uuid, HologramStyle.HOLOGRAM_ITEM_NAME_PROGRESS.getName(), player);
                    player.sendMessage(Messages.HOLOGRAM_CHANGE_STYLE_SUCCES.getMessage().replace("%style", HologramStyle.HOLOGRAM_ITEM_NAME_PROGRESS.getUpperCaseName()));
                    changeHologramStyleInventory(player, uuid);
                    main.updateHolograms();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        if(hologramStyle.equals(HologramStyle.HOLOGRAM_NAME_PROGRESS.getName())) {
            gui.setItem(6, ItemBuilder.of(Material.ARMOR_STAND)
                    .name(ItemDescription.HOLOGRAM_STYLE_4_ACTIVE.getText())
                    .description(ItemDescription.HOLOGRAM_STYLE_SPACE_HOLDER.getText(), ItemDescription.HOLOGRAM_STYLE_4_ACTIVE_LORE.getText())
                    .enchant(Enchantment.MENDING, 1)
                    .addFlags(ItemFlag.HIDE_ENCHANTS)
                    .asItem());
        } else {
            gui.setItem(6, ItemBuilder.of(Material.ARMOR_STAND)
                    .name(ItemDescription.HOLOGRAM_STYLE_4.getText())
                    .description(ItemDescription.HOLOGRAM_STYLE_SPACE_HOLDER.getText(), ItemDescription.HOLOGRAM_STYLE_4_LORE.getText())
                    .asItem(), event -> {
                try {
                    main.getSrDatabase().setHologramStyle(uuid, HologramStyle.HOLOGRAM_NAME_PROGRESS.getName(), player);
                    player.sendMessage(Messages.HOLOGRAM_CHANGE_STYLE_SUCCES.getMessage().replace("%style", HologramStyle.HOLOGRAM_NAME_PROGRESS.getUpperCaseName()));
                    changeHologramStyleInventory(player, uuid);
                    main.updateHolograms();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        if(hologramStyle.equals(HologramStyle.HOLOGRAM_ITEM_PROGRESS.getName())) {
            gui.setItem(8, ItemBuilder.of(Material.ARMOR_STAND)
                    .name(ItemDescription.HOLOGRAM_STYLE_5_ACTIVE.getText())
                    .description(ItemDescription.HOLOGRAM_STYLE_SPACE_HOLDER.getText(), ItemDescription.HOLOGRAM_STYLE_5_ACTIVE_LORE.getText())
                    .enchant(Enchantment.MENDING, 1)
                    .addFlags(ItemFlag.HIDE_ENCHANTS)
                    .asItem());
        } else {
            gui.setItem(8, ItemBuilder.of(Material.ARMOR_STAND)
                    .name(ItemDescription.HOLOGRAM_STYLE_5.getText())
                    .description(ItemDescription.HOLOGRAM_STYLE_SPACE_HOLDER.getText(), ItemDescription.HOLOGRAM_STYLE_5_LORE.getText())
                    .asItem(), event -> {
                try {
                    main.getSrDatabase().setHologramStyle(uuid, HologramStyle.HOLOGRAM_ITEM_PROGRESS.getName(), player);
                    player.sendMessage(Messages.HOLOGRAM_CHANGE_STYLE_SUCCES.getMessage().replace("%style", HologramStyle.HOLOGRAM_ITEM_PROGRESS.getUpperCaseName()));
                    changeHologramStyleInventory(player, uuid);
                    main.updateHolograms();
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
}
