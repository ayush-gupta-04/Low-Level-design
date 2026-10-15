package LibraryManagementSystem.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Catalog {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, List<BookCopy>> copies = new HashMap<>();

    public void addBook(Book book, int numCopies) {
        books.putIfAbsent(book.getIsbn(), book);
        List<BookCopy> list = copies.computeIfAbsent(book.getIsbn(), k -> new ArrayList<>());
        for (int i = 0; i < numCopies; i++){
            list.add(new BookCopy(book.getIsbn() + "-" + (list.size() + 1), book));
        }    
    }

    public void removeBook(String isbn) { books.remove(isbn); copies.remove(isbn); }

    public Book getBook(String isbn) { return books.get(isbn); }

    public Optional<BookCopy> findIssuableCopy(String isbn, Member m) {
        return copies.getOrDefault(isbn, List.of()).stream()
                     .filter(c -> c.isIssuableTo(m)).findFirst();
    }

    public List<Book> searchByTitle(String t)    { return books.values().stream().filter(b -> b.getTitle().toLowerCase().contains(t.toLowerCase())).collect(Collectors.toList()); }
    public List<Book> searchByAuthor(String a)   { return books.values().stream().filter(b -> b.getAuthor().toLowerCase().contains(a.toLowerCase())).collect(Collectors.toList()); }
    public List<Book> searchByCategory(String c) { return books.values().stream().filter(b -> b.getCategory().equalsIgnoreCase(c)).collect(Collectors.toList()); }
    public Book searchByIsbn(String isbn)        { return books.get(isbn); }
}
