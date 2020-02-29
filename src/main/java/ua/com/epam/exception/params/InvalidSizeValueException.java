package ua.com.epam.exception.params;

public class InvalidSizeValueException extends RuntimeException{

    private static final String MESSAGE_LAYOUT = "Size value %d is invalid, choose value between %d and %d";

    public InvalidSizeValueException(int actualSize, int minSize, int maxSize) {
        super(String.format(MESSAGE_LAYOUT, actualSize, minSize, maxSize));
    }
}
