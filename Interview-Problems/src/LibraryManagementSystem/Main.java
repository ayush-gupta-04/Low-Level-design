package LibraryManagementSystem;

import java.time.LocalDate;
import java.util.Optional;

import LibraryManagementSystem.entities.Book;
import LibraryManagementSystem.entities.Loan;
import LibraryManagementSystem.entities.Member;
import LibraryManagementSystem.service.Library;
import LibraryManagementSystem.strategy.finepolicy.PerDayFinePolicy;
import LibraryManagementSystem.strategy.notification.ConsoleNotificationService;

public class Main {
    public static void main(String[] args) {
        Library lib = new Library(new PerDayFinePolicy(5), new ConsoleNotificationService());

        lib.getCatalog().addBook(new Book("111", "Clean Code", "Robert Martin", "Programming"), 1);
        lib.registerMember(new Member("M1", "Ayush"));
        lib.registerMember(new Member("M2", "Rahul"));

        Optional<Loan> loan = lib.borrowBook("M1", "111");
        lib.reserveBook("M2", "111");                       // no copy left, so Rahul queues

        // Ayush returns 4 days late
        lib.returnBook(loan.get().getLoanId(), LocalDate.now().plusDays(18));
        // -> fine Rs. 20 notified, Rahul notified that the book is ready

        Optional<Loan> l2 = lib.borrowBook("M2", "111");              // Rahul gets the held copy
        System.out.println("Rahul borrowed: " + l2.get().getLoanId());
    }
}