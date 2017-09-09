package Final;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;

public class keyGeneration {

    private static byte[] byteKey;
    public static SecretKey key1, key2;
    public static IvParameterSpec ivSpec;
    private static final int AES_KEY_SIZE = 128;
    private static KeyPairGenerator keyPairGenerator;
    public static SecretKey secretKey;

    public void main() throws Exception {

        //Generates and initialises a KeyPairGenerator
        //This uses default parameters, generating own takes too long to be viable
        keyPairGenerator = KeyPairGenerator.getInstance("DiffieHellman");
        keyPairGenerator.initialize(1024);
        keyGeneration();


        getKey1();
        setKey1(key1);

        getKey2();
        setKey2(key2);

        getIvSpec();
        setIvSpec(ivSpec);

        getSecretKey();
        setSecretKey(secretKey);


    }

    public keyGeneration() {

        try {
            main();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //key generation class that generates keys
    public static void keyGeneration() throws Exception {

        // Generates keyPairs for Alice and Bob
        KeyPair keyPair1 = keyGeneration.genDHKeyPair();
        KeyPair keyPair2 = keyGeneration.genDHKeyPair();

        // Gets the public key of Alice(g^X mod p) and Bob (g^Y mod p)
        PublicKey publicKeyPair1 = keyPair1.getPublic();
        PublicKey publicKeyPair2 = keyPair2.getPublic();

        // Gets the private key of Alice X and Bob Y
        PrivateKey privateKeyPair1 = keyPair1.getPrivate();
        PrivateKey privateKeyPair2 = keyPair2.getPrivate();
        try {

            // Computes secret keys for Alice (g^Y mod p)^X mod p == Bob (g^X mod p)^Y mod p
            key1 = keyGeneration.agreeSecretKey(privateKeyPair1, publicKeyPair2);
            key2 = keyGeneration.agreeSecretKey(privateKeyPair2, publicKeyPair1);
        } catch (Exception e) {     //java.lang.exception
            e.printStackTrace();
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        //byte[] iv = new byte[cipher.getBlockSize()];
        //randomSecureRandom.nextBytes(iv);
        //ivSpec = new IvParameterSpec(iv);


        //for testing and debugging purposes
        byte[] iv = {0, 1, 2, 9, 6, 8, 3, 5, 4, 0, 9, 3, 1, 8, 0, 7};
        ivSpec = new IvParameterSpec(iv);

        System.out.println("The shared keys are: ");
        System.out.println(key1);
        System.out.println(key2);
        System.out.println("");
        System.out.println("The Initialisation Vector is: " + ivSpec);
        System.out.println("Start of Client:");
        System.out.println("");

        //premade key for testing purposes
        byte[] encoded = {-120, 17, 42, 121, 111, 123, 56, 65, 76, 87, 45, 34, 26, 65, 103, 122};
        secretKey = new SecretKeySpec(encoded, "AES");

    }

    //secret key agreement
    public static SecretKey agreeSecretKey(PrivateKey privateKey_self, PublicKey publicKey_peer) throws Exception {

        // Instantiates and initialises a KeyAgreement
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(privateKey_self);

        // Computes the KeyAgreement
        keyAgreement.doPhase(publicKey_peer, true);

        // Generates a shared secret
        byte[] secret = keyAgreement.generateSecret();

        //A key derivation function with user input would be a safer alternative
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byteKey = Arrays.copyOf(sha256.digest(secret), AES_KEY_SIZE / Byte.SIZE);

        return new SecretKeySpec(byteKey, "AES");
    }

    public static KeyPair genDHKeyPair() {
        return keyPairGenerator.genKeyPair();
    }

    //getters and setter for testing
    public SecretKey getKey1() {
        return key1;
    }

    public static void setKey1(SecretKey key1) {
        keyGeneration.key1 = key1;
    }

    public static SecretKey getKey2() {
        return key2;
    }

    public static void setKey2(SecretKey key2) {
        keyGeneration.key2 = key2;
    }

    public static IvParameterSpec getIvSpec() {
        return ivSpec;
    }

    public static void setIvSpec(IvParameterSpec ivSpec) {
        keyGeneration.ivSpec = ivSpec;
    }

    public static SecretKey getSecretKey() {
        return secretKey;
    }

    public static void setSecretKey(SecretKey secretKey) {
        keyGeneration.secretKey = secretKey;
    }
}

//Help From: http://stackoverflow.com/questions/26828649/diffiehellman-key-exchange-to-aes-or-desede-in-java