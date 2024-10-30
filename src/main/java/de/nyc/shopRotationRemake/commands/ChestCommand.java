package de.nyc.shopRotationRemake.commands;

import de.leonheuer.mcguiapi.utils.ItemBuilder;
import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.HologramStyle;
import de.nyc.shopRotationRemake.enums.Messages;
import de.nyc.shopRotationRemake.objects.Hologram;
import de.nyc.shopRotationRemake.objects.Quadruple;
import de.nyc.shopRotationRemake.util.HologramUtils;
import de.nyc.shopRotationRemake.util.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
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
                String inputMaterial = args[2];

                if(!isValidBlock(inputMaterial)) {
                    player.sendMessage(Messages.CHEST_MATERIAL_WRONG.getMessage().replace("%input", inputMaterial));
                    return true;
                }
                Material materialChest = getBlockType(inputMaterial);
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
                try {
                    this.main.getSrDatabase().createChest(chestUUID, name, location, false, materialChest, true, player, HologramStyle.HOLOGRAM_ITEM_NAME);
                    Bukkit.getLogger().severe("[ShopRotation] srChest \"" + chestUUID + " / " + name + "\" has been written to the SQL DB!");

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
                    HologramUtils.deleteHolograms();

                    List<String> chestUuids = this.main.getUuidList();
                    List<String> chestNames = this.main.getChestNames();
                    if(chestUuids.contains(rInput)) {
                        chestUuids.remove(rInput);
                    }
                    if(chestNames.contains(rInput)) {
                        chestNames.remove(rInput);
                    }
                    UUID uuidOfChest = this.main.getSrDatabase().getUuidByInput(rInput);
                    this.main.getSrDatabase().deleteItems(uuidOfChest, player);
                    this.main.getSrDatabase().deleteChestByUuid(rInput, player);

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "adminsettings":
                break;
            case "add":
                // /srChest add <uuid> <material> <amountRequired>
                if(args.length != 4) {
                    player.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS.getMessage());
                    return true;
                }
                String aUuid = args[1];

                if(!Utils.isNumeric(args[3])) {
                    player.sendMessage(Messages.IS_NOT_NUMERIC.getMessage());
                    return true;
                }
                Integer amountRequired = Integer.valueOf(args[3]);
                if(!checkIfChestExists(aUuid)) {
                    player.sendMessage(Messages.CHEST_DOES_NOT_EXISITS.getMessage().replace("%name", aUuid));
                    return true;
                }
                if(!Utils.isMaterial(args[2])) {
                    player.sendMessage(Messages.CHEST_MATERIAL_WRONG.getMessage().replace("%input", args[2]));
                    return true;
                }
                Material aMaterial = getBlockType(args[2]);
                String aItem = Utils.convertItemStackToString(aMaterial, String.valueOf(aMaterial));
                try {
                    this.main.getSrDatabase().addItemToItemsDB(UUID.fromString(aUuid), aItem, amountRequired, player);

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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
            case "debug":
                try {
                    Map<Integer, Quadruple> actionMap = this.main.getSrDatabase().getLastActions();
                    for(Map.Entry<Integer, Quadruple> entry : actionMap.entrySet()) {
                        Integer id = entry.getKey();
                        Quadruple values = entry.getValue();
                        player.sendMessage(id.toString() + " » " + values.getValue1() + " | " + values.getValue2() + " | " + values.getValue3() + " | " + values.getValue4());
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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
                case "remove", "adminsettings", "add":
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

    private Material getBlockType(String argument) {
        String value = argument.substring(argument.lastIndexOf(".") + 1);
        return Material.getMaterial(value);
    }

    private boolean isValidBlock(String input) {
        List<Material> blocks = new ArrayList<>();

        for(Material material : Material.values()) {
            if(material.isBlock()) {
                blocks.add(material);
            }
        }
        String value = input.substring(input.lastIndexOf(".") + 1);
        return blocks.contains(Material.getMaterial(value));
    }

    private boolean checkIfChestExists(String input) {
        try {
            return this.main.getSrDatabase().chestExistsInDB(input);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
 }
