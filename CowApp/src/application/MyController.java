package application;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioClip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import tcpclient.DownloadRequest;
import tcpclient.LoginRequest;
import tcpclient.Request;
import tcpclient.TCP_Client;
public class MyController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private BorderPane borderPane;

    @FXML
    private URL location;

    @FXML
    private DatePicker endDP;

    @FXML
    private DatePicker startDP;

    @FXML
    private TextField chooseTF;

    @FXML
    private Button chooseButton;

    @FXML
    private TextField usernameTF;

    @FXML
    private PasswordField passwTF;

    @FXML
    private Button loginButton;

    @FXML
    private CheckBox multipleDaysChoice;

    @FXML
    private CheckBox soundAdvidceChoice;

    @FXML
    private Label loggedUser;

    @FXML
    private CheckBox shutdownChoice;

    @FXML
    private RadioButton downloadAfterRadio;

    @FXML
    private RadioButton downloadAtRadio;

    @FXML
    private ChoiceBox<String> hourCB;

    @FXML
    private ChoiceBox<String> minuteCB;

    @FXML
    private CheckBox closeChoice;

    @FXML
    private Button downloadButton;

    private boolean LOGGED = false;
    private final String IPSERVER = "localhost";
    private final int SERVER_PORT = 3000;

    @FXML
    private void chooseNewFolder(ActionEvent event) {

        // String chosseFolder= chooseTF.getText();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Directory");
        Stage stage = (Stage) borderPane.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        this.chooseTF.setText(selectedDirectory.getAbsolutePath());

    }

    @FXML
    private void login(ActionEvent event) {

        String username = usernameTF.getText();
        String passw = passwTF.getText();
        Request loginRequest = new LoginRequest("LOGIN", "LOGIN", username, passw);

        String log = TCP_Client.TCP_send(IPSERVER, SERVER_PORT, loginRequest);
        if (log.equalsIgnoreCase("LOGGED")) {
            LOGGED = true;
            loggedUser.setText(username + " logged");
        }
    }

    @FXML
    private void download(ActionEvent event) {

        if (!LOGGED) {

            Alert alert = new Alert(AlertType.ERROR, "DEVI PRIMA EFFETTUARE IL LOGIN", ButtonType.OK);
            alert.showAndWait();
        } else {

            boolean multipleDaysChoiceB = multipleDaysChoice.isSelected();
            boolean soundAdvidceChoiceB = soundAdvidceChoice.isSelected();
            boolean shutdownChoiceB = shutdownChoice.isSelected();
            boolean closeChoiceB = closeChoice.isSelected();
            boolean downloadCompleted = false;
            String path = chooseTF.getText();
            boolean timerType = this.downloadAfterRadio.isSelected();
            int hour = Integer.parseInt(this.hourCB.getValue());
            int minute = Integer.parseInt(this.minuteCB.getValue());
            LocalDate start = startDP.getValue();
            LocalDate end = endDP.getValue();

            Request request = new DownloadRequest(path, "DOWNLOAD", "DOWNLOAD", start);
            if (multipleDaysChoiceB)
                ((DownloadRequest) request).setEnd(end);
            else
                ((DownloadRequest) request).setEnd(start);

            ((DownloadRequest) request).setDelay(delayCalculator(timerType, hour, minute));

            String down = TCP_Client.TCP_send(IPSERVER, SERVER_PORT, request);
            if (down.equalsIgnoreCase("DOWNLOADED"))
                downloadCompleted = true;

            if (soundAdvidceChoiceB && downloadCompleted) {
                // Toolkit.getDefaultToolkit().beep();
                AudioClip audioClip = new AudioClip(Paths.get("COW.wav").toUri().toString());
                audioClip.play(10);

            }

            if (shutdownChoiceB && downloadCompleted) {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("shutdown -s -t 10"); // da cambiare per linux. Process proc =
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (closeChoiceB && downloadCompleted) {
                System.exit(0);
            }
        }
    }

    @FXML
    private void multipleDaysActived(ActionEvent event) {
        if (this.multipleDaysChoice.isSelected())

            this.endDP.setDisable(false);
        else
            this.endDP.setDisable(true);
    }

    @FXML
    void initialize() {
        // PALESEMENTE DA MODIFICARE
        String st[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                "17", "18", "19", "20", "21", "22", "23" };
        String st1[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33",
                "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
                "51", "52", "53", "54", "55", "56", "57", "58", "59" };
        this.hourCB.setItems(FXCollections.observableArrayList(st));
        this.minuteCB.setItems(FXCollections.observableArrayList(st1));
        this.hourCB.setValue("0");
        this.minuteCB.setValue("0");
        this.downloadAfterRadio.setSelected(true);
        ToggleGroup toggleGroup = new ToggleGroup();

        this.downloadAfterRadio.setToggleGroup(toggleGroup);
        this.downloadAtRadio.setToggleGroup(toggleGroup);

        assert borderPane != null : "fx:id=\"borderPane\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert endDP != null : "fx:id=\"endDP\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert startDP != null : "fx:id=\"startDP\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert chooseTF != null : "fx:id=\"chooseTF\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert chooseButton != null : "fx:id=\"chooseButton\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert usernameTF != null : "fx:id=\"usernameTF\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert passwTF != null : "fx:id=\"passwTF\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert multipleDaysChoice != null : "fx:id=\"multipleDaysChoice\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert soundAdvidceChoice != null : "fx:id=\"soundAdvidceChoice\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert shutdownChoice != null : "fx:id=\"shutdownChoice\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert downloadAfterRadio != null : "fx:id=\"downloadAfterRadio\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert downloadAtRadio != null : "fx:id=\"downloadAtRadio\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert hourCB != null : "fx:id=\"hourCB\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert minuteCB != null : "fx:id=\"minuteCB\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert closeChoice != null : "fx:id=\"closeChoice\" was not injected: check your FXML file 'cowGraphic.fxml'.";
        assert loggedUser != null : "fx:id=\"loggedUser\" was not injected: check your FXML file 'cowGraphic.fxml'.";

    }

    private int delayCalculator(boolean timerType, int hour, int minute) {
        int result = 0;
        if (timerType) {
            result = (minute * 60 + hour * 60 * 60) * 1000;
        } else {
            result = ((hour - LocalTime.now().getHour()) * 60 * 60 + (minute - LocalTime.now().getMinute()) * 60)
                    * 1000;
        }

        return result;
    }

}
