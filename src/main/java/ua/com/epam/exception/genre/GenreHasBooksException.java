package ua.com.epam.exception.genre;

public class GenreHasBooksException extends RuntimeException {

    private static final String MESSAGE_LAYOUT = "Cannot delete genre with id %d because some books are referenced to it";

    public GenreHasBooksException(long genreId) {
        super(String.format(MESSAGE_LAYOUT, genreId));
    }
}
