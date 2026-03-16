module com.cts.passiton {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    requires java.sql;

    requires org.controlsfx.controls;
    requires eu.hansolo.tilesfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires net.synedra.validatorfx;
    requires mysql.connector.j;

    exports com.cts.passiton;
    opens com.cts.passiton to javafx.fxml;
}