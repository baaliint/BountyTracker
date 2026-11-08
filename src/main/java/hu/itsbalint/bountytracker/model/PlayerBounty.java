package hu.itsbalint.bountytracker.model;

import hu.itsbalint.bountytracker.taxstrategy.TaxCalculationStrategy;
import java.util.UUID;

public class PlayerBounty extends AbstractBounty {
    private final boolean isPvpEvent;

    public PlayerBounty(UUID targetId, UUID issuerId, double amount, TaxCalculationStrategy taxStrategy, boolean isPvpEvent) {
        super(targetId, issuerId, amount, taxStrategy);
        this.isPvpEvent = isPvpEvent;
    }

    @Override
    public String getBountyType() {
        return isPvpEvent ? "PVP Event Bounty" : "Standard Player Bounty";
    }
}