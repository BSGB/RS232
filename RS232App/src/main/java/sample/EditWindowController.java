package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditWindowController implements Initializable {

    @FXML
    private Button acceptButton = new Button();

    @FXML
    private ComboBox<String> baudComboBox = new ComboBox<>();

    @FXML
    private ComboBox<String> dataComboBox = new ComboBox<>();

    @FXML
    private ComboBox<String> stopComboBox = new ComboBox<>();

    @FXML
    private ComboBox<String> parityComboBox = new ComboBox<>();

    @FXML
    private ComboBox<String> flowComboBox = new ComboBox<>();

    private PortConfiguration portConfiguration;

    @FXML
    public void acceptEdit() {
        if (baudComboBox.getValue() == null || dataComboBox.getValue() == null || stopComboBox.getValue() == null ||
                parityComboBox.getValue() == null || flowComboBox.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Set-up");
            alert.setHeaderText("Error in set-up");
            alert.setContentText("Fill all parameters!");
            alert.showAndWait();
        } else {
            int stopBits;
            int parityCtrl;
            int n = flowComboBox.getValue().equals("none") ? 1 : 2;
            int flowCtrl[] = new int[n];

            if (stopComboBox.getValue().equals("one")) {
                stopBits = 1;
            } else {
                stopBits = 2;
            }

            if (parityComboBox.getValue().equals("none")) {
                parityCtrl = 0;
            } else if (parityComboBox.getValue().equals("odd")) {
                parityCtrl = 1;
            } else {
                parityCtrl = 2;
            }

            if (flowComboBox.getValue().equals("none")) {
                flowCtrl[0] = 0;
            } else if (flowComboBox.getValue().equals("RTSCTS")) {
                flowCtrl[0] = 1;
                flowCtrl[1] = 2;
            } else {
                flowCtrl[0] = 4;
                flowCtrl[1] = 8;
            }

            portConfiguration
                    = PortConfigurationSingleton.getInstance().getPortsConfiguration().get(PortNameSingleton.getInstance().getPortName());

            portConfiguration.setBaudRate(Integer.parseInt(baudComboBox.getValue()));
            portConfiguration.setDataBits(Integer.parseInt(dataComboBox.getValue()));
            portConfiguration.setStopBits(stopBits);
            portConfiguration.setParityCtrl(parityCtrl);
            portConfiguration.setFlowCtrl(flowCtrl);

            portConfiguration.setBaudRateStr(baudComboBox.getValue());
            portConfiguration.setDataBitsStr(dataComboBox.getValue());
            portConfiguration.setStopBitsStr(stopComboBox.getValue());
            portConfiguration.setParityCtrlStr(parityComboBox.getValue());
            portConfiguration.setFlowCtrlStr(flowComboBox.getValue());
            portConfiguration.setSet(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Set-up");
            alert.setHeaderText("Configuration set-up");
            alert.setContentText("Parameters set successfully!");
            alert.showAndWait();


            Stage stage = (Stage) acceptButton.getScene().getWindow();
            stage.close();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<String> baudList = FXCollections.observableArrayList("300", "600",
                "1200", "2400", "4800", "9600", "14400", "19200", "28800", "38400", "56000", "57600", "115200");
        ObservableList<String> dataList = FXCollections.observableArrayList("5", "6", "7", "8");
        ObservableList<String> stopList = FXCollections.observableArrayList("one", "two");
        ObservableList<String> parityList = FXCollections.observableArrayList("none", "even", "odd");
        ObservableList<String> flowList = FXCollections.observableArrayList("RTSCTS", "XONXOF", "none");

        baudComboBox.setItems(baudList);
        dataComboBox.setItems(dataList);
        stopComboBox.setItems(stopList);
        parityComboBox.setItems(parityList);
        flowComboBox.setItems(flowList);

        portConfiguration =
                PortConfigurationSingleton.getInstance().getPortsConfiguration().get(PortNameSingleton.getInstance().getPortName());
        if (portConfiguration.isSet()) {
            baudComboBox.getSelectionModel().select(portConfiguration.getBaudRateStr());
            dataComboBox.getSelectionModel().select(portConfiguration.getDataBitsStr());
            stopComboBox.getSelectionModel().select(portConfiguration.getStopBitsStr());
            parityComboBox.getSelectionModel().select(portConfiguration.getParityCtrlStr());
            flowComboBox.getSelectionModel().select(portConfiguration.getFlowCtrlStr());
        }
    }
}
