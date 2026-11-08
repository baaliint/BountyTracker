package hu.itsbalint.bountytracker.command;

import hu.itsbalint.bountytracker.exception.InvalidBountyException;
import hu.itsbalint.bountytracker.manager.BountyManager;
import hu.itsbalint.bountytracker.model.PlayerBounty;
import hu.itsbalint.bountytracker.taxstrategy.StandardTaxStrategy;
import hu.itsbalint.bountytracker.taxstrategy.VipTaxStrategy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BountyCommand implements CommandExecutor {
    private static final int MIN_BOUNTY = 8;
    private static final int MAX_BOUNTY = 64;

    private final BountyManager bountyManager;

    public BountyCommand(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Használat: /bounty add <játékos> <gyémánt_összeg> VAGY /bounty list", NamedTextColor.RED));
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            List<PlayerBounty> topBounties = bountyManager.getTopBounties();

            if (topBounties.isEmpty()) {
                sender.sendMessage(Component.text("Jelenleg senkin nincs fejpénz.", NamedTextColor.GRAY));
                return true;
            }

            sender.sendMessage(Component.text("=== Aktív Körözések ===", NamedTextColor.GOLD));
            for (PlayerBounty bounty : topBounties) {
                String targetName = Bukkit.getOfflinePlayer(bounty.getTargetId()).getName();
                if (targetName == null) targetName = "Ismeretlen";

                Component line = Component.text(targetName + " - ", NamedTextColor.YELLOW)
                        .append(Component.text((int) bounty.getPayoutAmount() + " Gyémánt", NamedTextColor.AQUA));
                sender.sendMessage(line);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage(Component.text("Használat: /bounty add <játékos> <" + MIN_BOUNTY + "-" + MAX_BOUNTY + ">", NamedTextColor.RED));
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Ezt a parancsot csak játékos futtathatja!", NamedTextColor.RED));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Component.text("Játékos nem található!", NamedTextColor.RED));
                return true;
            }

            // Ellenőrizzük, hogy van-e már rajta bounty
            if (bountyManager.hasBounty(target.getUniqueId())) {
                sender.sendMessage(Component.text("Ennek a játékosnak a fejére már van kitűzve egy aktív fejpénz!", NamedTextColor.RED));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Kérlek, egy érvényes számot adj meg " + MIN_BOUNTY + " és " + MAX_BOUNTY + " között!", NamedTextColor.RED));
                return true;
            }

            if (amount < MIN_BOUNTY || amount > MAX_BOUNTY) {
                sender.sendMessage(Component.text("A fejpénz összege " + MIN_BOUNTY + " és " + MAX_BOUNTY + " gyémánt között kell, hogy legyen!", NamedTextColor.RED));
                return true;
            }

            if (!player.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), amount)) {
                sender.sendMessage(Component.text("Nincs elég gyémántod! Szükséges: " + amount + " gyémánt.", NamedTextColor.RED));
                return true;
            }

            try {
                var strategy = player.hasPermission("bounty.vip") ? new VipTaxStrategy() : new StandardTaxStrategy();
                PlayerBounty bounty = new PlayerBounty(target.getUniqueId(), player.getUniqueId(), amount, strategy, false);

                bountyManager.addBounty(bounty);

                // Remove diamonds
                player.getInventory().removeItem(new ItemStack(Material.DIAMOND, amount));

                // Sikeres bounty kitűzés
                sender.sendMessage(Component.text("Sikeresen kitűzted a fejpénzt " + target.getName() + " fejére!", NamedTextColor.GREEN));

                // Bounty kitűzés broadcast
                Component broadcastMessage = Component.text(player.getName(), NamedTextColor.YELLOW)
                        .append(Component.text(" fejpénzt tűzött ki ", NamedTextColor.RED))
                        .append(Component.text(target.getName(), NamedTextColor.DARK_RED))
                        .append(Component.text(" fejére! Jutalom: ", NamedTextColor.RED))
                        .append(Component.text(amount + " Gyémánt", NamedTextColor.AQUA));

                Bukkit.broadcast(broadcastMessage);

            } catch (InvalidBountyException e) {
                sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
            }
            return true;
        }

        return false;
    }
}