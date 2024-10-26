package de.nyc.shopRotationRemake.commands;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
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

public class CreateChestCommand implements CommandExecutor, TabCompleter {

    private final Main main;

    public CreateChestCommand(Main main) {
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
            case "set":
                //srChest set <name> <Material.CHEST_TYPE>
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

                checkChestType(args[2], player);
                Material materialChest = Material.getMaterial(args[2]);
                if(materialChest == null) {
                    materialChest = Material.CHEST;
                }

                Block block = location.getBlock();
                block.setType(materialChest);
                UUID chestUUID = UUID.randomUUID();
                if(block.getState() instanceof Chest) {
                    Chest chest = (Chest) block.getState();
                    chest.getPersistentDataContainer().set(new NamespacedKey("srchest-plugin", "chest_uuid"), PersistentDataType.STRING, chestUUID.toString());
                    chest.update();
                }

                //set Chest by default enabled to false
                try {
                    this.main.getSrDatabase().createChest(chestUUID, name, location, false);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "get":
                break;
            case "remove":
                break;
            case "adminsettings":
                break;
            case "help":
                break;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            arguments.add("set");
            arguments.add("get");
            arguments.add("remove");
            arguments.add("adminsettings");
            arguments.add("help");
            StringUtil.copyPartialMatches(args[0], arguments, completions);
        }
        if (args.length == 2) {
            //TODO ARGUMENTS
        }
        return null;
    }

    private boolean checkChestType(String argument, Player player) {
        List<Material> validMaterials = List.of(Material.CHEST, Material.ENDER_CHEST, Material.TRAPPED_CHEST);
        if(validMaterials.contains(Material.getMaterial(argument))) {
            return true;
        }
        player.sendMessage(Messages.CHEST_SET_MATERIAL_WRONG.getMessage());
        return false;
    }
}
