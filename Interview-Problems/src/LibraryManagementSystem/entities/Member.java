package LibraryManagementSystem.entities;

import java.util.ArrayList;
import java.util.List;

import LibraryManagementSystem.config.LibraryConfig;

public class Member {
    private final String id, name;
    private final List<Loan> activeLoans = new ArrayList<>();
    private final List<Loan> history = new ArrayList<>();
    private final List<Fine> fines = new ArrayList<>();

    public Member(String id, String name) { this.id = id; this.name = name; }

    // public boolean hasReachedLimit() { return activeLoans.size() >= LibraryConfig.MAX_BOOKS_PER_MEMBER; }
    // public boolean hasUnpaidFines()  { return fines.stream().anyMatch(f -> !f.isPaid()); }

    public void addLoan(Loan l)      { activeLoans.add(l); }
    public void completeLoan(Loan l) { activeLoans.remove(l); history.add(l); }
    public void addFine(Fine f)      { fines.add(f); }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Fine> getFines() { return fines; }
    public List<Loan> getHistory() { return history; }
}