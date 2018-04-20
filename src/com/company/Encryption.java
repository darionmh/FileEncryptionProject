package com.company;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;

public class Encryption {

    public static void main(String argv[]) throws IOException, AES.CryptoException {
        String clientSentence;
        ServerSocket welcomeSocket = new ServerSocket(6789);

        String fileAddr = "";
        if(argv.length < 1){
            System.out.println("Please give location of file to be encrypted/decrypted.");
            Scanner in = new Scanner(System.in);
            fileAddr = in.nextLine();
        }else{
            fileAddr = argv[0];
        }

        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        Socket connectionSocket = welcomeSocket.accept();
        System.out.println("Connected to "+connectionSocket.toString());
        BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

        outToClient.writeBytes("Connected.\r\n");
        while (true) {
            clientSentence = inFromClient.readLine();
            System.out.println("Received: " + clientSentence);
            if (clientSentence == null) break;

            if(clientSentence.contains("quit")){
                outToClient.writeBytes("quit\r\n");
                break;
            }

            if(clientSentence.contains("encrypt")){
                RSA rsa = new RSA();
                SecureRandom random = new SecureRandom();

                String n = "";
                Random rand = new Random();
                for(int i=0;i<16;i++){
                    n+=rand.nextInt(8)+1;
                }

                BigInteger bi = new BigInteger(n.getBytes());

                System.out.println("AES Key:"+n);
                System.out.println("Integer representation of key: "+bi);

                BigInteger encrypted = rsa.encrypt(bi);

                System.out.println("Encrypted key: "+encrypted);
                outToClient.writeBytes("key:"+encrypted.toString()+":key\r\n");

                encryptFile(n, new File(fileAddr));
            }

            if(clientSentence.contains("decrypt")){
                RSA rsa = new RSA();
                String key = clientSentence.split(":")[1];
                BigInteger bi = new BigInteger(key);

                String decrypted = rsa.decrypt(bi);
                System.out.println("Decrypted key: "+decrypted);

                decryptFile(decrypted.substring(0,16), new File(fileAddr));
            }
        }

    }

    public static void encryptFile(String key, File file) throws AES.CryptoException {
        AES.encrypt(key, file, new File(file.getPath().replace(".", "-encrypted.")));
        file.delete();
    }

    public static void decryptFile(String key, File file) throws AES.CryptoException {
        AES.decrypt(key, file, new File(file.getPath().replace("-encrypted.", ".")));
        file.delete();
    }
}
