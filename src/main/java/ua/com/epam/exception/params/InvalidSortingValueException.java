package ua.com.epam.exception.params;

import java.util.List;

public class InvalidSortingValueException extends RuntimeException {

    private static final String MESSAGE_LAYOUT = "Sorting value %s is invalid, available sorting values: %s";

    public InvalidSortingValueException(String actualValue, List<String> expectedValues) {
        super(String.format(MESSAGE_LAYOUT, actualValue, expectedValues.stream().reduce((a, b) -> a + ", " + b).orElse("")));
    }
}
