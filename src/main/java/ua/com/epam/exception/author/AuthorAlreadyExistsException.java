package ua.com.epam.exception.author;

public class AuthorAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE_LAYOUT = "Author with id %d already exists";

    public AuthorAlreadyExistsException(long authorId) {
        super(String.format(MESSAGE_LAYOUT, authorId));
    }
}
