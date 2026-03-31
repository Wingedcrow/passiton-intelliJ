package com.cts.passiton;

public class UserSession {

    private static int userId;
    private static String email;

    public static void startSession(int id, String userEmail) {
        userId = id;
        email = userEmail;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getEmail() {
        return email;
    }

    public static void clearSession() {
        userId = 0;
        email = null;
    }
}
