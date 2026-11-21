package hu.itsbalint.bountytracker.currency;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public class GoldCurrency extends Currency {
    public GoldCurrency() {
        super("GOLD", "Aranyrúd", Material.GOLD_INGOT, NamedTextColor.GOLD, 4.0);
    }
}