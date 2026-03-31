package com.cts.passiton;

// take care with the JavaFx imports as they need to correlate with what we have in Scene builder
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

// This is the launching out JavaFx application and loading the first screen.
public class JavaFxDemoApp extends Application {

    // Logger is recording the errors and events to the console for us.
    private static final Logger logger = Logger.getLogger(JavaFxDemoApp.class.getName());

    DatabaseConnection dc = new DatabaseConnection();
    private static Stage currentStg;

    //this is a behaviour to override main and start the menu
    //the FXMLLoader line loads the controller.java and the associated fxml
    @Override
    public void start(Stage primaryStage) throws IOException {
        currentStg = primaryStage; // this is saving the window reference
        primaryStage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxDemoApp.class.getResource("javafx-demo-app-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load()); //creating a scene from the fxml we now referenced above

        primaryStage.setTitle("JavaFX Project Demo Application: User Login");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(750);
        primaryStage.setScene(scene); // this is attaching the scene to the stage
        primaryStage.show(); //  this is showing the window

       //This is checking the database connection to one of our tables and printing into console
        try {
            String query = "SELECT * FROM tblusers";
            dc.rst = dc.stat.executeQuery(query);

            //this is iterating all the rows of data returned by sql
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
                currentStg.setTitle("JavaFX Project Demo: Application Dashboard");
                break;

            case "new-reg-user-view.fxml":
                currentStg.setTitle("JavaFX Project Demo: Register new User");
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
        }


        currentStg.setWidth(sWidth);
        currentStg.setHeight(sHeight);

        // From my understanding, the thought process is
        // Take current window
        // Get the scene ( your content) attached to the stage
        // Swap out everything inside with the new screen
        currentStg.getScene().setRoot(pane);
    }

    public static void main(String[] args) {launch();}
}