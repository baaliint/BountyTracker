package hu.itsbalint.bountytracker.model;

import hu.itsbalint.bountytracker.currency.Currency;
import hu.itsbalint.bountytracker.currency.DiamondCurrency;
import hu.itsbalint.bountytracker.taxstrategy.TaxCalculationStrategy;

import java.util.UUID;

public abstract class Bounty implements Comparable<Bounty> {
    private final UUID targetId;
    private final UUID issuerId;
    protected double amount;
    private final String currencyId;
    private transient TaxCalculationStrategy taxStrategy;

    public Bounty(UUID targetId, UUID issuerId, double amount, Currency currency, TaxCalculationStrategy taxStrategy) {
        if (amount <= 0) {
            throw new IllegalArgumentException("A fejpénznek pozitívnak kell lennie!");
        }
        this.targetId = targetId;
        this.issuerId = issuerId;
        this.amount = amount;
        this.currencyId = currency.getId();
        this.taxStrategy = taxStrategy;
    }

    public UUID getTargetId() { return targetId; }
    public UUID getIssuerId() { return issuerId; }
    public double getAmount() { return amount; }

    public Currency getCurrency() {
        Currency currency = Currency.fromInput(currencyId);
        return currency != null ? currency : new DiamondCurrency();
    }

    public void setTaxStrategy(TaxCalculationStrategy taxStrategy) {
        this.taxStrategy = taxStrategy;
    }

    public double getPayoutAmount() {
        if (taxStrategy == null) return amount;
        return taxStrategy.calculateFinalReward(amount);
    }

    public abstract String getBountyType();

    @Override
    public int compareTo(Bounty other) {
        double thisTotalValue = this.amount * this.getCurrency().getValueMultiplier();
        double otherTotalValue = other.getAmount() * other.getCurrency().getValueMultiplier();
        return Double.compare(otherTotalValue, thisTotalValue);
    }
}