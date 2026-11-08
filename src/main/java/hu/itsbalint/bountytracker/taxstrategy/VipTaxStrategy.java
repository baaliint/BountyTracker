package hu.itsbalint.bountytracker.taxstrategy;

public class VipTaxStrategy implements TaxCalculationStrategy {
    @Override
    public double calculateFinalReward(double initialBounty) {
        return initialBounty * 0.95;
    }
}