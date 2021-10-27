package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import models.FogServer;
import models.PatientDevice;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Classe controladora responsável por executar funções dispararadas por ações
 * realizadas na janela da aplicação do monitoramento do médico.
 *
 * @author João Erick Barbosa
 */
public class MonitoringController implements Initializable {

    @FXML
    private Pane paneInfo;

    @FXML
    private Label lblSelectPatient;

    @FXML
    private Label txtUserName;

    @FXML
    private Label lblUserName;

    @FXML
    private Label txtRespiratoryFrequency;

    @FXML
    private Label lblRespiratoryFrequency;

    @FXML
    private Label txtTemperature;

    @FXML
    private Label lblTemperature;

    @FXML
    private Label txtBloodOxygen;

    @FXML
    private Label lblBloodOxygen;

    @FXML
    private Label txtHeartRate;

    @FXML
    private Label lblHeartRate;

    @FXML
    private Label txtBloodPressure;

    @FXML
    private Label lblBloodPressure;

    @FXML
    private Label txtSeverityLevel;

    @FXML
    private Label lblSeverityLevel;

    @FXML
    private TableView<PatientDevice> table;

    @FXML
    private TableColumn<PatientDevice, String> clmID;

    @FXML
    private TableColumn<PatientDevice, String> clmUserName;

    @FXML
    private TableColumn<PatientDevice, String> clmSituation;

    @FXML
    private Label lblStatus;

    @FXML
    private TextField txtAmountPatients;

    /*-------------------------- Constantes ----------------------------------*/
    private static final int REFRESH_TIME_TABLE = 6000;
    private static final int REFRESH_TIME_INFO = 5500;
    private static final String IP_ADDRESS = "localhost";
    private static final int PORT = 12250;
    /*------------------------------------------------------------------------*/

    private static Socket client;

    private static List<PatientDevice> patients = new ArrayList();

    //Armazena o paciente selecionado da tabela de pacientes.
    private PatientDevice selected;
    //Armazena o paciente que estava selecionado para atualizar suas informações na tela.
    private PatientDevice selectedRefresh;
    //Armazena a quantidade de pacientes mais graves que usuário deseja listar na interface.
    private static int amountPatients;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblStatus.setText("Aguardando...");

        //Inicializa a tabela de pacientes.
        initTable();

        //Armazena um paciente caso algum item da tabela de pacientes seja selecionado.
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                selected = (PatientDevice) newValue;

