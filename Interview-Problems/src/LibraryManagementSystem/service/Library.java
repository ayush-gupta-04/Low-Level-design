package LibraryManagementSystem.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import LibraryManagementSystem.entities.Book;
import LibraryManagementSystem.entities.BookCopy;
import LibraryManagementSystem.entities.Catalog;
import LibraryManagementSystem.entities.Fine;
import LibraryManagementSystem.entities.Loan;
import LibraryManagementSystem.entities.Member;
import LibraryManagementSystem.entities.Reservation;
import LibraryManagementSystem.strategy.FinePolicy;
import LibraryManagementSystem.strategy.NotificationService;

public class Library {
    private final Catalog catalog = new Catalog();
    private final Map<String, Member> members = new HashMap<>();
    private final Map<String, Loan> activeLoans = new HashMap<>();
    private final Map<String, Queue<Reservation>> reservations = new HashMap<>();  // isbn -> queue<reservation>
    private final FinePolicy finePolicy;
    private final NotificationService notifier;

    public  Library(FinePolicy finePolicy, NotificationService notifier) {
        this.finePolicy = finePolicy; this.notifier = notifier;
    }

    // ---------- setup ----------
    public  Catalog getCatalog() { return catalog; }
    public  void registerMember(Member m) { members.put(m.getId(), m); }

    // ---------- borrow ----------
    public  synchronized Optional<Loan> borrowBook(String memberId, String isbn) {
        Member member = getMember(memberId);

        // if (member.hasUnpaidFines()){System.out.println("Clear pending fines first"); return Optional.empty();}
        // if (member.hasReachedLimit()){System.out.println("Borrowing limit reached"); return Optional.empty();}

        Optional<BookCopy> copy = catalog.findIssuableCopy(isbn, member);
        if(copy.isEmpty()){System.out.println("No Copy Available! for : " + isbn); return Optional.empty();}

        copy.get().markIssued();
        Loan loan = new Loan(member, copy.get(), LocalDate.now());
        member.addLoan(loan);
        activeLoans.put(loan.getLoanId(), loan);

        // if this member was waiting in the queue, remove their reservation
        Queue<Reservation> q = reservations.get(isbn);
        if (q != null) q.removeIf(r -> r.getMember() == member);

        return Optional.of(loan);
    }

    // ---------- return ----------
    public synchronized void returnBook(String loanId, LocalDate returnDate) {
        Loan loan = activeLoans.remove(loanId);
        if (loan == null) return;

        loan.markReturned(returnDate);
        Member member = loan.getMember();
        member.completeLoan(loan);

        double amount = finePolicy.calculate(loan.overdueDays());
        if (amount > 0) {
            member.addFine(new Fine(loan, amount));
            notifier.notify(member, "Late return. Fine: Rs. " + amount);
        }

        // book copy is returned.
        // reserve it for the next in queue. 
        BookCopy copy = loan.getCopy();
        Queue<Reservation> q = reservations.get(copy.getBook().getIsbn());
        Reservation next = (q == null) ? null : q.poll();
        if (next != null) {
            copy.markReserved(next.getMember());
            notifier.notify(next.getMember(), "'" + copy.getBook().getTitle() + "' is now available for you");
        } else {
            copy.markAvailable();
        }
    }

    // ---------- reserve ----------
    public  synchronized void reserveBook(String memberId, String isbn) {
        Member member = getMember(memberId);
        Book book = catalog.getBook(isbn);

        // if book copy exist and issuable .. then take it.
        if (catalog.findIssuableCopy(isbn, member).isPresent()) return;

        // if already reservation exist, return.
        Queue<Reservation> q = reservations.computeIfAbsent(isbn, k -> new LinkedList<>());
        boolean alreadyQueued = q.stream().anyMatch(r -> r.getMember() == member);
        if (alreadyQueued){
            System.out.println("Already reserved!");
            return;
        }

        // add reservation.
        q.add(new Reservation(member, book));
    }

    private Member getMember(String id) {
        Member m = members.get(id);
        if (m == null) throw new IllegalArgumentException("Member not found");
        return m;
    }
}