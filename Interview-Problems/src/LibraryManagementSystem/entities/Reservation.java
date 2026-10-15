package LibraryManagementSystem.entities;

public class Reservation {
    private final Member member;
    private final Book book;
    public Reservation(Member member, Book book) { this.member = member; this.book = book; }
    public Member getMember() { return member; }
    public Book getBook() { return book; }
}