                showDetails();
            }
        });

        /**
         * Uma nova thread é inicializada concorrentemente ao sistema para fazer
         * requisições ao servidor. A cada REFRESH_TIME_TABLE, uma nova lista de
         * pacientes é requisitada e a tabela é atualizada.
         */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable updater = new Runnable() {

                    @Override
                    public void run() {

                        lblStatus.setText("Conectado");
                        lblStatus.setStyle("-fx-text-fill: green");
                        requestPatientsDevices();

                        table.setItems(listToObservableList());
                    }
                };

                while (true) {
                    try {
                        Thread.sleep(REFRESH_TIME_TABLE);
                    } catch (InterruptedException ie) {
                        System.err.println("Thread finalizada de maneira "
                                + "inesperada.");
                        System.out.println(ie);
                    }

                    // A atualização é feita na thread da aplicação.
                    Platform.runLater(updater);
                }
            }
        });

        /* Impede a thread de finalizar a JVM. */
        thread.setDaemon(true);
        thread.start();

        /**
         * Uma nova thread é inicializada concorrentemente ao sistema para fazer
         * requisições ao servidor. A cada REFRESH_TIME_INFO, as informações do
         * paciente selecionado são atualizadas.
         */
        Thread threadUpdateInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable updater = new Runnable() {

                    @Override
                    public void run() {
                        if (selectedRefresh != null) {
                            showRefreshDetails();
                        }
                    }
                };

                while (true) {
                    try {
                        Thread.sleep(REFRESH_TIME_INFO);
                    } catch (InterruptedException ie) {
                        System.err.println("Thread de atualização de informação finalizada de maneira "
                                + "inesperada.");
                        System.out.println(ie);
                    }

                    // A atualização é feita na thread da aplicação.
                    Platform.runLater(updater);
                }
            }
        });

        /* Impede a thread de finalizar a JVM. */
        threadUpdateInfo.setDaemon(true);
        threadUpdateInfo.start();
        
        //Limitando os tipos de caracteres que podem ser digitados nos campos de texto.
        txtAmountPatients.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtAmountPatients.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    /**
     * Inicializa a conexão cliente com o servidor através do endereço IP e
     * porta especificados.
     */
    private static void initClient() {
        try {
            client = new Socket(IP_ADDRESS, PORT);
            System.out.println("Conexão estabelecida!");
        } catch (IOException ioe) {
            System.err.println("Erro, a conexão com o servidor não foi "
                    + "estabelecida!");
            System.out.println(ioe);

            try {
                client.close();
            } catch (IOException ioex) {
                System.err.println("Não foi possível fechar a porta da conexão.");
                System.out.println(ioex);
            }
        }
    }

    /**
     * Inicializa a tabela de pacientes com dados presentes na lista de
     * pacientes ordenada.
     */
    private void initTable() {
        clmID.setCellValueFactory(new PropertyValueFactory("deviceId"));
        clmUserName.setCellValueFactory(new PropertyValueFactory("name"));
        clmSituation.setCellValueFactory(new PropertyValueFactory("isSeriousConditionLabel"));

        if (client != null) {
            requestPatientsDevices();
            table.setItems(listToObservableList());
        } else {
            System.out.println("Tentando se conectar ao servidor...");
        }
    }

    /**
     * Converte a lista de pacientes de forma a ser compatível com tabela da
     * interface.
     *
     * @return ObservableList<PatientDevice> - Pacientes ordenados.
     */
    private ObservableList<PatientDevice> listToObservableList() {
        return FXCollections.observableArrayList(patients);
    }

    /**
     * Faz requisição ao servidor para resgatar a lista de pacientes do sistema.
     */
    private void requestPatientsDevices() {
        try {
            //Faz a conexão com o servidor.
            initClient();

            patients.removeAll(patients);

            amountPatients = txtAmountPatients.getText().equals("")
                    ? 5
                    : Integer.parseInt(txtAmountPatients.getText());

            JSONObject json = new JSONObject();
            json.put("method", "GET");
            json.put("route", "/patients/" + amountPatients);

            //Enviando a requisição para o servidor.
            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
            output.flush();
            output.writeObject(json);

            //Recebendo a resposta do servidor.
            ObjectInputStream input = new ObjectInputStream(client.getInputStream());

            JSONObject jsonResponse = (JSONObject) input.readObject();
            
            JSONArray jsonArray = jsonResponse.getJSONArray("data");

            /* Adicionando os dispositivos dos pacientes em uma lista.*/
            for (int i = 0; i < jsonArray.length(); i++) {
                patients.add(
                        new PatientDevice(
                                jsonArray.getJSONObject(i).
                                        getString("name"),
                                jsonArray.getJSONObject(i).
                                        getString("deviceId"),
                                jsonArray.getJSONObject(i).
                                        getString("isSeriousConditionLabel"),
                                (FogServer) jsonArray.getJSONObject(i).
                                        get("fogServer")
                        )
                );
            }

            client.close();
        } catch (IOException ex) {
            Logger.getLogger(MonitoringController.class.getName()).log(Level.SEVERE, null, ex);
            lblStatus.setText("Sem conexão");
            lblStatus.setStyle("-fx-text-fill: red");
            client = null;
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Classe JSONObject não foi encontrada.");
            System.out.println(cnfe);
        }
    }

    /**
     * Requisita todos os dados do paciente selecionado.
     *
     * @param deviceId String - Identificação do dispositivo.
     * @return PatientDevice
     */
    private PatientDevice requestSpecificPatient(PatientDevice patientDevice) {
        //Faz a conexão do cliente com o servidor.
        Socket conn = null;

        try {
            conn = new Socket(
                    patientDevice.getFogServer().getAddress(), 
                    patientDevice.getFogServer().getPort()
            );
            System.out.println("Conexão estabelecida com a Fog do paciente!");

            JSONObject json = new JSONObject();
            json.put("method", "GET");
            json.put("route", "/patient/" + patientDevice.getDeviceId());

            ObjectOutputStream output = new ObjectOutputStream(conn.getOutputStream());
            output.flush();
            output.writeObject(json);

            ObjectInputStream input = new ObjectInputStream(conn.getInputStream());

            JSONObject jsonResponse = (JSONObject) input.readObject();
            
            conn.close();
            
            if(jsonResponse.has("error")){
                setPatientInfosVisibility(false);
                return null;
            }
            
            return new PatientDevice(
                    jsonResponse.getJSONObject("data").getString("name"),
                    jsonResponse.getJSONObject("data").getString("deviceId"),
                    jsonResponse.getJSONObject("data").getFloat("bodyTemperature"),
                    jsonResponse.getJSONObject("data").getInt("respiratoryFrequency"),
                    jsonResponse.getJSONObject("data").getFloat("bloodOxygenation"),
                    jsonResponse.getJSONObject("data").getInt("bloodPressure"),
                    jsonResponse.getJSONObject("data").getInt("heartRate"),
                    jsonResponse.getJSONObject("data").getBoolean("isSeriousCondition"),
                    jsonResponse.getJSONObject("data").getString("isSeriousConditionLabel"),
                    jsonResponse.getJSONObject("data").getFloat("patientSeverityLevel"),
                    (FogServer) jsonResponse.getJSONObject("data").get("fogServer")
            );
        } catch (IOException ioe) {
            System.err.println("Erro, a conexão com o servidor não foi "
                    + "estabelecida!");
            System.out.println(ioe);

            try {
                conn.close();
            } catch (IOException e) {
                System.err.println("Não foi possível fechar a porta da conexão.");
                System.out.println(e);
            }
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Classe JSONObject não foi encontrada.");
            System.out.println(cnfe);
        }

        return null;
    }

    /**
     * Exibe todas as informações do paciente selecionado.
     */
    private void showDetails() {
        if (selected != null) {
            PatientDevice temp = requestSpecificPatient(selected);
            selectedRefresh = selected;
            if (selectedRefresh != null) {
                selected = temp;
                this.setPatientInfosVisibility(true);

            } else {
                this.setPatientInfosVisibility(false);
            }
        }
    }

    /**
     * Atualiza as informações do paciente selecionado.
     */
    private void showRefreshDetails() {
        if (selectedRefresh != null) {
            PatientDevice temp
                    = requestSpecificPatient(selectedRefresh);

            if (temp != null) {
                selectedRefresh = temp;

                lblUserName.setText(selectedRefresh.getName());
                lblRespiratoryFrequency.setText(
                        String.valueOf(
                                selectedRefresh.getRespiratoryFrequency())
                        + " movimentos/min"
                );
                lblTemperature.setText(
                        String.format(
                                "%.1f", selectedRefresh.getBodyTemperature()
                        ).replace(",", ".") + " ºC"
                );
                lblBloodOxygen.setText(
                        String.format(
                                "%.1f", selectedRefresh.getBloodOxygenation()
                        ).replace(",", ".") + " %"
                );
                lblHeartRate.setText(
                        String.valueOf(selectedRefresh.getHeartRate())
                        + " batimentos/min"
                );
                lblBloodPressure.setText(
                        String.valueOf(selectedRefresh.getBloodPressure())
                        + " mmHg"
                );
                lblSeverityLevel.setText(
                        String.format(
                                "%.1f", selectedRefresh.getPatientSeverityLevel()
                        ).replace(",", ".")
                );
            }

        }
    }

    /**
     * Altera a visibilidade dos campos de informações dos pacientes.
     *
     * @param status boolean - true: Visível | false: Não visível
     */
    private void setPatientInfosVisibility(boolean status) {
        if (status) {
            paneInfo.setStyle("-fx-background-color: #eaeaea; -fx-border-color: #dfdfdf; -fx-border-radius: 8;");

            lblUserName.setText(selected.getName());
            lblRespiratoryFrequency.setText(String.valueOf(selected.getRespiratoryFrequency()) + " movimentos/min");
            lblTemperature.setText(String.format("%.1f", selected.getBodyTemperature()).replace(",", ".") + " ºC");
            lblBloodOxygen.setText(String.format("%.1f", selected.getBloodOxygenation()).replace(",", ".") + " %");
            lblHeartRate.setText(String.valueOf(selected.getHeartRate()) + " batimentos/min");
            lblBloodPressure.setText(String.valueOf(selected.getBloodPressure()) + " mmHg");
            lblSeverityLevel.setText(String.format("%.1f", selected.getPatientSeverityLevel()).replace(",", "."));
        } else {
            paneInfo.setStyle("-fx-background-color: #dfdfdf; -fx-border-color: #dfdfdf; -fx-border-radius: 8;");
            lblSelectPatient.setText("ERRO AO REQUISITAR \nOS DADOS");

            lblUserName.setText("");
            lblRespiratoryFrequency.setText("");
            lblTemperature.setText("");
            lblBloodOxygen.setText("");
            lblHeartRate.setText("");
            lblBloodPressure.setText("");
            lblSeverityLevel.setText("");
        }

        /* Esse campo é o inverso dos demais */
        lblSelectPatient.setVisible(!status);

        txtUserName.setVisible(status);
        txtRespiratoryFrequency.setVisible(status);
        txtTemperature.setVisible(status);
        txtBloodOxygen.setVisible(status);
        txtHeartRate.setVisible(status);
        txtBloodPressure.setVisible(status);
        txtSeverityLevel.setVisible(status);

    }
}
