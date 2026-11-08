package hu.itsbalint.bountytracker.listener;

import hu.itsbalint.bountytracker.model.PlayerBounty;
import hu.itsbalint.bountytracker.manager.BountyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
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

            // Kiszámoljuk a gémántok összegét (legalább 1 gyémánt jár)
            int diamondAmount = Math.max(1, (int) bounty.getPayoutAmount());

            // Reward átadása
            ItemStack rewardDiamonds = new ItemStack(Material.DIAMOND, diamondAmount);
            HashMap<Integer, ItemStack> leftover = killer.getInventory().addItem(rewardDiamonds);

            // Ha tele az inventory, a földre dobjuk a maradékot
            for (ItemStack item : leftover.values()) {
                killer.getWorld().dropItemNaturally(killer.getLocation(), item);
            }

            killer.sendMessage(Component.text("Sikeresen begyűjtötted " + victim.getName() + " fejpénzét! Jutalom: " + diamondAmount + " gyémánt!", NamedTextColor.GREEN));
            killer.getServer().sendMessage(Component.text("⚔ " + killer.getName() + " begyűjtötte a fejpénzt " + victim.getName() + " fejéről! (" + diamondAmount + " Gyémánt)", NamedTextColor.GOLD));
        }
    }
}