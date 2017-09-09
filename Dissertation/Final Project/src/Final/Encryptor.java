/**
 * Short User Guide:
 * key generation is inherited, so it does not need to be run
 * encryptor needs to be run first - input nicknames, usernames until message prompt
 * then run decryptor
 **/

package Final;

        import javax.crypto.*;
        import java.io.IOException;
        import java.io.OutputStream;
        import java.io.PrintWriter;
        import java.io.UnsupportedEncodingException;
        import java.net.Socket;
        import java.security.*;
        import java.util.Arrays;
        import java.util.Scanner;

public class Encryptor extends keyGeneration {

    //connection, writer and scanner
    private static final int port = 6665;
    private static final String server = "secmess.ddns.net";    //server address url
    private static final String channel = "#Channel";
    private static Scanner in;
    private static PrintWriter out;
    private static OutputStream out_Data;

    //user inputted nicknames & boolean for message of the day
    private static Scanner consoleIn;
    private static String nickname;
    private static String username;

    //message, ciphertext & cipher (see line 45)
    public static String message;
    public static byte[] ciphertext;
    private static Cipher cipher;

    public static void main(String[] args) throws Exception {

        keyGenerator key = new keyGenerator();    //call key1 and ivspec from keygeneration class
        key1 = key.getSecretKey1();
        secretKey = getSecretKey();     //for testing
        ivSpec = getIvSpec();
        cipher = Cipher.getInstance("AES/CTR/NoPadding");   //define padding once here for consistency

        //Can use no padding ("AES/CTR/NoPadding");
        //Can also use padding ("AES/CBC/PKCS5Padding");

        //connector method called here
        connector();
        //client method called here
        client();

        //message, ciphertext get and set for testing purposes
        getMessage();
        getCiphertext();
        setMessage(message);
        setCiphertext(ciphertext);
    }

    //connect to server (functioning well)
    private static void connector() throws IOException {
        consoleIn = new Scanner(System.in);
        Socket socket = new Socket(server, port);
        System.out.println("**CONNECTED**");

        out = new PrintWriter(socket.getOutputStream(), true); //gets output from server
        out_Data = socket.getOutputStream();

        in = new Scanner(socket.getInputStream());  //gets input via scanner
        System.out.println("**READER & SCANNER ESTABLISHED**");
    }

    //client (encryption method is called in here)
    private static void client() {
        System.out.println("Choose your nickname: ");
        nickname = consoleIn.nextLine();

        System.out.println("Choose a username: ");
        username = consoleIn.nextLine();

        //commands and strings to join
        writeString("NICK", nickname);
        writeString("USER", username + " 0 * :" + nickname);
        writeString("JOIN", channel);

        while (in.hasNext()) {  //read server output into client
            String serverMessage = in.nextLine();
            System.out.println("<<<< " + serverMessage);

            //so message of the day can be printed without requiring you to type messages
            if (serverMessage.contains("End of"))
            {
                boolean serverDone = true;
                //so you aren't prompted by scanner every half second
                while (serverDone = true)
                {
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
            //responds to pings so user doesn't get kicked
            if (serverMessage.startsWith("PING"))
            {
                String pingContents = serverMessage.split(" ", 2)[1];
                writeString("PONG", pingContents);
            }
        }
    }
    //array output for sending messages
    private static byte[] appendByteArray(byte[] a, byte[] b)
    {
        byte[] output = new byte[a.length + b.length];
        System.arraycopy(a, 0, output, 0, a.length);
        System.arraycopy(b, 0, output, a.length,b.length);

        return output;
    }

    //write to server method (used to send messages)
    private static void write(byte[] command, byte[] message) throws IOException
    {
        //byte[] fullMessage = command + " " + message;
        //bytes for sending byte array to server in order to allow decryption
        byte[] fullMessage = appendByteArray(command, new String(" ").getBytes("UTF-8"));
        fullMessage = appendByteArray(fullMessage, message);
        fullMessage = appendByteArray(fullMessage, new String("\r\n").getBytes("UTF-8"));

        System.out.print(">>>> " + new String(fullMessage));
        out_Data.write(fullMessage);
        out.flush();
    }
    //write method for writing commands to the server
    private static void writeString(String command, String message) {
        String fullMessage = command + " " + message;
        System.out.println(">>>> " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }

    //encryption method encrypts messages to be sent
    private static void encryption() throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException {

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec); //get cipher and key ready
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        //user enters message to be encrypted
        System.out.println("Enter a message: ");
        message = consoleIn.nextLine();

        //plaintext message is trimmed and turned into bytecode
        message.trim();     //removes whitespace trails
        byte[] messageBytes = message.getBytes("UTF-8");
        System.out.println(Arrays.toString(messageBytes) + " Bytecode array");

        try {   //cipher to encrypt - actual encryption here
            ciphertext = cipher.doFinal(messageBytes);     //message bytes are converted with ciphertext
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        //channel to join
        String channelStuff = channel + " :";
        byte[] outMessage = channelStuff.getBytes();
        byte[] output = appendByteArray( outMessage,  ciphertext);

        try
        {   //command to send message
            String command = "PRIVMSG";
            byte[] commandArray = command.getBytes("UTF-8");
            String backconvert = new String(commandArray, "UTF-8");

            write(commandArray, output); //command to send encrypted message`
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Byte length on sending: " + ciphertext.length);
        //byte length on message sending

        try {   //decryption class for decrypting encrypted messages
            decryption();
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }

    //decryption method for decryption
    private static void decryption() throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {

        try {   //cipher for decryption initialised
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }

        //make space for local decryption
        System.out.println("");
        System.out.println("Local Decryption:");
        //message decrypted back to bytecode
        byte[] messageChar = cipher.doFinal(ciphertext);    //ciphertext);        //remove encryption = messageChar (bytes) that is ready to convert back to string in next line
        System.out.println("Bytecode address after decryption: " + Arrays.toString(messageChar));

        //message turned from bytecode to string and printed
        String plaintext = new String(messageChar, "UTF-8");
        System.out.println("Decrypted plaintext: " + plaintext);

        System.out.println("Done");
    }

    //getters and setters for testing
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