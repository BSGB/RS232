package sample;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private ComboBox<String> portsComboBox = new ComboBox<>();

    @FXML
    private Button openCloseButton = new Button();

    @FXML
    private Label openCloseLabel = new Label();

    @FXML
    private Button reloadButton = new Button();

    @FXML
    private Button sendButton = new Button();

    @FXML
    private TextField sendTextField = new TextField();

    @FXML
    private TextArea receivedTextArea = new TextArea();

    @FXML
    private CheckBox rCheckBox = new CheckBox();

    @FXML
    private CheckBox nCheckBox = new CheckBox();

    @FXML
    private TitledPane communicationPane = new TitledPane();

    @FXML
    private Button editButton = new Button();
    @FXML
    private Label baudLabel = new Label();

    @FXML
    private Label dataLabel = new Label();

    @FXML
    private Label stopLabel = new Label();

    @FXML
    private Label parityLabel = new Label();

    @FXML
    private Label flowLabel = new Label();

    private String portNames[];
    private HashMap<String, SerialPort> serialPorts = new HashMap<>();
    private static SerialPort serialPort;
    private boolean isPortOpened = false;
    PortConfiguration portConfiguration;

    @FXML
    public void openCloseButtonAction() throws SerialPortException {

        if (isPortOpened) {
            try {
                serialPort.closePort();
                communicationPane.setDisable(true);
                editButton.setDisable(false);
                receivedTextArea.clear();
                sendTextField.clear();
                portsComboBox.setDisable(false);
            } catch (SerialPortException e) {
                //e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Connection error");
                alert.setContentText("Cannot close port! Reload list and check if port is still available.");
                alert.showAndWait();
            }
        } else {
            try {
                serialPort.openPort();

                int baudRate = portConfiguration.getBaudRate();
                int dataBits = portConfiguration.getDataBits();
                int stopBits = portConfiguration.getStopBits();
                int parityCtrl = portConfiguration.getParityCtrl();
                int[] flowCtrl = portConfiguration.getFlowCtrl();

                communicationPane.setDisable(false);
                portsComboBox.setDisable(true);

                serialPort.setParams(baudRate, dataBits, stopBits, parityCtrl);

                if (flowCtrl.length == 1) {
                    serialPort.setFlowControlMode(flowCtrl[0]);
                } else {
                    serialPort.setFlowControlMode(flowCtrl[0] | flowCtrl[1]);
                }
                int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;

                serialPort.addEventListener(serialPortEvent -> {
                    if (serialPortEvent.isRXCHAR() || serialPortEvent.getEventValue() > 0) {
                        try {
                            byte[] receivedData = serialPort.readBytes(serialPortEvent.getEventValue(), 1000);
                            String receivedMessage = new String(receivedData);
                            receivedTextArea.appendText(receivedMessage);

                        } catch (SerialPortException ex) {
                            System.out.println("Blad odbioru: " + ex);
                        } catch (SerialPortTimeoutException e) {
                            e.printStackTrace();
                        }
                    } else if (serialPortEvent.isCTS()) {
                        if (serialPortEvent.getEventValue() == 1) {
                            System.out.println("CTS - ON");
                        } else {
                            System.out.println("CTS - OFF");
                        }
                    } else if (serialPortEvent.isDSR()) {
                        if (serialPortEvent.getEventValue() == 1) {
                            System.out.println("DSR - ON");
                        } else {
                            System.out.println("DSR - OFF");
                        }
                    }
                }, mask);


                editButton.setDisable(true);
            } catch (SerialPortException e) {
                //e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Connection error");
                alert.setContentText("Cannot open port! Reload list and check if port is still available.");
                alert.showAndWait();
            }

        }
        checkStatus();
    }

    @FXML
    public void checkStatus() {

        openCloseButton.setDisable(false);
        editButton.setDisable(false);

        String portName = portsComboBox.getValue();
        serialPort = serialPorts.get(portName);


        portConfiguration = PortConfigurationSingleton.getInstance().getPortsConfiguration().get(portsComboBox.getValue());

        baudLabel.setText(portConfiguration.getBaudRateStr());
        dataLabel.setText(portConfiguration.getDataBitsStr());
        stopLabel.setText(portConfiguration.getStopBitsStr());
        parityLabel.setText(portConfiguration.getParityCtrlStr());
        flowLabel.setText(portConfiguration.getFlowCtrlStr());

        if (serialPort.isOpened()) {
            openCloseButton.setText("Close");
            openCloseLabel.setText("Port is opened...");
            isPortOpened = true;
        } else {
            openCloseButton.setText("Open");
            openCloseLabel.setText("Port is closed...");
            isPortOpened = false;
        }
    }

    @FXML
    public void reloadComBox() {
        portNames = SerialPortList.getPortNames();
        if (portNames.length != 0) {
            portsComboBox.setItems(FXCollections.observableArrayList(portNames));
        }
    }

    @FXML
    public void sendMessage() throws SerialPortException {
        String message = sendTextField.getText();
        message = rCheckBox.isSelected() ? message.concat("\r") : message;
        message = nCheckBox.isSelected() ? message.concat("\n") : message;
        //serialPort.writeBytes(message.getBytes());
        serialPort.writeByte((byte)0x3C);
    }

    @FXML
    public void openEditWindow() {

        PortNameSingleton.getInstance().setPortName(portsComboBox.getValue());

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/editWindow.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Edit");
            stage.setScene(new Scene(root, 300, 350));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clearTextArea() {
        receivedTextArea.clear();
    }

    public void initialize(URL location, ResourceBundle resources) {
        reloadComBox();
        for (String portName : portNames) {
            serialPorts.put(portName, new SerialPort(portName));
            PortConfigurationSingleton.getInstance().getPortsConfiguration().put(portName, new PortConfiguration());
        }
    }
}
