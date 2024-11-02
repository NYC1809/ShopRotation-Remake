package de.nyc.shopRotationRemake.util;

import de.leonheuer.mcguiapi.gui.GUI;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.ItemDescription;
import de.nyc.shopRotationRemake.enums.Messages;
import de.nyc.shopRotationRemake.objects.Quadruple;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;

import java.sql.SQLException;
import java.util.*;

public class InventoryManager implements Listener {

    private static Main main;

    public InventoryManager(Main main) {
        InventoryManager.main = main;
    }

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
                gui.setItem(53, ItemBuilder.of(Material.GREEN_WOOL).name(ItemDescription.ITEM_ENABLED.getText()).description(ItemDescription.ITEM_ENABLED_LORE_1.getText(), ItemDescription.ITEM_ENABLED_LORE_2.getText()).asItem(), event -> {
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
            gui.setItem(53, ItemBuilder.of(Material.GREEN_WOOL).name(ItemDescription.ITEM_ENABLED.getText()).description(ItemDescription.ITEM_ENABLED_LORE_1.getText(), ItemDescription.ITEM_ENABLED_LORE_2.getText()).asItem(), event -> {
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

        gui.setItem(23, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.ITEM_CHANGE_TITLE.getText()).asItem(), event -> {
            try {
                changeTitleGUI(player, title, Utils.setColorInMessage("&eNeuen &6Titel &eeingeben..."), uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        String blockType = main.getSrDatabase().getTypeOfChest(uuid.toString());
        gui.setItem(24, ItemBuilder.of(Material.CHEST).name(ItemDescription.ITEM_CHANGE_CHEST_TYPE.getText()).description(ItemDescription.ITEM_CHANGE_CHEST_TYPE_LORE_1.getText(), ItemDescription.ITEM_CHANGE_CHEST_TYPE_LORE_2.getText().replace("%type", blockType)).asItem(), event -> {
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
            //TODO: ADD ITEM FUNCTION
        });

        gui.setItem(50, ItemBuilder.of(Material.REDSTONE_BLOCK).name(ItemDescription.ITEM_DELETE_ALL_ITEMS.getText()).description(ItemDescription.ITEM_DELETE_ALL_ITEMS_LORE_1.getText(), ItemDescription.ITEM_DELETE_ALL_ITEMS_LORE_2.getText()).asItem(), event -> {
            try {
                deleteAllItems(player, uuid);
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
                        stateSnapshot.getPlayer().sendMessage(Messages.CHEST_CHANGED_NAME_SUCCESS.getMessage().replace("%name", Utils.setColorInMessage(newTitle[0])));
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
                        player.sendMessage(Messages.CHEST_MATERIAL_WRONG.getMessage().replace("%input", input));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Material." + blockType));
                    }
                    try {
                        Material material = Utils.getBlockType(input);
                        main.getSrDatabase().setTypeOfChest(uuid, player, String.valueOf(material));
                        player.sendMessage(Messages.CHEST_CHANGED_TYPE_SUCCESS.getMessage().replace("%type", input));

                        Location blockLocation = main.getSrDatabase().getLocationOfChest(uuid.toString());
                        blockLocation.getBlock().setType(material);

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
}
