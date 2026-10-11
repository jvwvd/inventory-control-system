package util;

import exception.ValidationException;

public final class ValidationUtil {

    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private static final String PHONE_PATTERN =
            "^[0-9+()\\-\\s]{7,20}$";

    private ValidationUtil() {
    }

    public static void requireNonBlank(
            String value,
            String fieldName
    ) throws ValidationException {

        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    fieldName + " is required."
            );
        }
    }

    public static void requireNotNull(
            Object value,
            String fieldName
    ) throws ValidationException {

        if (value == null) {
            throw new ValidationException(
                    fieldName + " is required."
            );
        }
    }

    public static void requirePositive(
            int value,
            String fieldName
    ) throws ValidationException {

        if (value <= 0) {
            throw new ValidationException(
                    fieldName
                            + " must be greater than zero."
            );
        }
    }

    public static void requireNonNegative(
            int value,
            String fieldName
    ) throws ValidationException {

        if (value < 0) {
            throw new ValidationException(
                    fieldName
                            + " cannot be negative."
            );
        }
    }

    public static void requirePositive(
            double value,
            String fieldName
    ) throws ValidationException {

        if (Double.isNaN(value)
                || Double.isInfinite(value)
                || value <= 0) {

            throw new ValidationException(
                    fieldName
                            + " must be greater than zero."
            );
        }
    }

    public static void requireNonNegative(
            double value,
            String fieldName
    ) throws ValidationException {

        if (Double.isNaN(value)
                || Double.isInfinite(value)
                || value < 0) {

            throw new ValidationException(
                    fieldName
                            + " cannot be negative."
            );
        }
    }

    public static void validateOptionalEmail(
            String email
    ) throws ValidationException {

        if (email == null || email.isBlank()) {
            return;
        }

        if (!email.trim().matches(EMAIL_PATTERN)) {
            throw new ValidationException(
                    "Email format is invalid."
            );
        }
    }

    public static void validateOptionalPhone(
            String phone
    ) throws ValidationException {

        if (phone == null || phone.isBlank()) {
            return;
        }

        if (!phone.trim().matches(PHONE_PATTERN)) {
            throw new ValidationException(
                    "Phone number format is invalid."
            );
        }
    }
}