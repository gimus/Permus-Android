package com.gimus.permus.crypto;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES128 {

     private Cipher cipher;
     private IvParameterSpec ivSpec;
    private SecretKeySpec keySpec;
    private String lastKey="";

    public AES128() throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        InitVectorsFromKey("stringa di inizializzazione di default");
    }

    protected void InitVectorsFromKey( String key) throws UnsupportedEncodingException {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(key.getBytes("UTF-8"));
            byte[] aIv = new byte[16];
            byte[] aKey = new byte[16];

            System.arraycopy(hash,0,aKey,0,16 );
            System.arraycopy(hash,16,aIv,0,16 );

            ivSpec = new IvParameterSpec(aIv);
            keySpec = new SecretKeySpec(aKey, "AES");
            lastKey=key;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    /**
     * Encrypt the string with this internal algorithm.
     *
     * @param toBeEncrypt string object to be encrypt.
     * @return returns encrypted string.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String encrypt(String toBeEncrypt, String key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        try {
            if (key != lastKey)
               InitVectorsFromKey(key);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(toBeEncrypt.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Decrypt this string with the internal algorithm. The passed argument should be encrypted using
     * {@link #encrypt(String,String) encrypt} method of this class.
     *
     * @param encrypted encrypted string that was encrypted using {@link #encrypt(String,String) encrypt} method.
     * @return decrypted string.
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String decrypt(String encrypted, String key) throws InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        return decrypt( Base64.decode(encrypted, Base64.DEFAULT), key );
    }

    public String decrypt(byte[] encryptedBytes, String key) throws InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        try {

            if (key != lastKey)
                InitVectorsFromKey(key);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }




    public static String enc(String toBeEncrypted, String key) {
        try {
            AES128 aes= new AES128();
            return aes.encrypt(toBeEncrypted,key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String dec(byte[] encryptedBytes, String key) {
        try {
            AES128 aes= new AES128();
            return aes.decrypt(encryptedBytes, key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String dec(String encrypted, String key) {
        try {
            AES128 aes= new AES128();
            return aes.decrypt(encrypted ,key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

   public static String SHA128(String s){
        MessageDigest digest = null;
       try {
           digest = MessageDigest.getInstance("SHA");
       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
       }
       try {
           byte[] hash = digest.digest(s.getBytes("UTF-8"));
           return Base64.encodeToString(hash, Base64.DEFAULT);
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }
       return "";
    }
}
