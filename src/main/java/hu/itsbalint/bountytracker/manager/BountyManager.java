package hu.itsbalint.bountytracker.manager;

import hu.itsbalint.bountytracker.exception.InvalidBountyException;
import hu.itsbalint.bountytracker.model.PlayerBounty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BountyManager {
    private final List<PlayerBounty> activeBounties = new ArrayList<>();

    public void addBounty(PlayerBounty bounty) throws InvalidBountyException {
        if (bounty.getTargetId().equals(bounty.getIssuerId())) {
            throw new InvalidBountyException("Saját magadra nem tűzhetsz ki fejpénzt!");
        }
        if (hasBounty(bounty.getTargetId())) {
            throw new InvalidBountyException("Ennek a játékosnak a fejére már van kitűzve aktív fejpénz!");
        }
        activeBounties.add(bounty);
    }

    // Ellenőrzi, hogy van-e már aktív fejpénz a célponton
    public boolean hasBounty(UUID targetId) {
        return activeBounties.stream().anyMatch(b -> b.getTargetId().equals(targetId));
    }

    public Optional<PlayerBounty> claimBounty(UUID targetId) {
        for (PlayerBounty bounty : new ArrayList<>(activeBounties)) {
            if (bounty.getTargetId().equals(targetId)) {
                activeBounties.remove(bounty);
                return Optional.of(bounty);
            }
        }
        return Optional.empty();
    }

    public List<PlayerBounty> getTopBounties() {
        List<PlayerBounty> sortedList = new ArrayList<>(activeBounties);
        Collections.sort(sortedList);
        return sortedList;
    }

    public List<PlayerBounty> getAllBounties() {
        return activeBounties;
    }

    public void setAllBounties(List<PlayerBounty> bounties) {
        this.activeBounties.clear();
        if (bounties != null) {
            this.activeBounties.addAll(bounties);
        }
    }
}