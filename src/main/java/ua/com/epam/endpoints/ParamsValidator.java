package ua.com.epam.endpoints;

import ua.com.epam.entities.SearchParams;
import ua.com.epam.entities.SearchParamsWithPagination;
import ua.com.epam.exception.params.InvalidSizeValueException;
import ua.com.epam.exception.params.InvalidSortingValueException;

import java.util.Arrays;
import java.util.List;

public class ParamsValidator {

    private static final List<String> SORTING_ORDERS = Arrays.asList("asc", "desc");
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;

    public static void validateSearchParams(SearchParams params) {
        validate(params.getOrderType(), params.getSize());
    }

    public static void validateSearchParams(SearchParamsWithPagination params) {
        validate(params.getOrderType(), params.getSize());
    }

    private static void validate(String sorting, int size) {
        if (!SORTING_ORDERS.contains(sorting)) {
            throw new InvalidSortingValueException(sorting, SORTING_ORDERS);
        }
        if (size < MIN_SIZE || MAX_SIZE < size) {
            throw new InvalidSizeValueException(size, MIN_SIZE, MAX_SIZE);
        }
    }
}
