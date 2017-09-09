/**
 * Short User Guide:
 * key generation is inherited, so it does not need to be run
 * encryptor needs to be run first - input nicknames, usernames until message prompt
 * then run decryptor
 **/

package Final;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;
import java.util.Scanner;

//decryptor for decrypting messages
public class Decryptor extends keyGeneration {

    //connection, writer and scanner
    private static final int port = 6665;
    private static final String server = "secmess.ddns.net";       //server address url
    private static final String channel = "#Channel";
    private static Scanner in;

    private static InputStream inStream;
    private static PrintWriter out;

    //user inputted nicknames
    private static Scanner consoleIn;
    private static String nickname;
    private static String username;

    //message, ciphertext & plaintext
    public static byte[] ciphertext;
    private static Cipher cipher;

    //conversion from server message
    private static byte[] messageChar;
    private static String serverMessage;
    private static byte[] serverMessageByte;

    //main where main classes are called
    public static void main(String[] args) throws Exception {

        //key generation class called here
        keyGeneration key = new keyGeneration();        //call key2 and ivspec from keygeneration class
       // secretKey2 = key.getSecretKey2();
        secretKey = key.getSecretKey();
        ivSpec = key.getIvSpec();

        cipher = Cipher.getInstance("AES/CTR/NoPadding");

        //Can use no padding ("AES/CTR/NoPadding");
        //Can also use padding ("AES/CBC/PKCS5Padding");

        try {
            //connector called here - possible connection errors below
            connector();
        } catch (IOException e) {
            System.out.println("Cannot connect to server: " + e.toString());
            System.out.println("- is the server running?" );
            System.out.println("- has the server been updated?");
            System.out.println("- has the server timed out?");
            System.out.println("- if server is not running or is unavailable, please use other irc server");
        }
        //client called here - decryption inside
        client();

        //getters and setter for testing
        getCiphertext();
        setCiphertext(ciphertext);

        getMessageChar();
        setMessageChar(messageChar);

        getServerMessage();
        setServerMessage(serverMessage);
    }

    //connect to server (functioning well)
    private static void connector() throws IOException {
        consoleIn = new Scanner(System.in);
        Socket socket = new Socket(server, port);
        System.out.println("**CONNECTED**");

        //TESTING
        inStream = socket.getInputStream();

        //BufferedReader in = new BufferedReader(   new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true); //gets output from server
        in = new Scanner(socket.getInputStream());  //gets input via scanner
        System.out.println("**READER & SCANNER ESTABLISHED**");
    }

    //client (decryption method is called in here)
    private static void client() throws BadPaddingException, IllegalBlockSizeException, IOException {

        //scanner for nickname
        System.out.println("Choose your nickname: ");
        nickname = consoleIn.nextLine();

        //scanner for username
        System.out.println("Choose a username: ");
        username = consoleIn.nextLine();

        //write username, nickanme and join channel
        write("NICK", nickname);
        write("USER", username + " 0 * : " + nickname);
        write("JOIN", channel);

        //so message of the day can be printed without requiring you to type messages
        //while (in.hasNext())

        int count;
        byte[] bytes = new byte[16*1024];
        while ((count = inStream.read(bytes)) > 0)  {
            serverMessage = new String(bytes, "UTF-8");
            serverMessage = serverMessage.substring(0, count);
            System.out.println(serverMessage);

            serverMessageByte = bytes;

            //serverMessage = in.nextLine();
            //System.out.println("<<<< " + serverMessage);

            //if message contains bit of ciphertext
            if (serverMessage.contains("�")) {          //|| serverMessage.contains("\u0002")
                //call decryption function (below)
                decryption();
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

    //decryption method decrypts messages
    private static void decryption() throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

        // System.out.println("Byte length on reception: " + ciphertext.length);    //how long message is on reception
        try {   //initiate cipher
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }

        //starting at beginning of message and ending in channel name - strip off excess data
        int startIndex = serverMessage.indexOf("<");
        int endIndex = serverMessage.indexOf(channel);
        String replacement = "";
        String replaced = serverMessage.substring(startIndex + 1, endIndex + channel.length() + 2);
        replaced = serverMessage.replace(replaced, replacement);

        int colonindex = serverMessage.lastIndexOf(":");
        int lengthofMessage = serverMessage.length();
        int lengthOfCode =(lengthofMessage - 2) - (colonindex + 1);
        byte[] cipherData = new byte[lengthOfCode];
        System.arraycopy(serverMessageByte, colonindex+1, cipherData, 0, lengthOfCode);

        System.out.println(new String(cipherData));                                    //System.out.println(serverMessage.replace(replaced, replacement));
        System.out.println("length on replacement: " + replaced.length());
        ciphertext = cipherData;//replaced.getBytes("UTF-8");

        //print server message to bytecode
        System.out.println(Arrays.toString(ciphertext));

        //strip off aes
        messageChar = cipher.doFinal(ciphertext);        //remove encryption = messageChar (bytes) that is ready to convert back to string in next line

        //message turned from bytecode to string and printed
        String plaintext = new String(messageChar, "UTF-8");
        System.out.println("Decrypted plaintext: " + plaintext);

        System.out.println("Done");
    }

    //getters and setters for testing
    public static byte[] getCiphertext() {
        return ciphertext;
    }

    public static void setCiphertext(byte[] ciphertext) {
        Decryptor.ciphertext = ciphertext;
    }

    public static byte[] getMessageChar() {
        return messageChar;
    }

    public static void setMessageChar(byte[] messageChar) {
        Decryptor.messageChar = messageChar;
    }

    public static String getServerMessage() {
        return serverMessage;
    }

    public static void setServerMessage(String serverMessage) {
        Decryptor.serverMessage = serverMessage;
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

