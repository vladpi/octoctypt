package crypto.model;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * Шифровальщик
 * Выполняет шифрование и дешифрование файлов
 */
public class Encryptor {
    private static final int IV_LENGTH = 16; // константа для длинны iv

    /**
     *  Метод шифрования
     *  @param in - поток ввода
     *  @param out - поток вывода
     *  @param password - ключ шифрования
    */
    private static void encrypt(InputStream in, OutputStream out, String password) throws Exception{

        // Создание IV
        SecureRandom r = new SecureRandom();
        byte[] iv = new byte[IV_LENGTH];
        r.nextBytes(iv); // Генерируем IV
        out.write(iv); // Записываем IV в выходной файл
        out.flush();

        // Формирование 128-ми битного ключа для AES шифрования
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), iv, 65536, 128);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        // Формирование IV для дальнейшего использования
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Передаем все параметры для шифрования
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secret, ivSpec);

        // Шифруем и записываем в файл
        out = new CipherOutputStream(out, cipher);
        byte[] buf = new byte[1024];
        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        out.close();
    }


    /**
     *  Метод расшифровки
     *  @param in - поток ввода
     *  @param out - поток вывода
     *  @param password - ключ шифрования
     */
    private static void decrypt(InputStream in, OutputStream out, String password) throws Exception{
        // Читаем IV
        byte[] iv = new byte[IV_LENGTH];
        in.read(iv);

        // Формирование 128-ми битного ключа для AES расшифровки
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), iv, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Формирование IV для дальнейшего использования
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Передаем все параметры для расшифровки
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding"); //"AES/CBC/PKCS5Padding"
        cipher.init(Cipher.DECRYPT_MODE, secret, ivSpec);

        // Расшифровка файла и запись в файл
        in = new CipherInputStream(in, cipher);
        byte[] buf = new byte[1024];
        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        out.close();
    }

    /**
     *  Метод запуска процесса
     *  @param mode - режим (шифрование/расшифровка)
     *  @param filename - имя файла
     *  @param key - ключ шифрования
     */
    public static void process(int mode, String filename, String key) throws Exception {
        FileInputStream is = new FileInputStream(filename); // Поток для чтения файла
        FileOutputStream os; // Поток для записи в файл

        if(mode==Cipher.ENCRYPT_MODE){ // Если выбран режим шифрования
            os = new FileOutputStream(filename + ".octo"); // Инициализируем поток вывода
            encrypt(is, os, key); // Шифруем

        }
        else if(mode==Cipher.DECRYPT_MODE){ // Если выбран режим расшифровки
            os = new FileOutputStream(filename.replace(".octo", "")); // Инициализируем поток вывода
            decrypt(is, os, key); // Расшифровываем
        }
        else throw new Exception("unknown mode"); // Иначе вызываем исключение

        // Закрываем потоки
        is.close();
        os.close();

        // Удаляем файл
        new File(filename).delete();
    }
}
