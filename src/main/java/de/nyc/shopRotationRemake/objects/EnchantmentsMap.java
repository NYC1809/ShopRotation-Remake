package de.nyc.shopRotationRemake.objects;

public class EnchantmentsMap {
    private String enchantment;
    private Integer level;

    public EnchantmentsMap(String enchantment, Integer level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public String getEnchantment() {
        return enchantment;
    }

    public Integer getLevel() {
        return level;
    }


    @Override
    public String toString() {
        return "EnchantmentsMap{" +
                "enchantment='" + enchantment + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
