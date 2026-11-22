package hu.itsbalint.bountytracker.command;

import hu.itsbalint.bountytracker.currency.Currency;
import hu.itsbalint.bountytracker.exception.InvalidBountyException;
import hu.itsbalint.bountytracker.manager.BountyManager;
import hu.itsbalint.bountytracker.model.PlayerBounty;
import hu.itsbalint.bountytracker.taxstrategy.StandardTaxStrategy;
import hu.itsbalint.bountytracker.taxstrategy.VipTaxStrategy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BountyCommand implements TabExecutor {
    private final BountyManager bountyManager;

    public BountyCommand(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Használat: /bounty add <játékos> <érc> <összeg> VAGY /bounty list", NamedTextColor.RED));
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

                Currency currency = bounty.getCurrency();
                sender.sendMessage(Component.text(targetName + " - ", NamedTextColor.YELLOW)
                        .append(Component.text((int) bounty.getPayoutAmount() + " " + currency.getDisplayName(), currency.getColor())));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 4) {
                sender.sendMessage(Component.text("Használat: /bounty add <játékos> <gyemant|smaragd|arany|vas> <összeg>", NamedTextColor.RED));
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

            if (bountyManager.hasBounty(target.getUniqueId())) {
                sender.sendMessage(Component.text("Ennek a játékosnak a fejére már van kitűzve egy aktív fejpénz!", NamedTextColor.RED));
                return true;
            }

            Currency currency = Currency.fromInput(args[2]);
            if (currency == null) {
                sender.sendMessage(Component.text("Érvénytelen érc! Választható: gyemant, smaragd, arany, vas", NamedTextColor.RED));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Kérlek egy érvényes számot adj meg " + currency.getMinAmount() + " és " + currency.getMaxAmount() + " között!", NamedTextColor.RED));
                return true;
            }

            if (amount < currency.getMinAmount() || amount > currency.getMaxAmount()) {
                sender.sendMessage(Component.text(currency.getDisplayName() + " esetén a fejpénz összege " + currency.getMinAmount() + " és " + currency.getMaxAmount() + " között kell, hogy legyen!", NamedTextColor.RED));
                return true;
            }

            if (!player.getInventory().containsAtLeast(new ItemStack(currency.getMaterial()), amount)) {
                sender.sendMessage(Component.text("Nincs elég " + currency.getDisplayName() + " a leltáradban! Szükséges: " + amount + " db.", NamedTextColor.RED));
                return true;
            }

            try {
                var strategy = player.hasPermission("bounty.vip") ? new VipTaxStrategy() : new StandardTaxStrategy();
                PlayerBounty bounty = new PlayerBounty(target.getUniqueId(), player.getUniqueId(), amount, currency, strategy, false);

                bountyManager.addBounty(bounty);
                player.getInventory().removeItem(new ItemStack(currency.getMaterial(), amount));

                sender.sendMessage(Component.text("Sikeresen kitűzted a fejpénzt " + target.getName() + " fejére!", NamedTextColor.GREEN));

                Bukkit.broadcast(Component.text(player.getName(), NamedTextColor.YELLOW)
                        .append(Component.text(" fejpénzt tűzött ki ", NamedTextColor.RED))
                        .append(Component.text(target.getName(), NamedTextColor.DARK_RED))
                        .append(Component.text(" fejére! Jutalom: ", NamedTextColor.RED))
                        .append(Component.text(amount + " " + currency.getDisplayName(), currency.getColor()))
                        .append(Component.text("!", NamedTextColor.RED)));

            } catch (InvalidBountyException e) {
                sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
            }
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return filterStartingWith(List.of("add", "list"), args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            return null;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            return filterStartingWith(List.of("gyemant", "smaragd", "arany", "vas"), args[2]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("add")) {
            Currency currency = Currency.fromInput(args[2]);
            if (currency != null) {
                int min = currency.getMinAmount();
                int max = currency.getMaxAmount();
                int mid = (min + max) / 2;
                return filterStartingWith(List.of(String.valueOf(min), String.valueOf(mid), String.valueOf(max)), args[3]);
            }
        }

        return List.of();
    }

    private List<String> filterStartingWith(List<String> options, String prefix) {
        List<String> result = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(option);
            }
        }
        return result;
    }
}