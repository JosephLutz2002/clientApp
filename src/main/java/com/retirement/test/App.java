package com.retirement.test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.retirement.test.util.customTable;
import com.retirement.test.util.ping;
import com.retirement.test.util.resident_data;
import com.retirement.test.util.serverThread;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * class App will create the UI and manage all the interactions of the user, it will allow the user to select
 * a directory for analysing as well as selecting starting points for use, once complete it will allow the user the
 * option to remove all unused data, provides options for help and stopping the application. All data from the application
 * written to a log file for later use
 * @author Joseph Lutz
 * @version 1.0
 */
public class App extends Application {

    class DisplayArea extends VBox {
    private ObservableList<Unit> observableUnits;

    public DisplayArea(List<Unit> units) {
        setSpacing(10);
        setPadding(new javafx.geometry.Insets(10));
        observableUnits = javafx.collections.FXCollections.observableArrayList(units);
        ListView<Unit> listView = new ListView<>(observableUnits);
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Unit unit, boolean empty) {
                super.updateItem(unit, empty);

                if (empty || unit == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (unit.isActive()) setText(unit.getName());
                    else setText(unit.getName() + "\t" + "Please check");
                    setGraphic(createUnitCircle(unit.isActive()));
                }
            }
        });

        // Make the ListView scrollable
        listView.setPrefHeight(200);

        getChildren().add(listView);
    }

    private Circle createUnitCircle(boolean isActive) {
        Circle circle = new Circle(10);
        circle.setFill(isActive ? Color.GREEN : Color.RED);
        circle.setStroke(Color.BLACK);
        return circle;
    }

    // Public method to update the isActive status of a specific Unit
    public void updateUnitStatus(String unitName, boolean isActive) {
        for (Unit unit : observableUnits) {
            if (unit.getName().equals(unitName)) {
                unit.setIsActive(isActive);
                ((ListView<Unit>) getChildren().get(0)).refresh();
                break;
            }
        }
    }
}
    
    
   public  class Unit {
        private String name;
        private boolean isActive;
    
        public Unit(String name, boolean isActive) {
            this.name = name;
            this.isActive = isActive;
        }
    
        public String getName() {
            return name;
        }
    
        public boolean isActive() {
            return isActive;
        }

        public void setIsActive(boolean isActive){
            this.isActive = isActive;
        }
    }
    //private variables
    private static final String NoDirectoryForAnalyzing = "NO DIRECTORY SELECTED FOR ANALYSING";
    private static final String noDirectoryForExtraction = "NO DIRECTORY SELECTED FOR EXTRACTION";
    private static final String noStartPointSelected = "NO STARTING POINT HAS BEEN SELECTED FOR THE APPLICATION TO BEGIN";
    private Button chooseDirectory;
    private ComboBox<String> filters;
    private ImageView logo;
    private Label img;
    private String file_path = "";
    private String directory_path= "";
    private DropShadow effects = new DropShadow();
    private FileChooser fileSelect;
    private DirectoryChooser directorySelect;
    private ToolBar options;
    private TextArea display1;
    private Label d1;
    private TextArea display2;
    private Label d2;
    private GridPane pane;
    private ProgressBar extraction;
    private ProgressBar analysing;
    private Label extractLabel;
    private Label analyseLabel;
    private HBox ext_box;
    private HBox analize_box;
    private ToolBar info;
    private BorderPane screen_Pane;
    private Scene scene;
    private Alert warning;
    private Stage dialog;
    private Button start;
    private Button selectFile;
    private FileChooser chooser;
    private TextArea display;
    private BorderPane b_pane;
    private Pane lp;
    private Pane rp;
    private ScaleTransition fs;
    private ScaleTransition s_t;
    private ArrayList<String> files_chosen;
    private Label directory_chosen;
    private Label display_direct_choice;
    private String absolute_dir;
    private ArrayList<String> unused_files = new ArrayList<>();
    private ArrayList<String> files_to_remove = new ArrayList<>();
    private Button test_system;
    private Button help;
    private Button resident_info;
    private Button unit_status;
    List<Unit> units = new ArrayList<>();
    private Label timestamp;
    private HBox label_box;
    private serverThread serverThread;
    private ping pingThread;
    private DisplayArea displayArea;
    private Button openEmergencies;
    private customTable openEmergencyTable;
    private Scene logScene;
    private resident_data data_log;
    private Map<String,unitHistory> unit_data;
    
    @Override
    /**
     * Method will start the UI for the end user to use
     * @param stage - the starting stage of the application
     * 
     */
    public void start(Stage stage) {
        initiliseData();
        help = new Button("Help");
        test_system = new Button("test system");
        resident_info = new Button("Resident Information");
        resident_info.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        openResidentInformation();
                    }
                });
        unit_status = new Button("See unit History");
        unit_status.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        openUnitHistory();
                    }
                });
        openEmergencies = new Button("See open Emergencies");
        options = new ToolBar(resident_info,unit_status,test_system,help,openEmergencies);
        display1 = new TextArea();
        d1 = new Label("Logs");
        d1.setAlignment(Pos.CENTER);
        display1.setEditable(false);
        display2 = new TextArea();
        d2 = new Label("Units Online");
        d2.setAlignment(Pos.CENTER);
        timestamp = new Label(getTimeStamp());
        timestamp.setAlignment(Pos.CENTER);
        label_box = new HBox(10);
        label_box.getChildren().addAll(d2, timestamp);
        label_box.setAlignment(Pos.CENTER);
        display1.setPrefSize(400, 800);
        displayArea = new DisplayArea(units);
        openEmergencyTable = new customTable();
        pane = new GridPane();
        pane.setVgap(10); 
        pane.add(d1, 0, 1);
        pane.add(display1, 0, 2);
        pane.add(label_box,1,1);
        pane.add(displayArea, 1, 2);
        pane.add(openEmergencyTable,0,0,2,1);
        ColumnConstraints leftColumn = new ColumnConstraints();
        leftColumn.setHgrow(javafx.scene.layout.Priority.ALWAYS);
        ColumnConstraints rightColumn = new ColumnConstraints();
        rightColumn.setHgrow(javafx.scene.layout.Priority.ALWAYS);
        pane.getColumnConstraints().addAll(leftColumn, rightColumn);
        GridPane.setMargin(d1, new Insets(10));
        GridPane.setMargin(display1, new Insets(10));
        GridPane.setMargin(d2, new Insets(10));
        GridPane.setMargin(display2,new Insets(10));
        GridPane.setHalignment(d1, HPos.CENTER);
        GridPane.setValignment(d1, VPos.CENTER);
        GridPane.setHalignment(d2, HPos.CENTER);
        GridPane.setValignment(d2, VPos.CENTER);
        screen_Pane = new BorderPane();
        screen_Pane.setTop(options);
        screen_Pane.setCenter(pane);
        screen_Pane.setStyle("-fx-border-color: black;");
        screen_Pane.setPadding(new Insets(1));
        screen_Pane.prefHeightProperty().bind(stage.heightProperty());
        scene = new Scene(screen_Pane);
        //scene.getStylesheets().add("styles.css");
        stage.setScene(scene);
        stage.setTitle("Server Running");
        stage.setResizable(true);
        stage.show();
    }
 public static void main(String[] args) {
        launch(args);
    }

 public void updateLogs(String logEntry, String remoteID){
    Platform.runLater(() -> display1.appendText(getTimeStamp()+ "\t Received from Unit" + logEntry+"\n With RemoteID" + remoteID + "\n"));
    String address = data_log.getResidentAddress(logEntry);
    String severity = data_log.getResidentSeverity(data_log.getResidentUnitFromRemote(remoteID));
    String MedicalConditions = data_log.getResidentHealth(data_log.getResidentUnitFromRemote(remoteID));
    createAlert(logEntry);
    openEmergencyTable.setItems(logEntry,address,severity,MedicalConditions);
    addUnitHistory(logEntry);

 }
  private void startServerThread(int port) {
        serverThread = new serverThread(port, this::updateLogs);
        new Thread(serverThread).start();
    }

    private void stopServerThread() {
        if (serverThread != null) {
            serverThread.setIsRunning(false);
        }
    }

    private String  getTimeStamp(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    private void startPingThread(){
        pingThread = new ping(this::updateUnitStatus, data_log.getResidentIP());
        new Thread(pingThread).start();
    }

    private void initiliseData(){
        data_log = new resident_data();
        Map<String,String> ip_add = data_log.getResidentIP();
        for (Map.Entry<String, String> entry : ip_add.entrySet()) {
            units.add(new Unit(entry.getKey(),true));
        }
        unit_data = new HashMap<>();
        startPingThread();
        startServerThread(12345);
    }

    public void updateUnitStatus(String unitName){
        System.out.println("Updating unit availability");
           Platform.runLater(() -> displayArea.updateUnitStatus(unitName,false));

    }

    private void createAlert(String unitNumber){
         Thread notificationThread = new Thread(() -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("ALERT FROM UNIT");
                alert.setHeaderText(null);
                alert.setContentText("Unit Number: " + unitNumber + "\nSeverity: ");
                alert.showAndWait();
            });
        });

        notificationThread.setDaemon(true);
        notificationThread.start();
    
    }

    private void openUnitHistory() {
    Stage ResidentStage = new Stage();
    ResidentStage.setTitle("Unit History");
    VBox vbox = new VBox();
    vbox.setSpacing(10);
    vbox.setPadding(new Insets(10));
    BorderPane border_pane = new BorderPane();
    Label unitLabel = new Label("Select Unit:");
    ComboBox<String> unitComboBox = new ComboBox<>();
    unitComboBox.getItems().addAll(unit_data.keySet());
    Button searchButton = new Button("Unit to Search");
    searchButton.setAlignment(Pos.CENTER);
    TextArea unitEntry = new TextArea();
    TextArea unitDisplay = new TextArea();
    unitEntry.setPrefSize(300, 50);
    border_pane.setTop(unitLabel);
    border_pane.setCenter(unitEntry);
    border_pane.setBottom(searchButton);
    searchButton.setDisable(true);
    vbox.getChildren().addAll(border_pane,unitDisplay);
    Scene historyScene = new Scene(vbox, 400, 300);
    ResidentStage.setScene(historyScene);
    ResidentStage.show();
    ResidentStage.setResizable(false);
    unitEntry.textProperty().addListener((observable, oldValue, newValue) -> {
            searchButton.setDisable(newValue.trim().isEmpty());
        });
    unitEntry.setOnMouseClicked(event -> {
            unitEntry.clear();
        });

    searchButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String unit = unitEntry.getText();
                        if(unit_data.containsKey(unit)){
                            unitDisplay.setText(unit_data.get(unit).toString());
                        }else{
                            unitDisplay.setText("No records found");
                        }
                    }
                });
}

