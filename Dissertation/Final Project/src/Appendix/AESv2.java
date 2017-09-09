package Appendix;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Arrays;

public class AESv2 {

    private static String message;
    private static byte[] ciphertext;
    private static byte[] key;
    private static SecretKey secretKey;
    private static MessageDigest sha;
    private static IvParameterSpec ivSpec;
    private static Cipher cipher;

    public static void main(String[] args) {

        try {
            keyGenerator();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            encrypt();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            decrypt();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void keyGenerator() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String myKey = "Bar12345Bar12345";  //16 byte long
        key = myKey.getBytes("UTF-8");  //convert string to key
        sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bits
        secretKey = new SecretKeySpec(key, "AES");
        System.out.println("Your key is: " + Arrays.toString(key));
        message = "Hello!";

    }

    public static void encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException {

        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[cipher.getBlockSize()];
        randomSecureRandom.nextBytes(iv);
        ivSpec = new IvParameterSpec(iv);

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec); //get cipher and key ready
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        byte[] messageBytes = new byte[0];
        try {
            messageBytes = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            ciphertext = cipher.doFinal(messageBytes);     //message bytes are converted with ciphertext
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        // ciphertext is printed
        System.out.println("Encrypted bytecode: " + new String(ciphertext));
        System.out.println("Array of cipher: " + Arrays.toString(ciphertext));
    }

    public static void decrypt() throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException {

        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        //message decrypted back to bytecode
        byte[] messageChar = new byte[0];        //remove encryption = messageChar (bytes) that is ready to convert back to string in next line
        try {
            messageChar = cipher.doFinal(ciphertext);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        System.out.println("Bytecode address after decryption: " + Arrays.toString(messageChar));

        //message turned from bytecode to string and printed
        String plaintext = new String(messageChar, "UTF-8");
        System.out.println("Decrypted plaintext: " + plaintext);
        System.out.println("Done");
    }
}

//Help from: http://aesencryption.net/