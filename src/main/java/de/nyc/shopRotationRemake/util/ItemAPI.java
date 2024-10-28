package de.nyc.shopRotationRemake.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemAPI extends ItemStack {

    ItemStack itemStack;
    ItemMeta itemMeta;

    public ItemAPI(String displayname, ArrayList<String> lore, Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayname);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public ItemAPI(String displayname, Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayname);
        itemStack.setItemMeta(itemMeta);
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
