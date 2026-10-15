package LibraryManagementSystem.entities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import LibraryManagementSystem.config.LibraryConfig;

public class Loan {
    private static int counter = 0;
    private final String loanId = "L" + (++counter);
    private final Member member;
    private final BookCopy copy;
    private final LocalDate issueDate, dueDate;
    private LocalDate returnDate;

    public Loan(Member member, BookCopy copy, LocalDate issueDate) {
        this.member = member; 
        this.copy = copy;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(LibraryConfig.LOAN_DAYS);
    }
    public void markReturned(LocalDate date) { this.returnDate = date; }
    public long overdueDays() {
        if (returnDate == null) return 0;
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, returnDate));
    }

    public String getLoanId() { return loanId; }
    public Member getMember() { return member; }
    public BookCopy getCopy() { return copy; }
    public LocalDate getDueDate() { return dueDate; }
}
