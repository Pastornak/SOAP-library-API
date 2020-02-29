package ua.com.epam.exception.book;

public class BookNotFoundException extends RuntimeException {

    private static final String MESSAGE_LAYOUT = "Book with id %d not found";

    public BookNotFoundException(long bookId) {
        super(String.format(MESSAGE_LAYOUT, bookId));
    }
}
