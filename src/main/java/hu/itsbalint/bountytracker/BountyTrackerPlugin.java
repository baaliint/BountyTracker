package hu.itsbalint.bountytracker;

import hu.itsbalint.bountytracker.command.BountyCommand;
import hu.itsbalint.bountytracker.listener.BountyListener;
import hu.itsbalint.bountytracker.manager.BountyManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class BountyTrackerPlugin extends JavaPlugin {
    private BountyManager bountyManager;
    private BountyDataStorage dataStorage;

    @Override
    public void onEnable() {
        bountyManager = new BountyManager();
        dataStorage = new BountyDataStorage(new File(getDataFolder(), "bounties.json"));
        getServer().getPluginManager().registerEvents(new BountyListener(bountyManager), this);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        try {
            bountyManager.setAllBounties(dataStorage.loadBounties());
            getSLF4JLogger().info("Körözések sikeresen betöltve!");
        } catch (IOException e) {
            getSLF4JLogger().error("Nem sikerült betölteni a körözéseket: ", e);
        }

        if (getCommand("bounty") != null) {
            getCommand("bounty").setExecutor(new BountyCommand(bountyManager));
        }

        getSLF4JLogger().info("BountyTracker plugin bekapcsolva!");
    }

    @Override
    public void onDisable() {
        try {
            dataStorage.saveBounties(bountyManager.getAllBounties());
            getSLF4JLogger().info("Körözések sikeresen elmentve!");
        } catch (IOException e) {
            getSLF4JLogger().error("Nem sikerült elmenteni a körözéseket: ", e);
        }

        getSLF4JLogger().info("BountyTracker plugin kikapcsolva!");
    }
}