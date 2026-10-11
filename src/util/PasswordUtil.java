package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtil {

    private static final String HASH_ALGORITHM =
            "SHA-256";

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException(
                    "Password cannot be null."
            );
        }

        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance(
                            HASH_ALGORITHM
                    );

            byte[] passwordBytes =
                    password.getBytes(
                            StandardCharsets.UTF_8
                    );

            byte[] hashBytes =
                    messageDigest.digest(passwordBytes);

            return convertToHex(hashBytes);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    exception
            );
        }
    }

    public static boolean verifyPassword(
            String plainPassword,
            String storedPasswordHash
    ) {
        if (plainPassword == null
                || storedPasswordHash == null) {
            return false;
        }

        String enteredPasswordHash =
                hashPassword(plainPassword);

        return enteredPasswordHash.equals(
                storedPasswordHash
        );
    }

    private static String convertToHex(
            byte[] hashBytes
    ) {
        StringBuilder hexString =
                new StringBuilder();

        for (byte hashByte : hashBytes) {
            String hex =
                    Integer.toHexString(
                            hashByte & 0xff
                    );

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }
}