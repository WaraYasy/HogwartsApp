module es.potter {
    requires javafx.controls;
    requires javafx.fxml;

    opens es.potter to javafx.fxml;
    exports es.potter;
}
