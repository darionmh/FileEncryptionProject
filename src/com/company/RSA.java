package com.company;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;


public class RSA {
    private final static BigInteger one      = new BigInteger("1");
    private final static SecureRandom random = new SecureRandom();

    public BigInteger privateKey;
    public BigInteger publicKey;
    public BigInteger modulus;

    //public 65537
    //private 110028069397471233636128527993239844865
    //modulus 160249557404819597314360911809701004393

    // generate an N-bit (roughly) public and private key
    RSA(int N) {
        BigInteger phi;
        do {
            BigInteger p = BigInteger.probablePrime(N, random);
            BigInteger q = BigInteger.probablePrime(N, random);
            phi = (p.subtract(one)).multiply(q.subtract(one));

            modulus = p.multiply(q);
            publicKey = new BigInteger("65537");     // common value in practice = 2^16 + 1
            privateKey = publicKey.modInverse(phi);
        }while(phi.mod(publicKey).toString().equals("1"));
    }

    RSA(){
        this.publicKey = new BigInteger("65537");
        this.privateKey = new BigInteger("52225520214989989735508773434503996636383493741813151852864917177700353");
        this.modulus = new BigInteger("67582267120738453100918915679279069086607271657133279713249887158198333");
    }

    BigInteger encrypt(BigInteger message) {
        return message.modPow(publicKey, modulus);
    }

    String decrypt(BigInteger encrypted) {
        return new String(encrypted.modPow(privateKey, modulus).toByteArray());
    }

    public String toString() {
        String s = "";
        s += "public  = " + publicKey  + "\n";
        s += "private = " + privateKey + "\n";
        s += "modulus = " + modulus;
        return s;
    }

    public static void main(String[] args) {
        String n = "";
        Random rand = new Random();
        for(int i=0;i<16;i++){
            n+=rand.nextInt(8)+1;
        }
        BigInteger msg = new BigInteger(n.getBytes());

        RSA key = new RSA(msg.bitCount()*2);
        BigInteger encrypt = key.encrypt(msg);
        String decrypt = key.decrypt(encrypt);
        System.out.println(n);
        System.out.println(msg);
        System.out.println(key);
        System.out.println("encrypted = " + encrypt);
        System.out.println("decrypted = " + decrypt);

        System.out.println();
    }
}
