package com.cts.passiton;

// take care with the JavaFx imports as they need to correlate with what we have in Scene builder
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * PassItOnApp.java
 * This is the main entry point for the PassItOn application.
 * Built on the foundations of the teacher's sample.
 * It is responsible for launching the application, displaying the initial login screen
 * Additionally it contains a reusable method for switching between scenes
 * A database connection test is also performed on startup to confirm connectivity
 * changes to the sample consist of directory changes, updating of names to associate with PassItOn
 * and adding switch cases for every addition of a scene.
 *
 * @author Joshua Howard & Bradley Balram
 * @version 1.0
 * @date (08/04/2026)
 */

public class PassItOnApp extends Application {

    // Logger is recording the errors and events to the console for us.
    private static final Logger logger = Logger.getLogger(PassItOnApp.class.getName());

    DatabaseConnection dc = new DatabaseConnection();
    private static Stage currentStg;

    //Override the start method to load the initial login screen when the application launches
    @Override
    public void start(Stage primaryStage) throws IOException {
        currentStg = primaryStage; // this is saving the stage reference so changescene() can access it later
        primaryStage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(PassItOnApp.class.getResource("passiton-login-app-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load()); //creating a scene from the fxml we referenced above

        primaryStage.setTitle("Pass it On: User Login");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(750);
        primaryStage.setScene(scene); // this is attaching the scene to the stage
        primaryStage.show(); //  display the window

       //This is checking the database connection to one of our tables and printing into console
        try {
            String query = "SELECT * FROM tblusers";
            dc.rst = dc.stat.executeQuery(query);

            //this is iterating all the rows of data and printing the details to visually confirm the connection
            while(dc.rst.next()) {
                System.out.print(dc.rst.getInt("user_id"));
                System.out.print("  ");
                System.out.print(dc.rst.getString("email"));
                System.out.print("  ");
                System.out.print(dc.rst.getString("first_name"));
                System.out.print("  ");
                System.out.println(dc.rst.getString("last_name"));
                System.out.print("  ");
                System.out.println(dc.rst.getString("password"));
            }
        } catch (SQLException e) {
            // Log the exception using the Java logger
            logger.severe("An error occurred: Database connectivity failed");
            logger.severe(e.toString());
            System.exit(0);
        }
    }
// this is a behaviour/method to change scenes, we use switch , and then make sure the directory goes to the .fxml
    public void changeScene(String fxml, Integer sWidth, Integer sHeight) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));

        switch (fxml) {
            case "application-dashboard-view.fxml":
                currentStg.setTitle("PassItOn: Application Dashboard");
                break;

            case "new-reg-user-view.fxml":
                currentStg.setTitle("PassItOn: Register New User");
                break;

            case "student-dashboard-view.fxml":
                currentStg.setTitle("PassItOn: Student Dashboard");
                break;

            case "marketboard-view.fxml":
                currentStg.setTitle("PassItOn: Marketboard");
                break;

            case "trades-view.fxml":
                currentStg.setTitle("PassItOn: Trades");
                break;

            case "supplies-view.fxml":
                currentStg.setTitle("PassItOn: Your Supplies");
                break;

            case "Settings-view.fxml":
                currentStg.setTitle("PassItOn: Your Settings");
                break;
        }


        currentStg.setWidth(sWidth);
        currentStg.setHeight(sHeight);
        //takes the current stage , get its scene , and swap the root node with the new scene
        currentStg.getScene().setRoot(pane);
    }

    public static void main(String[] args) {launch();}
}