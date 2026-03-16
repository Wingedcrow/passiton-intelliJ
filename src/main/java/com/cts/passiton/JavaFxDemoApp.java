package com.cts.passiton;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

public class JavaFxDemoApp extends Application {

    private static final Logger logger = Logger.getLogger(JavaFxDemoApp.class.getName());

    DatabaseConnection dc = new DatabaseConnection();
    private static Stage currentStg;
//this is a behaviour to override main and start the menu
    @Override
    public void start(Stage primaryStage) throws IOException {
        currentStg = primaryStage;
        primaryStage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxDemoApp.class.getResource("javafx-demo-app-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("JavaFX Project Demo Application: User Login");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(750);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            String query = "SELECT * FROM tblusers";
            dc.rst = dc.stat.executeQuery(query);

            while(dc.rst.next()) {
                System.out.print(dc.rst.getInt("user_id"));
                System.out.print("  ");
                System.out.print(dc.rst.getString("email"));
                System.out.print("  ");
                System.out.print(dc.rst.getString("first_name"));
                System.out.print("  ");
                System.out.println(dc.rst.getString("last_name"));
            }
        } catch (SQLException e) {
            // Log the exception using the Java logger
            logger.severe("An error occurred: Database connectivity failed");
            logger.severe(e.toString());
            System.exit(0);
        }
    }
// this is a behaviour to change scenes
    public void changeScene(String fxml, Integer sWidth, Integer sHeight) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));

        switch (fxml) {
            case "application-dashboard-view.fxml":
                currentStg.setTitle("JavaFX Project Demo: Application Dashboard");
                break;

            case "register-user-view.fxml":
                currentStg.setTitle("JavaFX Project Demo: Register new User");
                break;
            case "inventory-accordion-view.fxml":
                currentStg.setTitle("JavaFX Project Demo: Customer Order Administration");
                break;
        }

        currentStg.setWidth(sWidth);
        currentStg.setHeight(sHeight);
        currentStg.getScene().setRoot(pane);
    }

    public static void main(String[] args) {launch();}
}