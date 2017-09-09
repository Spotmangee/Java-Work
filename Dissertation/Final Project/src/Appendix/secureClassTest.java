package Project; /**
 * Created by Karstan on 26/04/2017.
 */
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;
import java.util.Scanner;

public class secureClassTest {

    //connection, writer and scanner
    private static final int port = 6665;
    private static final String server = "secmess.ddns.net";
    private static final String channel = "#hello";
    private static Scanner in;
    private static PrintWriter out;

    //user inputted nicknames
    private static Scanner consoleIn;
    private static String nickname;
    private static String username;
    private static boolean serverDone = false;

    //encryption + decryption
    private static SecretKey key1, key2;
    private static IvParameterSpec ivspec;
    private static final int AES_KEY_SIZE = 128;

    //message, ciphertext & plaintext
    private static String message;
    private static byte[] ciphertext;
    private static byte[] plaintext;

    public static void main(String[] args) throws Exception {

        //key generator, connector and client classes
        keyGenerator();
        connector();

        //encrypt and decrypt are called inside client
        client();

    }

    //keys generated here
    private static void keyGenerator() {

        // Generates keyPairs for Alice and Bob
    KeyPair keyPair1 = secureClassTest.genDHKeyPair();
    KeyPair keyPair2 = secureClassTest.genDHKeyPair();

    // Gets the public key of Alice(g^X mod p) and Bob (g^Y mod p)
    PublicKey publicKeyPair1 = keyPair1.getPublic();
    PublicKey publicKeyPair2 = keyPair2.getPublic();

    // Gets the private key of Alice X and Bob Y
    PrivateKey privateKeyPair1 = keyPair1.getPrivate();
    PrivateKey privateKeyPair2 = keyPair2.getPrivate();
        try {
        // Computes secret keys for Alice (g^Y mod p)^X mod p == Bob (g^X mod p)^Y mod p
        key1 = secureClassTest.agreeSecretKey(privateKeyPair1, publicKeyPair2,true);
        key2 = secureClassTest.agreeSecretKey(privateKeyPair2, publicKeyPair1,true);
    } catch (Exception e) {     //java.lang.exception
        e.printStackTrace();
    }
}





//connection here
    private static void connector() throws IOException {
        consoleIn = new Scanner(System.in);
        Socket socket = new Socket(server, port);
        System.out.println("***CONNECTED***");

        out = new PrintWriter(socket.getOutputStream(), true); //gets output from server
        in = new Scanner(socket.getInputStream());  //gets input via scanner
        System.out.println("***READER & SCANNER ESTABLISHED***");
    }


    //client here (encryption inside)
    private static void client() {
        System.out.println("Choose your nickname: ");
        nickname = consoleIn.nextLine();

        System.out.println("Choose a username: ");
        username = consoleIn.nextLine();

        write("NICK", nickname);
        write("USER", username + " 0 * :" + " Client1");
        write("JOIN", channel);

        while (in.hasNext()) {
            serverDone = false;
            String serverMessage = in.nextLine();
            System.out.println("<<<< " + serverMessage);


        if(serverMessage.contains("End of")) {
            serverDone = true;

            while(serverDone = true) {
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
            if (serverMessage.contains("�")) {
                try {
                    decryption();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
        }

            if (serverMessage.startsWith("PING")) {
                String pingContents = serverMessage.split(" ", 2)[1];
                write("PONG", pingContents);
            }   //kicks you for inactivity


        }
    }

    //write function
    private static void write(String command, String message) {
        String fullMessage = command + " " + message;
        System.out.println(">>>> " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }

    //encryption method
    private static void encryption() throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException {

        // Instantiate the Cipher of algorithm "AES"
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[cipher.getBlockSize()];
        randomSecureRandom.nextBytes(iv);
        ivspec = new IvParameterSpec(iv);
        System.out.println(ivspec);     //send iv over network (unencrypted)    something that is done by TLS (successor to SSL)

        // initiate the encryption mode with secretKey1
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key1, ivspec); //get cipher and key ready
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

        write("PRIVMSG", channel + " :" + new String(ciphertext)); //command to send encrypted message

    }

    //decryption method
    private static void decryption() throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        // initiate the decryption mode with secretKey2
       Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        try {
            cipher.init(Cipher.DECRYPT_MODE, key2, ivspec);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        //message decrypted back to bytecode
        byte[] messageChar = cipher.doFinal(ciphertext);        //remove encryption = messageChar (bytes) that is ready to convert back to string in next line
        System.out.println("Bytecode address after decryption: " + Arrays.toString(messageChar));

        //message turned from bytecode to string and printed
        String plaintext = new String(messageChar, "UTF-8");
        System.out.println("Decrypted plaintext: " + plaintext);

        System.out.println("Done");
    }

//---------------------------------------------------------------------------------------------------------------------------------

    //key generation
    private static KeyPairGenerator keyPairGenerator;

    static {
        try {
            // === Generates and inits a KeyPairGenerator ===

            // changed this to use default parameters, generating your
            // own takes a lot of time and should be avoided
            // use ECDH or a newer Java (8) to support key generation with
            // higher strength
            keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(1024);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //agreed secret key here
    public static SecretKey agreeSecretKey(PrivateKey privateKey_self, PublicKey publicKey_peer, boolean lastPhase) throws Exception {

        // instantiates and inits a KeyAgreement
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(privateKey_self);

        // Computes the KeyAgreement
        keyAgreement.doPhase(publicKey_peer, lastPhase);

        // Generates the shared secret
        byte[] secret = keyAgreement.generateSecret();

        // === Generates an AES key ===

        // should use a strong KDF instead, but this is safe

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] byteKey;
        byteKey = Arrays.copyOf(sha256.digest(secret), AES_KEY_SIZE / Byte.SIZE);

        SecretKey desSpec = new SecretKeySpec(byteKey, "AES");
        return desSpec;
    }

    public static KeyPair genDHKeyPair() {
        return keyPairGenerator.genKeyPair();
    }
}

//---------------------------------------------------------------------------------------------------------------------------------

//source for encryption/decryption:
//http://stackoverflow.com/questions/26828649/diffiehellman-key-exchange-to-aes-or-desede-in-java

//source for converting string to bytecode:
//http://stackoverflow.com/questions/7401941/how-to-convert-string-into-byte-and-back

//source for base client:
//https://www.youtube.com/watch?v=Us_1xPgwLz0

//source for ivspec:
//http://stackoverflow.com/questions/29267435/generating-iv-for-aes-in-java
