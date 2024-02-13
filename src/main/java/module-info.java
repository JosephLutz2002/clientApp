module com.retirement.test {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.retirement.test to javafx.fxml;
    exports com.retirement.test;
}
