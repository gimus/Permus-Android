package com.gimus.permus.crypto;

import android.util.Base64;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {

    public static PrivateKey getPrivateKeyFromXml(String xString) {
        SAXBuilder builder = new SAXBuilder();

        BigInteger modulus = null, exponent = null, primeP = null, primeQ = null,
                primeExponentP = null, primeExponentQ = null,
                crtCoefficient = null, privateExponent = null;
        PrivateKey key=null;
        try {
            InputStream stream = new ByteArrayInputStream(xString.getBytes("UTF-8"));
            Document document = (Document) builder.build(stream);
            Element root = document.getRootElement();
            modulus = new BigInteger(1,b64decode(root.getChild("Modulus").getValue()));
            exponent = new BigInteger(1,b64decode(root.getChild("Exponent").getValue()));
            primeP = new BigInteger(1,b64decode(root.getChild("P").getValue()));
            primeQ = new BigInteger(1,b64decode(root.getChild("Q").getValue()));
            primeExponentP = new BigInteger(1,b64decode(root.getChild("DP").getValue()));
            primeExponentQ = new BigInteger(1,b64decode(root.getChild("DQ").getValue()));
            crtCoefficient = new BigInteger(1,b64decode(root.getChild("InverseQ").getValue()));
            privateExponent = new BigInteger(1,b64decode(root.getChild("D").getValue()));

            RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec (
                    modulus, exponent, privateExponent, primeP, primeQ,
                    primeExponentP, primeExponentQ, crtCoefficient);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
             key = keyFactory.generatePrivate(keySpec);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return key;
    }






    public static String getPrivateKeyBase64Encoded(PrivateKey key) {
        return Base64.encodeToString(getPrivateKey(key) , Base64.DEFAULT);
    }

    public static byte[] getPrivateKey(PrivateKey key) {
        return key.getEncoded();
    }

    public static PrivateKey getPrivateKey(String bKeyDataBase64) {
        return getPrivateKey(Base64.decode(bKeyDataBase64,  Base64.DEFAULT));
    }

    public static PrivateKey getPrivateKey(byte[] bKeyData) {
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(bKeyData));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPublicKeyBase64Encoded(PublicKey key) {
        return Base64.encodeToString(getPublicKey(key) , Base64.DEFAULT);
    }

    public static byte[] getPublicKey(PublicKey key) {
        return key.getEncoded();
    }

    public static PublicKey getPublicKey(String bKeyDataBase64) {
        return getPublicKey(Base64.decode(bKeyDataBase64,  Base64.DEFAULT));
    }

    public static PublicKey getPublicKeyFromXml(String xString) {
        SAXBuilder builder = new SAXBuilder();
        PublicKey key=null;
        BigInteger modulus = null, exponent = null ;
        try {
            InputStream stream = new ByteArrayInputStream(xString.getBytes("UTF-8"));
            Document document = (Document) builder.build(stream);
            Element root = document.getRootElement();
            modulus = new BigInteger(1, b64decode(root.getChild("Modulus").getValue()));
            exponent = new BigInteger(1, b64decode(root.getChild("Exponent").getValue()));

            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            key = keyFactory.generatePublic(keySpec);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return key;
    }




    public static PublicKey getPublicKey(byte[] bKeyData) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bKeyData));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getUTF8Bytes(String s) {
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
     public static String getUTFString(byte[] b) {
         try {
             return new String(b,"UTF-8");
         } catch (UnsupportedEncodingException e) {
             return null;
         }
     }

    public static KeyPair generaKeyPair() {
        return generaKeyPair(2048);
    }

    public static KeyPair generaKeyPair(int KeySize) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(KeySize);
            return  kpg.genKeyPair();
         } catch (Exception e) { return null; }
    }

    public static String encryptToBase64(String s, PublicKey publicKey, Boolean fOAEP) {
        byte[] buf = getUTF8Bytes(s);
        return encryptToBase64(buf, publicKey, fOAEP);
    }

    public static String encryptToBase64(byte[] data, PublicKey publicKey, Boolean fOAEP) {
        try {
            byte[] encData = encrypt(data, publicKey, fOAEP);
            return Base64.encodeToString(encData , Base64.DEFAULT);
        } catch (Exception e) { return null; }
    }
    public static byte[] encrypt(String s, PublicKey publicKey, Boolean fOAEP) {
        try {
            byte sb[] =  getUTF8Bytes(s);
            return encrypt(sb ,publicKey, fOAEP);
        } catch (Exception e) { return null; }
    }

    public static byte[] encrypt(byte[] data, PublicKey publicKey, Boolean fOAEP) {
        try {
            Cipher c;

            if (fOAEP)
                 c = Cipher.getInstance("RSA/ECB/OAEPPadding");
            else
                 c = Cipher.getInstance("RSA/ECB/PKCS1PADDING");


            SecureRandom random = new SecureRandom();
            c.init(Cipher.ENCRYPT_MODE, publicKey, random);
            byte[] encData=c.doFinal(data);
            return  encData;
        } catch (Exception e) { return null; }
     }

    public static String decryptToUTF8String(String base64EncodedString, PrivateKey privateKey, Boolean fOAEP) {
        byte[] decData=decrypt(base64EncodedString, privateKey, fOAEP);
        return getUTFString(decData);
    }

    public static byte[] decrypt(String base64EncodedString, PrivateKey privateKey, Boolean fOAEP) {
        try {
            byte[] encodedBytes = Base64.decode(base64EncodedString,  Base64.DEFAULT);
            return decrypt(encodedBytes, privateKey, fOAEP);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decryptToUTF8String(byte[] data, PrivateKey privateKey, Boolean fOAEP) {
        byte[] decData=decrypt(data, privateKey, fOAEP);
        return getUTFString(decData);
    }

    public static byte[] decrypt(byte[] data, PrivateKey privateKey, Boolean fOAEP) {
        try {

            Cipher c;

            if (fOAEP)
                c = Cipher.getInstance("RSA/ECB/OAEPPadding");
            else
                c = Cipher.getInstance("RSA/ECB/PKCS1PADDING");

            c.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decData=c.doFinal(data);
            return decData;
        } catch (Exception e) {
            return null;
        }
    }


    public static String getPublicKeyAsXmlString(KeyPair keyPair) {
        return getPublicKeyAsXmlString((RSAPrivateKey) keyPair.getPrivate());
    }


    public static String getPublicKeyAsXmlString(RSAPrivateKey privateKey) {
        try{
            StringBuffer buff = new StringBuffer(1024);

            RSAPrivateCrtKey pvkKey = (RSAPrivateCrtKey) privateKey;

            buff.append("<RSAKeyValue>") ;
            buff.append("<Modulus>" + b64encode(removeMSZero(pvkKey.getModulus().toByteArray())) + "</Modulus>");
            buff.append("<Exponent>" + b64encode(removeMSZero(pvkKey.getPublicExponent().toByteArray())) + "</Exponent>");
            buff.append("</RSAKeyValue>") ;
            return buff.toString();
        }
        catch(Exception e)
        {System.err.println(e);
            return null ;
        }
    }

    public static String getPrivateKeyAsXmlString(KeyPair keyPair) {
           return getPrivateKeyAsXmlString((RSAPrivateKey) keyPair.getPrivate());
    }

    public static String getPrivateKeyAsXmlString(RSAPrivateKey privateKey) {
        try {
            StringBuffer buff = new StringBuffer(1024);

            RSAPrivateCrtKey pvkKey = (RSAPrivateCrtKey) privateKey;

            buff.append("<RSAKeyValue>") ;
            buff.append("<Modulus>" + b64encode(removeMSZero(pvkKey.getModulus().toByteArray())) + "</Modulus>");
            buff.append("<Exponent>" + b64encode(removeMSZero(pvkKey.getPublicExponent().toByteArray())) + "</Exponent>");
            buff.append("<P>" + b64encode(removeMSZero(pvkKey.getPrimeP().toByteArray())) + "</P>");
            buff.append("<Q>" + b64encode(removeMSZero(pvkKey.getPrimeQ().toByteArray())) + "</Q>");
            buff.append("<DP>" +b64encode(removeMSZero(pvkKey.getPrimeExponentP().toByteArray())) + "</DP>");
            buff.append("<DQ>" + b64encode(removeMSZero(pvkKey.getPrimeExponentQ().toByteArray())) + "</DQ>");
            buff.append("<InverseQ>" + b64encode(removeMSZero(pvkKey.getCrtCoefficient().toByteArray())) + "</InverseQ>");
            buff.append("<D>" + b64encode(removeMSZero(pvkKey.getPrivateExponent().toByteArray())) + "</D>");
            buff.append("</RSAKeyValue>") ;

            return buff.toString();
        }
        catch(Exception e)
        {System.err.println(e);
            return null ;
        }
    }

    public enum HashAlgorithmEnum { SHA1withRSA, SHA256withRSA, SHA384withRSA, SHA512withRSA }

    public static byte[] sign(byte[] data, PrivateKey privateKey, HashAlgorithmEnum HashAlgorithm   ) {
        try {
            Signature sig = Signature.getInstance(HashAlgorithm.toString());
            sig.initSign(privateKey);
            sig.update(data);
            return sig.sign();
         } catch (Exception e) { return null; }
    }




    // --------- remove leading (Most Significant) zero byte if present  ----------------
    private static byte[] removeMSZero(byte[] data) {
        byte[] data1 ;
        int len = data.length;
        if (data[0] == 0)
        {
            data1 = new byte[data.length-1] ;
            System.arraycopy(data, 1, data1, 0, len-1);
        }
        else
            data1 = data;

        return data1;
    }

    private static final String b64encode(byte[] data) {
        return Base64.encodeToString(data , Base64.DEFAULT);
    }

    private static final byte[]  b64decode(String s) {
        return Base64.decode(s, Base64.DEFAULT);
    }

}
