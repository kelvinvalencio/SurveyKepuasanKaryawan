/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Crypto;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 *
 * @author Kelvin Valencio
 */
public class Hasher {
    public static String hashIt(String algorithm, String input){
    try{
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);
        
        while(hashtext.length() < 32){
            hashtext = "0" + hashtext;
        }
        return hashtext.toUpperCase();
    }catch(NoSuchAlgorithmException e){
        throw new RuntimeException(e);
    }
    }
    
    public static String getSha2(String input){
    try{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);
        
        while(hashtext.length() < 32){
            hashtext = "0" + hashtext;
        }
        return hashtext.toUpperCase();
    }catch(NoSuchAlgorithmException e){
        throw new RuntimeException(e);
    }
    }
    
}
