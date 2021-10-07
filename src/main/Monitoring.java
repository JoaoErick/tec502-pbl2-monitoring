
package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Classe responsável por instanciar a janela da aplicação.
 * @author João Erick Barbosa
 */
public class Monitoring extends Application {
    
    private static Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Monitoring.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Monitoramento - COVID-19");
        stage.setResizable(false);
        stage.show();
        setStage(stage);
        
        Image image = new Image("/image/monitoring-covid-icon.png");

        stage.getIcons().add(image);
    }

    /**
     * Resgata argumentos passados por linha de comando.
     * @param args 
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Retorna o objeto da janela instanciada.
     * @return Stage
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Altera o objeto da janela instanciada.
     * @return Stage
     */
    public static void setStage(Stage stage) {
        Monitoring.stage = stage;
    }
    
}
