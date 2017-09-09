package Project; /**
 * Created by Karstan on 20/04/2017.
 */
import java.io.DataInputStream;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Diffie-Hellman module for demonstrating KeyAgreement Algorithm
 */

public class Cryptography {

    private static final int AES_KEY_SIZE = 128;
    private static String message;
    private static byte[] ciphertext;
    private byte[] plaintext;

    private static SecretKey key1;
    private static SecretKey key2;

    //main class where products of keys are called
    //other parts of rpogram called
    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        // Generates keyPairs for Alice and Bob
        KeyPair keyPair1 = Cryptography.genDHKeyPair();
        KeyPair keyPair2 = Cryptography.genDHKeyPair();

        // Gets the public key of Alice(g^X mod p) and Bob (g^Y mod p)
        PublicKey publicKeyPair1 = keyPair1.getPublic();
        PublicKey publicKeyPair2 = keyPair2.getPublic();

        // Gets the private key of Alice X and Bob Y
        PrivateKey privateKeyPair1 = keyPair1.getPrivate();
        PrivateKey privateKeyPair2 = keyPair2.getPrivate();

        try {
            // Computes secret keys for Alice (g^Y mod p)^X mod p == Bob (g^X mod p)^Y mod p
            key1 = Cryptography.agreeSecretKey(privateKeyPair1, publicKeyPair2,
                    true);
            key2 = Cryptography.agreeSecretKey(privateKeyPair2, publicKeyPair1,
                    true);

        } catch (Exception e) {
            System.out.println("Error with key generation: " + e.toString());
        }

        System.out.println("Key1: " + key1 + " " + "Key2: " + key2);

            //--------------------------------------------------------------------------------------------------------------
            //plaintext is entered and converted to bytecode: -

            //user inputs the message
            DataInputStream in = new DataInputStream(System.in);
            System.out.println("Enter the plain text:");
            message = in.readLine();

            //plaintext message is trimmed and turned into bytecode
            message.trim();     //removes whitespace trails
            byte[] messageBytes = message.getBytes("UTF-8");
            System.out.println("Bytecode address before encryption: " + messageBytes);
            System.out.println(Arrays.toString(messageBytes) + " Bytecode array");
            System.out.println();

            //check if both strings are the same when inputted - underlying types and structure wrapping
            //check the size of objects (size of string, int)
            //

            //--------------------------------------------------------------------------------------------------------------
            //algorithm is then instantiated and ciphertext is made

//         build the initialization vector.  This example is all zeros, but it
//         could be any value or generated using a random number generator.
//        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
//        IvParameterSpec ivspec = new IvParameterSpec(iv);

        //source: http://stackoverflow.com/questions/29267435/generating-iv-for-aes-in-java
        // Instantiate the Cipher of algorithm "AES"
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[cipher.getBlockSize()];
        randomSecureRandom.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        System.out.println(ivspec);     //send iv over network (unencrypted)
                                        //something that is done by TLS (successor to SSL)

            // initiate the encryption mode with secretKey1
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key1, ivspec); //get cipher and key ready
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        ciphertext = new byte[1];
        ciphertext = cipher.doFinal(messageBytes);     //message bytes are converted with ciphertext

            // ciphertext is printed
            System.out.println("Encrypted bytecode: " + new String(ciphertext));
        System.out.println("Array of cipher: " + Arrays.toString(ciphertext));

        for (int i = 0; i < ciphertext.length; i++)
        {
            System.out.println("Index " + i + ": " + ciphertext[i]);
        }
            //--------------------------------------------------------------------------------------------------------------
            //decryption into bytecode and then turned from bytecode to string

            // initiate the decryption mode with secretKey2
        try {
            cipher.init(Cipher.DECRYPT_MODE, key2, ivspec);
        } catch (InvalidAlgorithmParameterException e) {
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

    private static KeyPairGenerator kpg;

    static {
        try {
            // === Generates and inits a KeyPairGenerator ===

            // changed this to use default parameters, generating your
            // own takes a lot of time and should be avoided
            // use ECDH or a newer Java (8) to support key generation with
            // higher strength
            kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(1024);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void IvParameter() {

    }

    public static SecretKey agreeSecretKey(PrivateKey prk_self, PublicKey pbk_peer, boolean lastPhase) throws Exception {
        // instantiates and inits a KeyAgreement
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(prk_self);
        // Computes the KeyAgreement
        ka.doPhase(pbk_peer, lastPhase);
        // Generates the shared secret
        byte[] secret = ka.generateSecret();

        // === Generates an AES key ===

        // you should really use a Key Derivation Function instead, but this is rather safe

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] byteKey = Arrays.copyOf(sha256.digest(secret), AES_KEY_SIZE / Byte.SIZE);
        System.out.println(Arrays.toString(byteKey));

        SecretKey desSpec = new SecretKeySpec(byteKey, "AES");
        return desSpec;
    }

    public static KeyPair genDHKeyPair() {
        return kpg.genKeyPair();
    }
}
/*
//sources:
//http://stackoverflow.com/questions/26828649/diffiehellman-key-exchange-to-aes-or-desede-in-java
//http://stackoverflow.com/questions/29267435/generating-iv-for-aes-in-java
//http://stackoverflow.com/questions/7401941/how-to-convert-string-into-byte-and-back
//https://www.youtube.com/watch?v=Us_1xPgwLz0   (irc client)
//
 */
