package LibraryManagementSystem.entities;

public class Fine {
    private final Loan loan;
    private final double amount;
    private boolean paid;

    public Fine(Loan loan, double amount) { this.loan = loan; this.amount = amount; }
    public void pay() { this.paid = true; }
    public boolean isPaid() { return paid; }
    public double getAmount() { return amount; }
}
