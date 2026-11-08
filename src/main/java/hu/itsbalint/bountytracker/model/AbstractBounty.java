package hu.itsbalint.bountytracker.model;

import hu.itsbalint.bountytracker.taxstrategy.TaxCalculationStrategy;
import java.util.UUID;

public abstract class AbstractBounty implements Comparable<AbstractBounty> {
    private final UUID targetId;
    private final UUID issuerId;
    protected double amount;
    private transient TaxCalculationStrategy taxStrategy;

    public AbstractBounty(UUID targetId, UUID issuerId, double amount, TaxCalculationStrategy taxStrategy) {
        if (amount <= 0) {
            throw new IllegalArgumentException("A fejpénznek pozitívnak kell lennie!");
        }
        this.targetId = targetId;
        this.issuerId = issuerId;
        this.amount = amount;
        this.taxStrategy = taxStrategy;
    }

    public UUID getTargetId() { return targetId; }
    public UUID getIssuerId() { return issuerId; }
    public double getAmount() { return amount; }

    public void setTaxStrategy(TaxCalculationStrategy taxStrategy) {
        this.taxStrategy = taxStrategy;
    }

    public double getPayoutAmount() {
        if (taxStrategy == null) return amount;
        return taxStrategy.calculateFinalReward(amount);
    }

    public abstract String getBountyType();

    @Override
    public int compareTo(AbstractBounty other) {
        return Double.compare(other.getAmount(), this.amount);
    }
}