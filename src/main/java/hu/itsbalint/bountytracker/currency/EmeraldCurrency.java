package hu.itsbalint.bountytracker.currency;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public class EmeraldCurrency extends Currency {
    public EmeraldCurrency() {
        super("EMERALD", "Smaragd", Material.EMERALD, NamedTextColor.GREEN, 8.0, 4, 96);
    }
}