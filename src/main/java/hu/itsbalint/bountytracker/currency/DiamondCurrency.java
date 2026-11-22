package hu.itsbalint.bountytracker.currency;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public class DiamondCurrency extends Currency {
    public DiamondCurrency() {
        super("DIAMOND", "Gyémánt", Material.DIAMOND, NamedTextColor.AQUA, 10.0, 4, 64);
    }
}