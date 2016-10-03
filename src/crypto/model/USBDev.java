package crypto.model;

import com.sun.jna.platform.win32.Advapi32Util;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

/**
 * Класс "USB накопитель"
 */
public class USBDev {
    private String name; // Имя устройства
    private String serial; // Серийный номер устройства

    /**
     * Конструктор
     * @param pair - ключ и значение из реестра
     */
    USBDev(Map.Entry pair) throws UnsupportedEncodingException {
        // Из ключа получаем имя накопителя
        String _name = String.valueOf(pair.getKey()).replace("\\DosDevices\\", "");
        this.name = _name;

        // Из значения получаем UID
        byte[] binValue = Advapi32Util.registryGetBinaryValue(HKEY_LOCAL_MACHINE, "SYSTEM\\MountedDevices", String.valueOf(pair.getKey()));
        String _uid = new String(binValue, "Windows-1251");

        // Разбиваем UID на части по знаку "#"
        String[] uidArr = _uid.split("#");

        try {
            this.serial = uidArr[2]; // Получаем серийный номер
        }
        catch (Exception ex) {
            System.out.println("Fail." + this.name);
        }
    }

    /**
     * Геттер для поля name
     * @return serial
     */
    public String getSerial() {
        return serial;
    }

    /**
     * Метод для вывода устройства
     * @return name
     */
    @Override
    public String toString() { return this.name; }
}
