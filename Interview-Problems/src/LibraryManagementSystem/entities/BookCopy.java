package LibraryManagementSystem.entities;

import LibraryManagementSystem.enums.CopyStatus;

public class BookCopy {
    private final String copyId;
    private final Book book;
    private CopyStatus status = CopyStatus.AVAILABLE;
    private Member reservedFor;            // set only when status == RESERVED

    public BookCopy(String copyId, Book book) { this.copyId = copyId; this.book = book; }

    public boolean isIssuableTo(Member m) {
        return status == CopyStatus.AVAILABLE || (status == CopyStatus.RESERVED && reservedFor == m);
    }
    public void markIssued() { status = CopyStatus.ISSUED; reservedFor = null; }
    public void markAvailable(){ status = CopyStatus.AVAILABLE; reservedFor = null; }
    public void markReserved(Member m){ status = CopyStatus.RESERVED; reservedFor = m; }

    public String getCopyId() { return copyId; }
    public Book getBook() { return book; }
    public CopyStatus getStatus() { return status; }
}
