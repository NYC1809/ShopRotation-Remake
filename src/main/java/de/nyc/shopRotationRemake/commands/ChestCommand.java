package de.nyc.shopRotationRemake.commands;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.Messages;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                Location location = player.getLocation();

                if(location.getBlock().getType() != Material.AIR) {
                    player.sendMessage(Messages.LOCATION_HAS_TO_BE_AIR.getMessage());
                    return true;
                }

                String name = args[1];

                if(!checkChestType(args[2])) {
                    player.sendMessage(Messages.CHEST_SET_MATERIAL_WRONG.getMessage().replace("%input", args[2]));
                    return true;
                }
                Material materialChest = getChestType(args[2]);
                if(materialChest == null) {
                    materialChest = Material.CHEST;
                    Bukkit.getLogger().info("[32:23:67] materialChest is null");
                }

                Block block = location.getBlock();
                block.setType(materialChest);
                UUID chestUUID = UUID.randomUUID();
                if(block.getState() instanceof Chest chest) {
                    chest.getPersistentDataContainer().set(new NamespacedKey("srchest-plugin", "chest_uuid"), PersistentDataType.STRING, chestUUID.toString());
                    chest.update();
                }
                if(block.getBlockData() instanceof Directional) {
                    Directional directional = (Directional) block.getBlockData();
                    directional.setFacing(Utils.getFacingDirection(location));
                    block.setBlockData(directional);
                    Bukkit.getLogger().info("[02:31:23] " + "Directional facing - " + Utils.getFacingDirection(location));
                }
                //set Enabled of Chest by default to false
                //set Hologram of Chest by default to true
                try {
                    this.main.getSrDatabase().createChest(chestUUID, name, location, false, materialChest, true);
                    Bukkit.getLogger().severe("[ShopRotation] srChest \"" + chestUUID + " / " + name + "\" has been written to the SQL DB!");
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
                    this.main.getSrDatabase().processAllChestUuids(player);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "remove":
                if(args.length != 2) {
                    player.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS.getMessage());
                    return true;
                }
                String input = args[1];
                try {
                    this.main.getSrDatabase().addFromDBtoList();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                List<String> uuids = this.main.getUuidList();
                List<String> chestNames = this.main.getChestNames();
                if(!(uuids.contains(input) || chestNames.contains(input))) {
                    player.sendMessage(Messages.CHEST_DOES_NOT_EXISITS.getMessage().replace("%name", input));
                    return true;
                }
                try {
                    this.main.getSrDatabase().deleteChestByUuid(input);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "adminsettings":
                break;
            case "help":
                player.sendMessage(ChatColor.GOLD + "»------------------ " + Utils.getPrefix() + ChatColor.GOLD + "------------------«");
                player.sendMessage(Messages.CHEST_CREATE.getMessage());
                player.sendMessage(Messages.CHEST_GET.getMessage());
                player.sendMessage(Messages.CHEST_REMOVE.getMessage());
                player.sendMessage(Messages.CHEST_ADMINSETTINGS.getMessage());
                player.sendMessage(Messages.CHEST_DEBUG.getMessage());
                player.sendMessage(Messages.CHEST_HELP.getMessage());
                player.sendMessage(ChatColor.GOLD + "»------------------ " + Utils.getPrefix() + ChatColor.GOLD + "------------------«");
                break;
            case "debug":
                break;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        List<String> uuidsOfChests = this.main.getUuidList();

        if (args.length == 1) {
            arguments.add("create");
            arguments.add("get");
            arguments.add("remove");
            arguments.add("adminsettings");
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
                case "remove", "adminsettings":
                    try {
                        this.main.getSrDatabase().addFromDBtoList();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    arguments.addAll(uuidsOfChests);
                    StringUtil.copyPartialMatches(args[1], arguments, completions);
                    break;
            }
            //TODO ARGUMENTS
        }
        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("create")) {
                List<String> validMaterials = List.of("Material.CHEST", "Material.ENDER_CHEST", "Material.TRAPPED_CHEST");
                arguments.addAll(validMaterials);
                StringUtil.copyPartialMatches(args[2], arguments, completions);
            }
        }
        return completions;
    }

    private boolean checkChestType(String argument) {
        List<String> validMaterials = List.of("Material.CHEST", "Material.ENDER_CHEST", "Material.TRAPPED_CHEST");
        return validMaterials.contains(argument);
    }

    private Material getChestType(String argument) {
        String value = argument.substring(argument.lastIndexOf(".") + 1);
        return Material.getMaterial(value);
    }

}
