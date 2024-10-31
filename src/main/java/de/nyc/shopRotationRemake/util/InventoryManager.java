package de.nyc.shopRotationRemake.util;

import de.leonheuer.mcguiapi.gui.GUI;
import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.ItemDescription;
import de.nyc.shopRotationRemake.enums.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.UUID;

public class InventoryManager implements Listener {

    private static Main main;

    public InventoryManager(Main main) {
        InventoryManager.main = main;
    }

    public static void createDefaultInventory(Player player, UUID uuid, String name) throws SQLException {
        Inventory inventory = Bukkit.createInventory(player, 54, Utils.setColorInMessage(name));
        player.openInventory(inventory);

        GUI gui = main.getGuiFactory().createGUI(6, Utils.setColorInMessage(name));
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
                        createDefaultInventory(player, uuid, name);
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
                        createDefaultInventory(player, uuid, name);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            gui.setItem(45, ItemBuilder.of(Material.COMMAND_BLOCK).name("&eKlicke hier um die &6Einstellungen &ezu öffnen:").asItem(), event -> {
                event.setCancelled(true);
                try {
                    createAdminSettingsInventory(player, uuid, name);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        gui.show(player);
    }

    public static void createAdminSettingsInventory(Player player, UUID uuid, String name) throws SQLException {
        //&TODO: permission system
        if(!player.isOp()) {
            player.sendMessage(Messages.NO_PERMS_ERROR.getMessage());
            return;
        }

        Inventory inventory = Bukkit.createInventory(player, 54, Utils.setColorInMessage(name));
        player.openInventory(inventory);

        GUI gui = main.getGuiFactory().createGUI(6, Utils.setColorInMessage(name));
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
                    createDefaultInventory(player, uuid, name);
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
                    createDefaultInventory(player, uuid, name);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        gui.setItem(52, ItemBuilder.of(Material.YELLOW_WOOL).name(ItemDescription.ITEM_ENABLE_DISABLE_ALL.getText()).description(ItemDescription.ITEM_ENABLE_DISABLE_ALL_LORE_1.getText(), ItemDescription.ITEM_ENABLE_DISABLE_ALL_LORE_2.getText()).asItem(), event -> {
            if(event.getClick().equals(ClickType.LEFT)) {
                try {
                    main.getSrDatabase().enableAllChests(uuid, player);
                    player.sendMessage(Messages.ENABLED_ALL.getMessage());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(event.getClick().equals(ClickType.SHIFT_LEFT)) {
                try {
                    main.getSrDatabase().disableAllChests(uuid, player);
                    player.sendMessage(Messages.DISABLED_ALL.getMessage());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            event.setCancelled(true);
        });


        gui.show(player);
    }

    private static String getCurrentItemFromDB(UUID uuid) throws SQLException {
        String item = main.getSrDatabase().getCurrentItem(uuid);
        //TODO: generate the current Item
        return item;
    }
}
