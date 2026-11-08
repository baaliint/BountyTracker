package hu.itsbalint.bountytracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import hu.itsbalint.bountytracker.model.PlayerBounty;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BountyDataStorage {
    private final File dataFile;
    private final Gson gson;

    public BountyDataStorage(File dataFile) {
        this.dataFile = dataFile;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveBounties(List<PlayerBounty> bounties) throws IOException {
        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(bounties, writer);
        }
    }

    public List<PlayerBounty> loadBounties() throws IOException {
        if (!dataFile.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(dataFile)) {
            Type listType = new TypeToken<ArrayList<PlayerBounty>>() {}.getType();
            List<PlayerBounty> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        }
    }
}