package crypto.model;

import com.sun.jna.platform.win32.Advapi32Util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

/**
 * Класс "Список накопителей"
 * Список доступных на данный момент USB накопителей
 */
public class ListUSB {
    private ArrayList<USBDev> data = new ArrayList<>(); // Список устройств

    /**
     * Конструктор
     * @throws IOException
     */
    public ListUSB() throws IOException {
        // Получаем Map из реестра
        Map<String, Object> values = Advapi32Util.registryGetValues(HKEY_LOCAL_MACHINE, "SYSTEM\\MountedDevices");

        // Преобразовываем Map в List
        mapToList(values);
    }

    /**
     * Метод преобразования Map в List
     * @param values - исходный Map из реестра
     * @throws IOException
     */
    private void mapToList(Map<String, Object> values) throws IOException {
        data.clear(); // Очищаем список

        for (Map.Entry pair : values.entrySet()) { // Для каждой пары в Map
            if (isDosDev(pair) && isUSBStor(pair) && isConnected(pair)) { // Если это USB накопитель и он подключен
                USBDev tmpDevice = new USBDev(pair); // Создаем новое устройство
                data.add(tmpDevice); // Добавляем в список
            }
        }
    }


    /**
     * Метод для проверки, является ли устройство USB-накопителем
     * @param pair - ключ и значение из реестра
     * @throws UnsupportedEncodingException
     */
    private boolean isUSBStor(Map.Entry pair) throws UnsupportedEncodingException {
        // Получаем значение из реестра
        byte[] binValue = Advapi32Util.registryGetBinaryValue(HKEY_LOCAL_MACHINE, "SYSTEM\\MountedDevices", String.valueOf(pair.getKey()));

        if (binValue[0] == 95 && binValue[8] == 85) // Если 0-ой байт равен 95 и 8-ой равен 85
            return true; // Устройство является USB-накопителем
        else // Иначе
            return false; // Устройство не является USB-накопителем
    }

    /**
     * Метод для проверки подключено ли устройство
     * @param pair - ключ и значение из реестра
     * @throws IOException
     */
    private boolean isConnected(Map.Entry pair) throws IOException {
        // Создаем переменную для нового файла
        File tmp = new File(String.valueOf(pair.getKey()).replace("\\DosDevices\\", "") + "/test.txt");

        try {
            tmp.createNewFile(); // Создаем файл
        }
        catch (Exception ex) { // Если словили ошибку возвращаем ложь
            return false;
        }

        tmp.delete(); // Удаляем файл
        return true; // Возвращаем истину
    }

    /**
     * Метод обновления списка
     * @throws IOException
     */
    public void refresh() throws IOException {
        // Получаем Map из реестра
        Map<String, Object> values = Advapi32Util.registryGetValues(HKEY_LOCAL_MACHINE, "SYSTEM\\MountedDevices");

        // Преобразовываем Map в List
        mapToList(values);
    }

    /**
     * Геттер для списка
     * @return data - список подключенных USB-накопителей
     */
    public ArrayList<USBDev> getData() {
        return data;
    }

    /**
     * Метод проверки, является ли устройство DosDevices
     * @param pair - ключ и значение из реестра
     * @return
     */
    private boolean isDosDev(Map.Entry pair) {
        return String.valueOf(pair.getKey()).contains("\\DosDevices\\");
    }

}
