package ua.com.epam.exception.genre;

public class GenreNotFoundException extends RuntimeException {

    private static final String MESSAGE_LAYOUT = "Genre with id %d not found";

    public GenreNotFoundException(long genreId) {
        super(String.format(MESSAGE_LAYOUT, genreId));
    }
}
