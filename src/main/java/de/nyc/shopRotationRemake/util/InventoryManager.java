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
            gui.setItem(i, ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name(" ").asItem(), event -> {
                event.setCancelled(true);
            });
        }
        for (int i=45; i<54; i++) {
            if(i == 49) { continue; }
            gui.setItem(i, ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name(" ").asItem(), event -> {
                event.setCancelled(true);
            });
        }
        for(int i=9; i<45; i++) {
            if(i == 10 || i == 13 || i == 15) { continue; }
            gui.setItem(i, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").asItem(), event -> {
                event.setCancelled(true);
            });
        }

        String item = getCurrentItemFromDB(uuid);
        if(item == null) {
            gui.setItem(13, ItemBuilder.of(Material.BARRIER).name(Messages.CHEST_HAS_NO_ACTIVE_ITEM.getMessage()).description(Messages.CHEST_HAS_NO_ACTIVE_ITEM_LORE_1.getMessage(), Messages.CHEST_HAS_NO_ACTIVE_ITEM_LORE_2.getMessage()).asItem(), event -> {
               event.setCancelled(true);
            });
        } else {
            //TODO: HERE
        }
        //TODO: Permission System
        if(player.isOp()) {
            boolean isEnabled = main.getSrDatabase().getChestEnabled(uuid);
            if(isEnabled) {
                gui.setItem(53, ItemBuilder.of(Material.GREEN_WOOL).name(ItemDescription.ITEM_ENABLED.getText()).description(ItemDescription.ITEM_ENABLED_LORE_1.getText(), ItemDescription.ITEM_ENABLED_LORE_2.getText()).asItem(), event -> {
                    event.setCancelled(true);
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
                    event.setCancelled(true);
                    try {
                        main.getSrDatabase().changeEnabledOfChest(uuid, true, player);
                        player.sendMessage(Messages.SET_ENABLED_SUCCESS.getMessage());
                        createDefaultInventory(player, uuid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            gui.setItem(45, ItemBuilder.of(Material.COMMAND_BLOCK).name("&eKlicke hier um die &6Einstellungen &ezu öffnen:").asItem(), event -> {
                event.setCancelled(true);
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
            gui.setItem(i, ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name(" ").asItem(), event -> {
                event.setCancelled(true);
            });
        }
        for (int i=45; i<54; i++) {
            if(i == 49) { continue; }
            gui.setItem(i, ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name(" ").asItem(), event -> {
                event.setCancelled(true);
            });
        }
        for(int i=9; i<19; i++) {
            gui.setItem(i, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").asItem(), event -> {
                event.setCancelled(true);
            });
        }
        gui.setItem(26, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").asItem(), event -> {
            event.setCancelled(true);
        });
        gui.setItem(27, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").asItem(), event -> {
            event.setCancelled(true);
        });
        for(int i=35; i<45; i++) {
            gui.setItem(i, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").asItem(), event -> {
                event.setCancelled(true);
            });
        }
        gui.setItem(13, ItemBuilder.of(Material.ACACIA_HANGING_SIGN).name(ItemDescription.AS_DESCRIPTION_NAME.getText()).description(ItemDescription.AS_DESCRIPTION_LORE_1.getText(), ItemDescription.AS_DESCRIPTION_LORE_2.getText()).asItem(), event -> {
            event.setCancelled(true);
        });


        boolean isEnabled = main.getSrDatabase().getChestEnabled(uuid);
        if(isEnabled) {
            gui.setItem(53, ItemBuilder.of(Material.GREEN_WOOL).name(ItemDescription.ITEM_ENABLED.getText()).description(ItemDescription.ITEM_ENABLED_LORE_1.getText(), ItemDescription.ITEM_ENABLED_LORE_2.getText()).asItem(), event -> {
                event.setCancelled(true);
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
                event.setCancelled(true);
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
            event.setCancelled(true);
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
                .description(recentActionsList.toArray(new String[0])).asItem(), event -> {
            event.setCancelled(true);
        });

        //TODO: Create player Item history here

        gui.setItem(23, ItemBuilder.of(Material.NAME_TAG).name(ItemDescription.ITEM_CHANGE_TITLE.getText()).asItem(), event -> {
            try {
                changeTitleGUI(player, title, Utils.setColorInMessage("&eNeuen &6Titel &eeingeben..."), uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            event.setCancelled(true);
        });

        String blockType = main.getSrDatabase().getTypeOfChest(uuid.toString());

        gui.setItem(24, ItemBuilder.of(Material.CHEST).name(ItemDescription.ITEM_CHANGE_CHEST_TYPE.getText()).description(ItemDescription.ITEM_CHANGE_CHEST_TYPE_LORE_1.getText(), ItemDescription.ITEM_CHANGE_CHEST_TYPE_LORE_2.getText().replace("%type", blockType)).asItem(), event -> {
            try {
                changeBlockTypeGUI(player, blockType, Utils.setColorInMessage("&eNeuen BlockType &eeingeben..."), uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
        final String newBlockType = blockType;
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
}
