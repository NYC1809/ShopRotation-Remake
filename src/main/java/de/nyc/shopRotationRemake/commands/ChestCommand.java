package de.nyc.shopRotationRemake.commands;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.HologramStyle;
import de.nyc.shopRotationRemake.enums.Messages;
import de.nyc.shopRotationRemake.util.HologramUtils;
import de.nyc.shopRotationRemake.util.InventoryManager;
import de.nyc.shopRotationRemake.util.ItemUtils;
import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;

import java.sql.SQLException;
import java.util.*;

public class ChestCommand implements CommandExecutor, TabCompleter {

    private final Main main;

    public ChestCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Messages.NO_PLAYER.getMessage());
            return true;
        }
        Player player = (Player) sender;
        if(!player.isOp()) {
            player.sendMessage(Messages.NO_PERMS.getMessage());
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create":
                //srChest create <name> <Material.CHEST_TYPE>
                if(!(args.length == 3)){
                    player.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS.getMessage());
                    return true;
                }
                Location locationPlayer = player.getLocation();
                Location location = locationPlayer.getBlock().getLocation();

                if(location.getBlock().getType() != Material.AIR) {
                    player.sendMessage(Messages.LOCATION_HAS_TO_BE_AIR.getMessage());
                    return true;
                }

                String name = args[1];
                try {
                    this.main.getSrDatabase().processAllChestUuids();
                    for(String uuid : this.main.getUuidList()) {
                        if(this.main.getSrDatabase().getNameOfChest(UUID.fromString(uuid)).equals(name)) {
                            player.sendMessage(Messages.CHEST_NAME_ALREADY_EXISTS.getMessage().replace("%name", name));
                            return true;
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                String inputMaterial = args[2];

                if(!Utils.isValidBlock(inputMaterial)) {
                    player.sendMessage(Messages.MATERIAL_WRONG.getMessage().replace("%input", inputMaterial));
                    return true;
                }
                Material materialChest = Utils.getMaterialType(inputMaterial);
                if(materialChest == null) {
                    materialChest = Material.CHEST;
                    Bukkit.getLogger().info("[32:23:67] materialChest is null -> set default type to \"Material.CHEST\"");
                }

                Block block = location.getBlock();
                block.setType(materialChest);
                UUID chestUUID = UUID.randomUUID();
                if(block.getState() instanceof Chest chest) {
                    chest.getPersistentDataContainer().set(new NamespacedKey("srchest-plugin", "chest_uuid"), PersistentDataType.STRING, chestUUID.toString());
                    chest.update();
                }
                if(block.getBlockData() instanceof Directional directional) {
                    directional.setFacing(Utils.getFacingDirection(locationPlayer));
                    block.setBlockData(directional);
                    Bukkit.getLogger().info("[02:31:23] " + "Directional facing - " + Utils.getFacingDirection(locationPlayer));
                }
                //set Enabled of Chest by default to false
                //set Hologram of Chest by default to true
                //set Hologram - style by default to "hologram_item_name"
                //set minimumAmount of Chest by default to 10% of itemAmount
                try {
                    this.main.getSrDatabase().createChest(chestUUID, name, location, false, materialChest, true, player, HologramStyle.HOLOGRAM_ITEM_NAME, 1, false, 10);
                    Bukkit.getLogger().severe("[ShopRotation] srChest \"" + chestUUID + " / " + name + "\" has been written to the SQL DB!");

                    player.sendMessage(Messages.CHEST_CREATE_SUCCESS.getMessage().replace("%uuid", chestUUID.toString()));
                    HologramUtils.createHologram();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Bukkit.getLogger().info("[ShopRotation] A srChest has been created at " + location);
                break;
            case "get":
                if(args.length != 1) {
                    player.sendMessage(Messages.TOO_MUCH_ARGUMENTS.getMessage());
                    return true;
                }
                try {
                    List<String> uuids = this.main.getSrDatabase().processAllChestUuids();
                    if(uuids == null) {
                        player.sendMessage(Messages.NO_CHEST_EXISTS.getMessage());
                        return true;
                    }
                    player.sendMessage(Messages.GET_UUID_INFO_LINE_1.getMessage());
                    for(String uuid : uuids) {
                        String nameOfChest = this.main.getSrDatabase().getNameOfChest(UUID.fromString(uuid));
                        Utils.coloredCopyToClipboard(player, uuid, nameOfChest);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "remove":
                if(args.length != 2) {
                    player.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS.getMessage());
                    return true;
                }
                String rInput = args[1];
                if(!checkIfChestExists(rInput)) {
                    player.sendMessage(Messages.CHEST_DOES_NOT_EXISITS.getMessage().replace("%name", rInput));
                    return true;
                }
                try {
                    //**EASTEREGG** - I deleted the Chest first and I didnt know why this wasnt working \/ FML (-2 Hours)
                    Location blockLocation = this.main.getSrDatabase().getLocationOfChest(rInput);
                    Material chestType = Material.valueOf(this.main.getSrDatabase().getTypeOfChest(rInput));
                    if(blockLocation.getBlock().getType().equals(chestType)) {
                        Bukkit.getLogger().info("[76:63:30] chestType is equals to DB!");
                        blockLocation.getBlock().setType(Material.AIR);
                    }

                    List<String> chestUuids = this.main.getUuidList();
                    List<String> chestNames = this.main.getChestNames();
                    if(chestUuids.contains(rInput)) {
                        chestUuids.remove(rInput);
                    }
                    if(chestNames.contains(rInput)) {
                        chestNames.remove(rInput);
                    }
                    UUID uuidOfChest = this.main.getSrDatabase().getUuidByInput(rInput);
                    HologramUtils.deleteSpecificHologram(uuidOfChest);
                    this.main.getSrDatabase().deleteItems(uuidOfChest, player);
                    this.main.getSrDatabase().deleteChestByUuid(rInput, player);

                    player.sendMessage(Messages.CHEST_REMOVE_SUCCESS.getMessage().replace("%chest", uuidOfChest.toString()));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "adminsettings":
                if(args.length != 2) {
                    player.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS.getMessage());
                    return true;
                }
                String uuidOfChest = args[1];
                if(!checkIfChestExists(uuidOfChest)) {
                    player.sendMessage(Messages.CHEST_DOES_NOT_EXISITS.getMessage().replace("%name", uuidOfChest));
                    return true;
                }
                try {
                    InventoryManager.createAdminSettingsInventory(player, UUID.fromString(uuidOfChest));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "add":
                // /srChest add <uuid> <material> <amountRequired>
                if(args.length != 4) {
                    player.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS.getMessage());
                    return true;
                }
                String aUuid = args[1];

                if(!Utils.isNumeric(args[3])) {
                    player.sendMessage(Messages.IS_NOT_NUMERIC.getMessage().replace("%input", args[3]));
                    return true;
                }
                Integer amountRequired = Integer.valueOf(args[3]);
                if(!checkIfChestExists(aUuid)) {
                    player.sendMessage(Messages.CHEST_DOES_NOT_EXISITS.getMessage().replace("%name", aUuid));
                    return true;
                }
                if(!Utils.isMaterial(args[2])) {
                    player.sendMessage(Messages.MATERIAL_WRONG.getMessage().replace("%input", args[2]));
                    return true;
                }
                Material aMaterial = Utils.getMaterialType(args[2]);
                String aItem = ItemUtils.createItemString(aMaterial.name(), aMaterial, null, null);
                UUID randomItemUuid = UUID.randomUUID();
                try {
                    this.main.getSrDatabase().addItemToItemsDB(UUID.fromString(aUuid),randomItemUuid, aItem, amountRequired, player);
                    HologramUtils.updateSpecificHologram(UUID.fromString(aUuid));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                player.sendMessage(Messages.ITEM_ADDED_SUCCESS.getMessage().replace("%item", "Material." + aMaterial.name()));
                player.sendMessage(Messages.ITEM_MODIFICATE_FOR_CHANGES.getMessage());

                break;
            case "help":
                player.sendMessage(ChatColor.GOLD + "»------------------ " + Utils.getPrefix() + ChatColor.GOLD + "------------------«");
                player.sendMessage(Messages.CHEST_CREATE.getMessage());
                player.sendMessage(Messages.CHEST_GET.getMessage());
                player.sendMessage(Messages.CHEST_REMOVE.getMessage());
                player.sendMessage(Messages.CHEST_ADMINSETTINGS.getMessage());
                player.sendMessage(Messages.CHEST_ADD_ITEM.getMessage());
                player.sendMessage(Messages.CHEST_DEBUG.getMessage());
                player.sendMessage(Messages.CHEST_HELP.getMessage());
                player.sendMessage(ChatColor.GOLD + "»------------------ " + Utils.getPrefix() + ChatColor.GOLD + "------------------«");
                break;
            case "addthis":
                // /srChest addthis <uuid> <amountRequired>
                if(args.length != 3) {
                    player.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS.getMessage());
                    return true;
                }
                String atUuid = args[1];
                if(!Utils.isNumeric(args[2])) {
                    player.sendMessage(Messages.IS_NOT_NUMERIC.getMessage().replace("%input", args[2]));
                    return true;
                }
                Integer amountRequiredAddthis = Integer.valueOf(args[2]);

                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if(itemStack.equals(new ItemStack(Material.AIR))) {
                    player.sendMessage(Messages.NO_ITEM_IN_MAIN_HAND.getMessage());
                    return true;
                }
                if(!itemStack.hasItemMeta()) {
                    Material material = itemStack.getType();
                    String itemString = ItemUtils.createItemString(material.name(), material, null, null);
                    UUID randomItemUuidaddthis = UUID.randomUUID();
                    try {
                        this.main.getSrDatabase().addItemToItemsDB(UUID.fromString(atUuid),randomItemUuidaddthis, itemString, amountRequiredAddthis, player);
                        HologramUtils.updateSpecificHologram(UUID.fromString(atUuid));
                        player.sendMessage(Messages.ITEM_ADDED_SUCCESS.getMessage().replace("%item", "Material." + material.name()));
                        player.sendMessage(Messages.ITEM_MODIFICATE_FOR_CHANGES.getMessage());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if(itemMeta != null) {
                        String displayName;
                        if(itemMeta.getDisplayName().isEmpty()) {
                            displayName = itemStack.getType().name();
                        } else {
                            displayName = itemMeta.getDisplayName();
                        }
                        Material material = itemStack.getType();
                        Map<Enchantment, Integer> enchantmentMap = null;
                        List<String> itemDescription = null;

                        if(itemMeta.hasEnchants()) {
                            enchantmentMap = itemMeta.getEnchants();
                        }
                        if(itemMeta.hasLore()) {
                            itemDescription = itemMeta.getLore();
                        }
                        String itemString = ItemUtils.createItemString(displayName, material, enchantmentMap, itemDescription);
                        UUID randomItemUuidaddthis = UUID.randomUUID();
                        try {
                            main.getSrDatabase().addItemToItemsDB(UUID.fromString(atUuid), randomItemUuidaddthis, itemString, amountRequiredAddthis, player);
                            HologramUtils.updateSpecificHologram(UUID.fromString(atUuid));
                            player.sendMessage(Messages.ITEM_ADDED_SUCCESS.getMessage().replace("%item", "Material." + material.name()));
                            player.sendMessage(Messages.ITEM_MODIFICATE_FOR_CHANGES.getMessage());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                break;
            default:
                player.sendMessage(Messages.MESSAGE_UNKNOWN.getMessage());
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        List<String> uuidsOfChests = this.main.getUuidList();

        if(sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            //TODO: Permission System
            if(!player.isOp()) {
                return completions;
            }
        }

        if (args.length == 1) {
            arguments.add("create");
            arguments.add("get");
            arguments.add("remove");
            arguments.add("adminsettings");
            arguments.add("add");
            arguments.add("addthis");
            arguments.add("help");
            arguments.add("debug");
            StringUtil.copyPartialMatches(args[0], arguments, completions);
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "create":
                    arguments.add("<name>");
                    StringUtil.copyPartialMatches(args[1], arguments, completions);
                    break;
                case "remove", "adminsettings", "add", "addthis":
                    try {
                        this.main.getSrDatabase().addFromDBtoList();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    arguments.addAll(uuidsOfChests);
                    StringUtil.copyPartialMatches(args[1], arguments, completions);
                    break;
            }
        }
        if(args.length == 3) {
            List<String> validBlocks = new ArrayList<>();
            switch (args[0].toLowerCase()) {
                case "create":
                    for (Material material : Utils.getBlockList()) {
                        validBlocks.add("Material." + material);
                    }
                    arguments.addAll(validBlocks);
                    StringUtil.copyPartialMatches(args[2], arguments, completions);
                    break;
                case "add":
                    for(Material material : Utils.getItemList()) {
                        validBlocks.add("Material." + material);
                    }
                    arguments.addAll(validBlocks);
                    StringUtil.copyPartialMatches(args[2], arguments, completions);
                    break;
                case "addthis":
                    arguments.add("<amount>");
                    StringUtil.copyPartialMatches(args[2], arguments, completions);
            }
        }
        if(args.length == 4) {
            if(args[0].equalsIgnoreCase("add")) {
                arguments.add("<amount>");
                StringUtil.copyPartialMatches(args[3], arguments, completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }

    private boolean checkIfChestExists(String input) {
        try {
            return this.main.getSrDatabase().chestExistsInDB(input);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
 }
