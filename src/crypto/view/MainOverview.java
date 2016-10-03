package crypto.view;

import crypto.MainApp;
import crypto.model.Encryptor;
import crypto.model.USBDev;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;

/**
 * Контроллер формы MainOverview
 * Обеспечаивает работу формы.
 */
public class MainOverview {
    @FXML
    private ComboBox<USBDev> comboUSB; // Выпадающий список для подключенных USB-накопителей
    @FXML
    private Label path; // Путь к выбранному файлу
    @FXML
    private RadioButton codeButton; // Переключатель для режима "Зашифровать"
    @FXML
    private RadioButton encodeButton; // Переключатель для режима "Расшифровать"

    @FXML
    // Инициализация формы перед запуском
    private void initialize() throws IOException {
        // Загружаем в выпадающий список данные из списка подключенных накопителей
        comboUSB.getItems().addAll(MainApp.listUSB.getData());

        comboUSB.setPlaceholder(new Text("Нет подключенных устройств"));
    }

    @FXML
    // Обработчик кнопки обновления списка накопителей
    private void refreshList() throws IOException {
        MainApp.listUSB.refresh(); // Обновляем список
        comboUSB.getItems().clear(); // Очищаем выпадающий список
        comboUSB.getItems().addAll(MainApp.listUSB.getData()); // Загружаем новые данные
    }

    @FXML
    // Обработчик кнопки открытия файла
    private void handleOpen() {
        // Инициализируем новый FileChoser
        FileChooser fileChooser = new FileChooser();

        if (encodeButton.isSelected()) // Если активе режим расшифровки
            // Добавляем фильтры для открываемых файлов
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OCTO", "*.octo"));

        // Отображаем окно выбора файла
        File file = fileChooser.showOpenDialog(MainApp.mainStage);


        if (file != null) // Если файл выбран
            path.setText(file.toString()); // Запоминаем его место положение
    }

    @FXML
    // Обработчик кнопки для запуска процесса
    private void proccess() throws Throwable {

        // Если все пункты заполнены
        if (comboUSB.getSelectionModel().getSelectedIndex() != -1 && (codeButton.isSelected() || encodeButton.isSelected()) && !path.equals("")) {

            if (codeButton.isSelected()) { // Если установлен режим шифрования
                // Запускаем процесс шифрования с параметрами
                Encryptor.process(Cipher.ENCRYPT_MODE, path.getText(), comboUSB.getValue().getSerial());
            } else { // Иначе
                // Запускаем процесс расшифровки с параметрами
                Encryptor.process(Cipher.DECRYPT_MODE, path.getText(), comboUSB.getValue().getSerial());
            }

            // Очищаем значения компонентов
            path.setText("");
            comboUSB.getSelectionModel().select(-1);

            // Выводим сообщение о успешном выполнении операции
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успешно");
            alert.setHeaderText(null);
            alert.setContentText("Операция успешно выполнена!");
            alert.showAndWait();
        } else { // Иначе выводим сообщение о том, что не все поля заполнены
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText(null);
            alert.setContentText("Заполните все поля!");
            alert.showAndWait();
        }
    }
}
