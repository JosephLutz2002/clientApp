package com.retirement.test.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class customTable extends VBox {

    public static class Person {
        private final StringProperty name;
        private final StringProperty address;
        private final StringProperty severity;
        private final StringProperty date;
        private final StringProperty medicalConditions;

        public Person(String name, String address, String severity,String medicalConditions) {
            this.name = new SimpleStringProperty(name);
            this.address = new SimpleStringProperty(address);
            this.severity = new SimpleStringProperty(severity);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.date = new SimpleStringProperty(dateFormat.format(new Date()));
            this.medicalConditions = new SimpleStringProperty(medicalConditions);
        }


        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public String getAddress() {
            return address.get();
        }

        public StringProperty addressProperty() {
            return address;
        }

        public StringProperty severityProperty() {
            return severity;
        }

        public String getDate(){
            return date.get();
        }

        public StringProperty dateProperty(){
            return date;
        }

        public String getMedicalConditions(){
            return medicalConditions.get();
        }

        public StringProperty medicalConditionsProperty(){
            return medicalConditions;
        }
    }

    private TableView<Person> tableView;
    private ObservableList<Person> items = FXCollections.observableArrayList();

    public customTable() {
        initializeTable();
    }

    private void initializeTable() {
        tableView = new TableView<>();
        tableView.setPrefWidth(400);
        TableColumn<Person, String> nameColumn = new TableColumn<>("Unit Number");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableView.getColumns().add(nameColumn);
        nameColumn.setMaxWidth(100);
        nameColumn.setMinWidth(100);

        TableColumn<Person, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        addressColumn.setCellFactory(tc -> createWrappedTextCell());
        addressColumn.setMaxWidth(300);
        addressColumn.setMinWidth(300);
        tableView.getColumns().add(addressColumn);

        TableColumn<Person, String> severityColumn = new TableColumn<>("Severity");
        severityColumn.setCellValueFactory(cellData -> cellData.getValue().severityProperty());
        severityColumn.setCellFactory(tc -> createSeverityTextCell());
        tableView.getColumns().add(severityColumn);
        severityColumn.setMaxWidth(100);
        severityColumn.setMinWidth(100);

         TableColumn<Person, String> medicalConditionsColumn = new TableColumn<>("MedicalConditions");
        medicalConditionsColumn.setCellValueFactory(cellData -> cellData.getValue().medicalConditionsProperty());
        medicalConditionsColumn.setCellFactory(tc -> createSeverityTextCell());
        tableView.getColumns().add(medicalConditionsColumn);
        medicalConditionsColumn.setMaxWidth(100);
        medicalConditionsColumn.setMinWidth(100);

        TableColumn<Person, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        timeColumn.setMaxWidth(240);
        timeColumn.setMinWidth(240);
        tableView.getColumns().add(timeColumn);

        TableColumn<Person, Void> actionColumn = new TableColumn<>("Close Emergency");
        actionColumn.setCellFactory(param -> createCloseButtonCell());
        actionColumn.setMaxWidth(200);
        actionColumn.setMinWidth(200);
        tableView.getColumns().add(actionColumn);

        getChildren().add(tableView);
    }

    private TableCell<Person, String> createWrappedTextCell() {
        return new TableCell<Person, String>() {
            private final Text text = new Text();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    text.setWrappingWidth(getTableColumn().getWidth()); // Adjust as needed
                    setGraphic(text);
                }
            }
        };
    }

    private TableCell<Person, String> createSeverityTextCell() {
        return new TableCell<Person, String>() {
            private final Text text = new Text();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    if ("Urgent".equals(item)) {
                        text.setFill(Color.RED);
                    } else if ("Serious".equals(item)) {
                        text.setFill(Color.ORANGE);
                    } else if ("Not Urgent".equals(item)) {
                        text.setFill(Color.GREEN);
                    } else {
                        text.setFill(Color.BLACK);
                    }

                    setGraphic(text);
                }
            }
        };
    }

    private TableCell<Person, Void> createCloseButtonCell() {
        return new TableCell<Person, Void>() {
            private final Button button = new Button("Close");

            {
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tableView.getItems().remove(getTableRow().getItem());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        };
    }

    public void setItems(String unit, String address, String severity,String MedicalConditions) {
        Person person = new Person(unit,address,severity,MedicalConditions);
        items.add(person);
        tableView.setItems(items);
    }

    public static void main(String[] args) {
        javafx.application.Application.launch(args);
    }
}
