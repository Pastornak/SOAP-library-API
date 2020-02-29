package ua.com.epam.exception.genre;

public class GenreAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE_LAYOUT = "Genre with id %d already exists";

    public GenreAlreadyExistsException(long genreId) {
        super(String.format(MESSAGE_LAYOUT, genreId));
    }
}