private void openResidentInformation() {
    Stage ResidentStage = new Stage();
    ResidentStage.setTitle("Resident Information");
    VBox vbox = new VBox();
    vbox.setSpacing(10);
    vbox.setPadding(new Insets(10));
    BorderPane border_pane = new BorderPane();
    Label unitLabel = new Label("Select Unit:");
    ComboBox<String> unitComboBox = new ComboBox<>();
    unitComboBox.getItems().addAll(unit_data.keySet());
    Button searchButton = new Button("Unit to Search");
    TextArea unitEntry = new TextArea();
    TextArea unitDisplay = new TextArea();
    unitEntry.setPrefSize(300, 50);
    border_pane.setTop(unitLabel);
    border_pane.setCenter(unitEntry);
    border_pane.setBottom(searchButton);
    searchButton.setDisable(true);
    Label ip = new Label("unit ip address");
    ip.setPrefSize(200,50);
    Label unit = new Label("unit number");
    unit.setPrefSize(200,50);
    Label medicalCond = new Label("Medical Conditions");
    medicalCond.setPrefSize(200,50);
    Label unit_severity = new Label("Severity of unit member");
    unit_severity.setPrefSize(200,50);
    Label address = new Label("unit address");
    address.setPrefSize(200,50);
    Label remote = new Label("Unit Remote");
    remote.setPrefSize(200,50);
    TextArea remote_display = new TextArea();
    remote_display.setPrefSize(200,50);
    remote_display.setDisable(true);
    TextArea ip_display = new TextArea();
    ip_display.setPrefSize(200,50);
    ip_display.setDisable(true);
    TextArea unit_display = new TextArea();
    unit_display.setPrefSize(200,50);
    unit_display.setDisable(true);
    TextArea medical_display = new TextArea();
    medical_display.setPrefSize(200,50);
    medical_display.setDisable(true);
    TextArea unit_severity_display = new TextArea();
    unit_severity_display.setPrefSize(200,50);
    unit_severity_display.setDisable(true);
    TextArea address_display = new TextArea();
    address_display.setPrefSize(200,50);
    address_display.setDisable(true);
    HBox unit_box = new HBox(10);
    HBox ip_box = new HBox(10);
    HBox medical_box = new HBox(10);
    HBox unit_severity_box = new HBox(10);
    HBox address_box = new HBox(10);
    HBox remote_box = new HBox(10);
    remote_box.getChildren().addAll(remote,remote_display);
    address_box.getChildren().addAll(address,address_display);
    unit_severity_box.getChildren().addAll(unit_severity,unit_severity_display);
    medical_box.getChildren().addAll(medicalCond,medical_display);
    ip_box.getChildren().addAll(ip,ip_display);
    unit_box.getChildren().addAll(unit,unit_display);
    vbox.getChildren().addAll(border_pane,unit_box,ip_box,medical_box,unit_severity_box,address_box,remote_box);
    Scene historyScene = new Scene(vbox, 500, 400);
    ResidentStage.setScene(historyScene);
    ResidentStage.show();
    ResidentStage.setResizable(false);
    unitEntry.textProperty().addListener((observable, oldValue, newValue) -> {
            searchButton.setDisable(newValue.trim().isEmpty());
        });
    unitEntry.setOnMouseClicked(event -> {
            unitEntry.clear();
        });

    searchButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String unit = unitEntry.getText();
                        if(data_log.isResident(unit)){
                            unit_display.setText(unit);
                            address_display.setText(data_log.getResidentAddress(unit));
                            unit_severity_display.setText(data_log.getResidentSeverity(unit));
                            medical_display.setText(data_log.getResidentHealth(unit));
                            ip_display.setText(data_log.getResidentIP(unit));
                            remote_display.setText(data_log.getRemoteCodeForUnit(unit));
                        }else{
                            unit_display.setText("Not a unit");
                            address_display.setText("");
                            unit_severity_display.setText("");
                            medical_display.setText("");
                            ip_display.setText("");
                            remote_display.setText("");
                        }
                    }
                });
}

    private void addUnitHistory(String unit){
        if(unit_data.containsKey(unit)){
            unitHistory history = unit_data.get(unit);
            history.addData(getTimeStamp());
            unit_data.put(unit,history);
        }else {
            unitHistory new_history = new unitHistory();
            new_history.addData(getTimeStamp());
            unit_data.put(unit,new_history);
        }
    }

    public class unitHistory{
        private int count;
        private List <String> timestamps;

        public unitHistory(){
            this.count =0;
            this.timestamps = new ArrayList<>();
        }

        public void addData(String timestamp){
            this.timestamps.add(timestamp);
            this.count++;
        }

        public String toString(){
            String unit = "Emergency Count:" + count + "\n";
            for(String time:timestamps){
                unit += time + "\n";
            }
            return unit;
        }
    }
}
