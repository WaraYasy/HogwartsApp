module es.potter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
//    requires org.mariadb.jdbc;
    requires org.slf4j;
    requires de.jensd.fx.glyphs.fontawesome;

    opens es.potter to javafx.fxml, javafx.base;
    opens es.potter.control to javafx.fxml, javafx.base;
    opens es.potter.model to javafx.fxml, javafx.base;

    exports es.potter;
    exports es.potter.control;
    exports es.potter.model;
}
