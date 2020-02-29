package ua.com.epam.exception.genre;

public class GenreNameAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE_LAYOUT = "Genre with name %s already exists";

    public GenreNameAlreadyExistsException(String name) {
        super(String.format(MESSAGE_LAYOUT, name));
    }
}
