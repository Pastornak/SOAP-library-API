package ua.com.epam.exception.author;

public class AuthorNotFoundException extends RuntimeException {

    private static final String MESSAGE_LAYOUT = "Author with id %d not found";

    public AuthorNotFoundException(long authorId) {
        super(String.format(MESSAGE_LAYOUT, authorId));
    }
}
