package Final;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by Karstan on 20/05/2017.
 */
public class keyGenerator {

    private static int bitLength = 512;
    private static int p = 7120; //prime
    private static int g = 2;   //generator- primitive root modulo of prime


    private static BigInteger generatorValue, primeValue;      //generator and prime fro both keys
    private static BigInteger publicA, secretA, sharedKeyA;    //keys for personA
    private static  BigInteger publicB, sharedKeyB, secretB;    //keys for personB
    public static SecretKey secretKey1, secretKey2;
    public static IvParameterSpec ivSpec;



    public void main() throws Exception {

        getSecretKey1();
        setSecretKey1(secretKey1);

        getSecretKey2();
        setSecretKey2(secretKey2);

        getIvSpec();
        setIvSpec(ivSpec);

        keyGenerator();
    }

    public keyGenerator() {

        try {
            main();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void keyGenerator() throws Exception {


        //--------------------------------------------------------------------------------------------------------------
        //Keys defined: -
        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");

        primeValue = BigInteger.valueOf((long) p);  //findPrime();
        System.out.println("the prime is " + primeValue);

        generatorValue = BigInteger.valueOf((long) g); // findPrimeRoot(primeValue);
        System.out.println("the generator of the prime is " + generatorValue);

        //keys for personA
        secretA = new BigInteger(bitLength - 2, randomSecureRandom);
        publicA = generatorValue.modPow(secretA, primeValue);

        // keys for personB
        secretB = new BigInteger(bitLength - 2, randomSecureRandom);
        publicB = generatorValue.modPow(secretB, primeValue);


        //--------------------------------------------------------------------------------------------------------------
        //Keys made and displayed:

        //should be same for A and B
        sharedKeyA = publicB.modPow(secretA, primeValue);
        sharedKeyB = publicA.modPow(secretB, primeValue);

        System.out.println("the shared key for A is " + sharedKeyA);
        System.out.println("the shared key for B is " + sharedKeyB);

        //--------------------------------------------------------------------------------------------------------------
        //Keys converted from BigInt to secret key: -

        byte[] key1Bytes = sharedKeyA.toByteArray();
        byte[] key2Bytes = sharedKeyA.toByteArray();

        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] key1Sha = sha.digest(key1Bytes);
        byte[] key2Sha = sha.digest(key2Bytes);

        byte[] key1 = Arrays.copyOf(key1Sha, 16); // use only first 128 bits
        secretKey1 = new SecretKeySpec(key1, "AES");

        byte[] key2 = Arrays.copyOf(key2Sha, 16); // use only first 128 bits
        secretKey2 = new SecretKeySpec(key1, "AES");

        //--------------------------------------------------------------------------------------------------------------

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
       // byte[] encoded = {-120, 17, 42, 121, 111, 123, 56, 65, 76, 87, 45, 34, 26, 65, 103, 122};
       // secretKey = new SecretKeySpec(encoded, "AES");

    }

    public static SecretKey getSecretKey1() {
        return secretKey1;
    }

    public static void setSecretKey1(SecretKey secretKey1) {
        keyGenerator.secretKey1 = secretKey1;
    }

    public static SecretKey getSecretKey2() {
        return secretKey2;
    }

    public static void setSecretKey2(SecretKey secretKey2) {
        keyGenerator.secretKey2 = secretKey2;
    }

    public static IvParameterSpec getIvSpec() {
        return ivSpec;
    }

    public static void setIvSpec(IvParameterSpec ivSpec) {
        keyGeneration.ivSpec = ivSpec;
    }
}