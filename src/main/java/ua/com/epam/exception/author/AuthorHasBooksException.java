package ua.com.epam.exception.author;

public class AuthorHasBooksException extends RuntimeException {

    private static final String MESSAGE_LAYOUT = "Cannot delete author with id %d because some books are referenced to him";

    public AuthorHasBooksException(long authorId) {
        super(String.format(MESSAGE_LAYOUT, authorId));
    }
}
