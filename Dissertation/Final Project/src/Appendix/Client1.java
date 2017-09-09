package Final;

import javax.crypto.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;
import java.util.Scanner;

public class Client1 extends keyGeneration {

    //connection, writer and scanner
    private static final int port = 6665;
    private static final String server = "secmess.ddns.net";
    private static final String channel = "#Channel";
    private static Scanner in;
    private static PrintWriter out;

    //user inputted nicknames & boolean for message of the day
    private static Scanner consoleIn;
    private static String nickname;
    private static String username;
    private static boolean serverDone = false;

    //message, ciphertext & cipher (see line 45)
    public static String message;
    public static byte[] ciphertext;
    private static Cipher cipher;

    public static void main(String[] args) throws Exception {

        keyGeneration key = new keyGeneration();    //call key1 and ivspec from keygeneration class
        key1 = key.getKey1();
        ivSpec = key.getIvSpec();
        cipher = Cipher.getInstance("AES/CTR/NoPadding");

        //Can use no padding ("AES/CTR/NoPadding");
        //Can also use padding ("AES/CBC/PKCS5Padding");

        connector();
        client();

        getMessage();
        getCiphertext();
        setMessage(message);
        setCiphertext(ciphertext);
    }

    //connect to server (functioning well)
    private static void connector() throws IOException {
        consoleIn = new Scanner(System.in);
        Socket socket = new Socket(server, port);
        System.out.println("***CONNECTED***");

        out = new PrintWriter(socket.getOutputStream(), true); //gets output from server
        in = new Scanner(socket.getInputStream());  //gets input via scanner
        System.out.println("***READER & SCANNER ESTABLISHED***");
    }

    //client (encryption method is called in here)
    private static void client() {
        System.out.println("Choose your nickname: ");
        nickname = consoleIn.nextLine();

        System.out.println("Choose a username: ");
        username = consoleIn.nextLine();

        //commands and strings to join
        write("NICK", nickname);
        write("USER", username + " 0 * :" + nickname);
        write("JOIN", channel);

        while (in.hasNext()) {  //read server output into client
            String serverMessage = in.nextLine();
            System.out.println("<<<< " + serverMessage);

            //so message of the day can be printed without requiring you to type messages
            if (serverMessage.contains("End of")) {
                serverDone = true;

                while (serverDone = true) {
                    try {
                        encryption();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
            //responds to pings so you don't get kicked
            if (serverMessage.startsWith("PING")) {
                String pingContents = serverMessage.split(" ", 2)[1];
                write("PONG", pingContents);
            }
        }
    }

    //write to server (used to send messages)
    private static void write(String command, String message) {
        String fullMessage = command + " " + message;
        System.out.println(">>>> " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }

    //encryption method encrypts messages to be sent
    private static void encryption() throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException {

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key1, ivSpec); //get cipher and key ready
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        System.out.println("Enter a message: ");
        message = consoleIn.nextLine();

        //plaintext message is trimmed and turned into bytecode
        message.trim();     //removes whitespace trails
        byte[] messageBytes = message.getBytes("UTF-8");
        //System.out.println("Bytecode address before encryption: " + messageBytes);
        System.out.println(Arrays.toString(messageBytes) + " Bytecode array");

        try {
            ciphertext = cipher.doFinal(messageBytes);     //message bytes are converted with ciphertext
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        //System.out.println("Encrypted bytecode: " + new String(ciphertext));    // ciphertext is printed for user
        //has to be new String(ciphertext) and not just ciphertext cause otherwise it won't encrypt
        String stringifiedCipher = new String(ciphertext);
        byte[] bytifiedString = stringifiedCipher.getBytes();

        write("PRIVMSG", channel + " :" + stringifiedCipher); //command to send encrypted message
        System.out.println("Byte length on sending: " + ciphertext.length);




        try {
            decryption();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }

    private static void decryption() throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

        try {
            cipher.init(Cipher.DECRYPT_MODE, key2, ivSpec);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


        System.out.println("");
        System.out.println("Local Decryption:");
        //message decrypted back to bytecode
        byte[] messageChar = cipher.doFinal(ciphertext);        //remove encryption = messageChar (bytes) that is ready to convert back to string in next line
        System.out.println("Bytecode address after decryption: " + Arrays.toString(messageChar));

        //message turned from bytecode to string and printed
        String plaintext = new String(messageChar, "UTF-8");
        System.out.println("Decrypted plaintext: " + plaintext);

        System.out.println("Done");

    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String message) {
        Encryptor.message = message;
    }

    public static byte[] getCiphertext() {
        return ciphertext;
    }

    public static void setCiphertext(byte[] ciphertext) {
        Encryptor.ciphertext = ciphertext;
    }
}

/*
Made with help from:

//encryption/decryption:
//http://stackoverflow.com/questions/26828649/diffiehellman-key-exchange-to-aes-or-desede-in-java
//http://aesencryption.net/

//converting string to bytecode:
//http://stackoverflow.com/questions/7401941/how-to-convert-string-into-byte-and-back

//base client:
//https://www.youtube.com/watch?v=Us_1xPgwLz0

//generating ivspec:
//http://stackoverflow.com/questions/29267435/generating-iv-for-aes-in-java
 */