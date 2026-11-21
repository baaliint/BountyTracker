package hu.itsbalint.bountytracker.currency;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public abstract class Currency {
    private final String id;
    private final String displayName;
    private final Material material;
    private final NamedTextColor color;
    private final double valueMultiplier;

    public Currency(String id, String displayName, Material material, NamedTextColor color, double valueMultiplier) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.color = color;
        this.valueMultiplier = valueMultiplier;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Material getMaterial() { return material; }
    public NamedTextColor getColor() { return color; }
    public double getValueMultiplier() { return valueMultiplier; }

    public static Currency fromInput(String input) {
        if (input == null) return null;
        return switch (input.toLowerCase().trim()) {
            case "gyemant", "gyémánt", "diamond" -> new DiamondCurrency();
            case "smaragd", "emerald" -> new EmeraldCurrency();
            case "arany", "gold" -> new GoldCurrency();
            case "vas", "iron" -> new IronCurrency();
            default -> null;
        };
    }
}