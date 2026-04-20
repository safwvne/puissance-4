module com.devops.puissance4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires java.naming;

    opens com.devops.puissance4 to javafx.fxml;
    opens com.devops.puissance4.controller to javafx.fxml;
    opens com.devops.puissance4.client to javafx.fxml, javafx.graphics;
    opens com.devops.puissance4.model to org.hibernate.orm.core, jakarta.persistence;

    exports com.devops.puissance4;
    exports com.devops.puissance4.controller;
    exports com.devops.puissance4.model;
    exports com.devops.puissance4.client;
    exports com.devops.puissance4.server;
}