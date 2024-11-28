package de.nyc.shopRotationRemake.listener;

import de.nyc.shopRotationRemake.Main;
import de.nyc.shopRotationRemake.enums.Messages;
import de.nyc.shopRotationRemake.objects.Quintuple;
import de.nyc.shopRotationRemake.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConnectionListener implements Listener {

    private static final Main main = Main.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        if(!player.hasPlayedBefore()) {
            return;
        }

        Map<UUID, Quintuple> pendingRewards = main.getSrDatabase().getAllPendingRewardEntries();

        if(pendingRewards == null || pendingRewards.isEmpty()) {
            return;
        }

        for(Map.Entry<UUID, Quintuple> pendingRewardsMap : pendingRewards.entrySet()) {
            UUID uuid = pendingRewardsMap.getKey();

            if(playerUuid.equals(uuid)) {
                Quintuple values = pendingRewardsMap.getValue();
                String itemUuid = values.getValue2();
                String itemString = values.getValue3();
                Integer itemAmount = Integer.valueOf(values.getValue4());
                Integer minimumAmount = Integer.valueOf(values.getValue5());

                Material itemMaterial = ItemUtils.getItemMaterial(itemString);
                String itemName = ItemUtils.getItemName(itemString);
                Map<Enchantment, Integer> itemEnchantments = ItemUtils.getItemEnchantments(itemString);
                List<String> itemDescription = ItemUtils.getItemDescription(itemString);

                ItemStack itemStack = ItemUtils.createItemStack(itemMaterial, itemName, itemEnchantments, itemDescription);

                int remainingAmount = ItemUtils.giveItemsToPlayer(player, itemStack, itemAmount);

                if(remainingAmount > 0) {
                    main.getSrDatabase().removePendingRewardEntry(playerUuid, UUID.fromString(itemUuid), itemString);
                    player.sendMessage(Messages.PLAYER_HAS_NOT_ENOUGH_INVENTORY_SPACE.getMessage());
                    main.getSrDatabase().addPendingRewardEntry(player, UUID.fromString(itemUuid), itemString, remainingAmount, minimumAmount);
                    Bukkit.getLogger().info("[68:37:98] Player had not enough invenotory space -> added \"" + remainingAmount + "\" for item \"" + itemUuid + "\"!");
                } else {
                    //Player had enough inventorySpace:
                    if (itemName.equals(String.valueOf(itemMaterial))) {
                        player.sendMessage(Messages.PLAYER_REWARD_RECIEVED_1.getMessage()
                                .replace("%amount", String.valueOf(itemAmount))
                                .replace("%itemname", itemName));
                    } else {
                        player.sendMessage(Messages.PLAYER_REWARD_RECIEVED_2.getMessage()
                                .replace("%amount", String.valueOf(itemAmount))
                                .replace("%itemname", itemName)
                                .replace("%itemmaterial", String.valueOf(itemMaterial)));
                    }
                    main.getSrDatabase().removePendingRewardEntry(playerUuid, UUID.fromString(itemUuid), itemString);
                }
                if(remainingAmount != itemAmount) {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                }
            }
        }
    }

}
