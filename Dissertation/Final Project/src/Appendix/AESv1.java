package Appendix;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AESv1 {

    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static MessageDigest sha;

    private static Cipher cipher;
    private static byte[] Ciphertext;

    //main class
    public static void main(String[] args) {

        try {
            //cipher instantiated
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        //create key string
        setKey("Bar12345Bar12345");
        System.out.println("");
        try {
            encrypt("Hello!");  //encrypt message 'hello'
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("");

        decrypt(getCiphertext());



    }
    //set key method
    private static void setKey(String myKey) {

        try {
            key = myKey.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        System.out.println("Key Length: " + key.length);
        try {
            System.out.println("Your Key: " + new String(key, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        secretKey = new SecretKeySpec(key, "AES");
    }

    //encryot method
    private static String encrypt(String Plaintext) throws UnsupportedEncodingException {

        try {   //cipher initialised
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        //get bytes from plaintext
        byte[] messageBytes;
        messageBytes = Plaintext.getBytes("UTF-8");

        try {
            Ciphertext = cipher.doFinal(messageBytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        //print out encrypted
        System.out.println("Encrypted Bytecode: " + new String(Ciphertext));
        System.out.println("Bytecode address after Encryption: " + Arrays.toString(Ciphertext));
        setCiphertext(Ciphertext);          //change from string to byte
        return Plaintext;
    }

    //decrypt method
    private static String decrypt(byte[] Ciphertext)    {

        try { //cipher initialised
            cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        //strip off cipher to get message bytes
        byte[] messageChar = new byte[0];
        try {
            messageChar = cipher.doFinal(Ciphertext);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        try {   //plaintext equals message bytes
            String Plaintext = new String(messageChar, "UTF-8");

        System.out.println("Decrypted Bytecode " + Plaintext);
        System.out.println("Bytecode address after decryption: " + Arrays.toString(messageChar));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
//getters and setters
    private static byte[] getCiphertext() {
        return Ciphertext;
    }

    private static void setCiphertext(byte[] Ciphertext) {
        AESv1.Ciphertext = Ciphertext;
    }
}


//Help from: http://aesencryption.net/