package ua.com.epam.exception.book;

public class BookAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE_LAYOUT = "Book with id %d already exists";

    public BookAlreadyExistsException(long bookId) {
        super(String.format(MESSAGE_LAYOUT, bookId));
    }
}
