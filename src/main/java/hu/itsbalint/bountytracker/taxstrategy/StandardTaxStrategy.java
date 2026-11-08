package hu.itsbalint.bountytracker.taxstrategy;

public class StandardTaxStrategy implements TaxCalculationStrategy {
    @Override
    public double calculateFinalReward(double initialBounty) {
        return initialBounty * 0.90;
    }
}