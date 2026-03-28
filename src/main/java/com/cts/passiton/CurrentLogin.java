package com.cts.passiton;

/* This is storing currently logged in user details
for the duration of their login.
 used in
    -  JavaFxDemoController.java
    -  PostRequestController.java
    -  MarketboardController.java
    -  StudentDashboardController.java
*/

public class CurrentLogin {

    private static int userId;
    private static String firstName;
    private static String lastName;
    private static String email;

    //sets the student details when a user logs in
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

    // this is checking if someone is logged in
    public static boolean isLoggedIn(){
        return userId != 0;
    }
}
