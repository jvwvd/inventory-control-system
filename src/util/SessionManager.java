package util;

import model.InventoryUser;

public class SessionManager {

    private static InventoryUser currentUser;

    private SessionManager() {
        // Prevent object creation.
    }

    public static void setCurrentUser(
            InventoryUser user
    ) {
        currentUser = user;
    }

    public static InventoryUser getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clearSession() {
        currentUser = null;
    }
}