package crypto;

import crypto.model.ListUSB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Главный класс приложения
 */

public class MainApp extends Application {
    public static ListUSB listUSB; // Список подключенных USB-накопители
    public static Stage mainStage; // Главная сцена (форма)

    @Override
    // Метод запуска приложения
    public void start(Stage primaryStage) throws Exception{
        listUSB = new ListUSB(); // Загружаем все подключенные USB-накопители

        // Загружаем форму MainOverview.fxml задаем ей заголовок и иконку
        Parent root = FXMLLoader.load(getClass().getResource("view/MainOverview.fxml"));
        primaryStage.setTitle("OctoCrypt");
        primaryStage.getIcons().add(new Image(getClass().getResource("res/icon.png").toString()));

        // Загружаем форму в приложение и отображаем ее
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
        mainStage = primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
