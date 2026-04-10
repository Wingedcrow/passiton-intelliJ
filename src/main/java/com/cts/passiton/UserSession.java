package com.cts.passiton;

/**
 * UserSession.java records every current logged-in user.
 * This class was made to assist with the handling of deleting an account through SettingsController.java
 *
 * @author Bradley Balram & Joshua Howard
 * @version 1.0
 * @date (10/04/2026)
 */

public class UserSession {

    private static int userId;
    private static String password;

    public static void startSession(int id, String userPass) {
        userId = id;
        password = userPass;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getPass() {
        return password;
    }

    public static void clearSession() {
        userId = 0;
        password = null;
    }
}
