package com.cts.passiton;

/**
 * CurrentLogin.java
 * This class is storing currently logged in user details
 * for the duration of their login.
 * Static fields were used so the information can be accessed from any controller
 * the alternative research was to pass user information between screens
 * but the current class was created for project wide availability.
 *
 * Used in
 *  -  PassItOnAppController.java
 *  -  PostRequestController.java
 *  -  MarketboardController.java
 *  -  StudentDashboardController.java
 *  -  TradesController.java
 *
 * @author Joshua Howard & Bradley Balram
 * @version 1.0
 * @date (08/04/2026)
*/

public class CurrentLogin {

    private static int userId;
    private static String firstName;
    private static String lastName;
    private static String email;

    //Stores the student details when a user successfully logs in
    public static void setLogin(int id, String first, String last , String eAddress){
        userId = id;
        firstName = first;
        lastName = last;
        email = eAddress;
    }

    //Clearing the details when the user logs out
    public static void clearLogin() {
        userId = 0;
        firstName = null;
        lastName = null;
        email = null;
    }

    // these are getters acquiring the current information
    public static int getUserId() { return userId; }
    public static String getFirstName() {return firstName; }
    public static String getLastName () {return lastName; }
    public static String getEmail () { return email; }

    // Returns true if a user is currently logged in
    public static boolean isLoggedIn(){
        return userId != 0;
    }
}
