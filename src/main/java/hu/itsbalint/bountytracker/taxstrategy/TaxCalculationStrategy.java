package hu.itsbalint.bountytracker.taxstrategy;

public interface TaxCalculationStrategy {
    double calculateFinalReward(double initialBounty);
}