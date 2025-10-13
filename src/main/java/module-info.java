module es.potter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.mariadb.jdbc;
    requires org.slf4j;

    opens es.potter to javafx.fxml, javafx.base;

    exports es.potter;
}
