package hu.itsbalint.bountytracker.listener;

import hu.itsbalint.bountytracker.currency.Currency;
import hu.itsbalint.bountytracker.manager.BountyManager;
import hu.itsbalint.bountytracker.model.PlayerBounty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;

public class BountyListener implements Listener {
    private final BountyManager bountyManager;

    public BountyListener(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null || killer.equals(victim)) {
            return;
        }

        Optional<PlayerBounty> bountyOptional = bountyManager.claimBounty(victim.getUniqueId());

        if (bountyOptional.isPresent()) {
            PlayerBounty bounty = bountyOptional.get();
            Currency currency = bounty.getCurrency();
            // Kiszámoljuk a jutalom összegét (legalább 1)
            int rewardAmount = Math.max(1, (int) bounty.getPayoutAmount());

            ItemStack rewardItems = new ItemStack(currency.getMaterial(), rewardAmount);
            HashMap<Integer, ItemStack> leftover = killer.getInventory().addItem(rewardItems);

            for (ItemStack item : leftover.values()) {
                killer.getWorld().dropItemNaturally(killer.getLocation(), item);
            }

            killer.sendMessage(Component.text("Sikeresen begyűjtötted " + victim.getName() + " fejpénzét! Jutalom: " + rewardAmount + " " + currency.getDisplayName() + "!", NamedTextColor.GREEN));
            killer.getServer().sendMessage(Component.text(killer.getName() + " begyűjtötte a fejpénzt " + victim.getName() + " fejéről! (", NamedTextColor.GOLD)
                    .append(Component.text(rewardAmount + " " + currency.getDisplayName(), currency.getColor()))
                    .append(Component.text(")", NamedTextColor.GOLD)));
        }
    }
}