package Appendix;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;

/**
 * Created by Karstan on 03/05/2017.
 */
public class DH1 {

    //initialises keys, key size and key generator
    private static byte[] byteKey;
    private static SecretKey key1, key2;
    private static final int AES_KEY_SIZE = 128;
    private static KeyPairGenerator keyPairGenerator;

    public static void main(String[] args) throws Exception {

        //Generates and initialises a KeyPairGenerator
        //This uses default parameters, generating own takes too long to be viable
        keyPairGenerator = KeyPairGenerator.getInstance("DiffieHellman");
        keyPairGenerator.initialize(1024);
        keyGenerator();

    }
        //key generation class
    public static void keyGenerator() throws Exception {

        // Generates keyPairs for Alice and Bob
        KeyPair keyPair1 = DH1.genDHKeyPair();
        KeyPair keyPair2 = DH1.genDHKeyPair();

        // Gets the public key of Alice(g^X mod p) and Bob (g^Y mod p)
        PublicKey publicKeyPair1 = keyPair1.getPublic();
        PublicKey publicKeyPair2 = keyPair2.getPublic();

        // Gets the private key of Alice X and Bob Y
        PrivateKey privateKeyPair1 = keyPair1.getPrivate();
        PrivateKey privateKeyPair2 = keyPair2.getPrivate();

        try {
            // Computes secret keys for Alice (g^Y mod p)^X mod p == Bob (g^X mod p)^Y mod p
            key1 = DH1.agreeSecretKey(privateKeyPair1, publicKeyPair2);
            key2 = DH1.agreeSecretKey(privateKeyPair2, publicKeyPair1);
        } catch (Exception e) {     //java.lang.exception
            e.printStackTrace();
        }

        System.out.println("The shared keys are: ");
        System.out.println(new String(String.valueOf(key1)));
        System.out.println(new String(String.valueOf(key2)));
    }

    //agreement of secret key using diffie hellman
    public static SecretKey agreeSecretKey(PrivateKey privateKey_self, PublicKey publicKey_peer) throws Exception {

        // instantiates and initialises a KeyAgreement
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(privateKey_self);

        // Computes the KeyAgreement
        keyAgreement.doPhase(publicKey_peer, true);

        // Generates the shared secret
        byte[] secret = keyAgreement.generateSecret();

        //A key derivation function with user input would be more secure
        //But this is safe enough for my needs
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byteKey = Arrays.copyOf(sha256.digest(secret), AES_KEY_SIZE / Byte.SIZE);

        SecretKey desSpec = new SecretKeySpec(byteKey, "AES");
        return desSpec;
    }

    //get key pair
    public static KeyPair genDHKeyPair() {
        return keyPairGenerator.genKeyPair();
    }


//Help From: http://stackoverflow.com/questions/26828649/diffiehellman-key-exchange-to-aes-or-desede-in-java

}