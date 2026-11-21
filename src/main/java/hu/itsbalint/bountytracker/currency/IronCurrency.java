package hu.itsbalint.bountytracker.currency;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public class IronCurrency extends Currency {
    public IronCurrency() {
        super("IRON", "Vasrúd", Material.IRON_INGOT, NamedTextColor.WHITE, 2.0);
    }
}