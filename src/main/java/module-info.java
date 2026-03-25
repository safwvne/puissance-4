module com.devops.puissance4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires java.naming;

    opens com.devops.puissance4 to javafx.fxml;
    opens com.devops.puissance4.controller to javafx.fxml;
    opens com.devops.puissance4.model to org.hibernate.orm.core, jakarta.persistence;

    exports com.devops.puissance4;
    exports com.devops.puissance4.controller;
    exports com.devops.puissance4.model;
}