package com.cts.passiton;

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
