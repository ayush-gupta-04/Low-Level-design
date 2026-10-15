package LibraryManagementSystem.strategy.finepolicy;

import LibraryManagementSystem.strategy.FinePolicy;

public class PerDayFinePolicy implements FinePolicy {
    private final double ratePerDay;
    public PerDayFinePolicy(double ratePerDay) { this.ratePerDay = ratePerDay; }
    public double calculate(long overdueDays) { return overdueDays * ratePerDay; }
}