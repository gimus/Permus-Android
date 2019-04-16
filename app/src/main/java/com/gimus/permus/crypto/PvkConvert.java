package com.gimus.permus.crypto;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class PvkConvert{

    private static final byte PUBLICKEYBLOB	= 	0x06;
    private static final byte PRIVATEKEYBLOB	= 	0x07;
    private static final byte CUR_BLOB_VERSION 	= 	0x02;
    private static final short RESERVED		= 	0x0000;
    private static final int CALG_RSA_KEYX		= 	0x0000a400;
    private static final int CALG_RSA_SIGN		= 	0x00002400;
    private static final int AT_KEYEXCHANGE	= 	1;
    private static final int AT_SIGNATURE 		= 	2;
    private static final int[] KEYSPECS 		= 	{0, CALG_RSA_KEYX, CALG_RSA_SIGN } ;
    private static final String MAGIC1		=	"RSA1"	; 	// 0x31415352
    private static final String MAGIC2		=	"RSA2"	; 	// 0x32415352
    private static final String OUTFILE1 		=	"_privatekeyblob" ;
    private static final String OUTFILE2 		=	"_publickeyblob" ;
    private static final String XMLRSAPRIKEY	=	"XMLPriKey.txt" ;
    private static final String XMLRSAPUBKEY	=	"XMLPubKey.txt" ;
    private static int bitlen 			=	00;
    private static int bytelen			= 	00;

    private static byte[] modulus		= null;
    private static int pubexp 		= 0;

    public static void main(String args[]){
        DataOutputStream dos 	= null;
        RSAPrivateKey mypvkKey	= null;

        if (args.length != 1 && args.length !=2)
        {
            System.out.println("Usage:\n  PvkConvert <PKCS#8PrivateKeyFile>");
            System.exit(0) ;
        }

        FileOutputStream fos = null;

        try{
            int keyspec =  AT_KEYEXCHANGE ;
            byte[] encodedPrivKey = getFileBytes(args[0]);
            byte [] privatekeyblob = privatekeyinfoToPrivatekeyblob(encodedPrivKey, keyspec) ;
            byte [] publickeyblob  =  toPublickeyblob(modulus, pubexp, keyspec) ;
            System.out.println("\nKeySize: " +bitlen + " bits");
            int blobbytes = 20 + 9*bytelen/2;		//for sanity check of PRIVATEKEYBLOB size.
            if(privatekeyblob.length != blobbytes) {
                System.out.println("Privatekeyblob length problem");
                System.exit(0) ;
            }
            fos = new FileOutputStream(OUTFILE1);
            fos.write(privatekeyblob);
            fos.close();
            System.out.println("Wrote PRIVATEKEYBLOB file  '" + OUTFILE1 + "'  (" + privatekeyblob.length + " bytes)") ;
            System.out.println("Dumping RSA private key components ...") ;
            dumpRSAPrivatekey(encodedPrivKey);

            fos = new FileOutputStream(OUTFILE2);
            fos.write(publickeyblob);
            fos.close();
            System.out.println("\n\nWrote PUBLICKEYBLOB file  '" + OUTFILE2 + "'  (" + publickeyblob.length + " bytes)") ;


            // -----------  get the XML-encoded RSA private and public keys for .NET 1.1 usage  -----------------------
            String xmlprikey = privatekeyinfoToXMLRSAPriKey(encodedPrivKey).replaceAll("[ \t\n\r]" ,"");  //remove an CrLf etc..
            String xmlpubkey = privatekeyinfoToXMLRSAPubKey(encodedPrivKey).replaceAll("[ \t\n\r]" ,"");  //remove an CrLf etc..

            System.out.println("\n\nXML Privatekey:");
            System.out.println(xmlprikey) ;
            FileWriter fwri = new FileWriter(XMLRSAPRIKEY);
            fwri.write(xmlprikey);
            fwri.close();
            System.out.println("\nWrote XML RSA private key file '" + XMLRSAPRIKEY  + "'") ;

            System.out.println("\n\nXML Publickey:");
            System.out.println(xmlpubkey) ;
            fwri = new FileWriter(XMLRSAPUBKEY);
            fwri.write(xmlpubkey);
            fwri.close();
            System.out.println("\nWrote XML RSA public key file '" + XMLRSAPUBKEY + "'") ;
        }

        catch(Exception e) {System.err.println(e);}
        finally{
            try{if (fos !=null)  fos.close(); }
            catch (IOException e) {}
        }
    }




    private static byte[]  privatekeyinfoToPrivatekeyblob(byte[] encodedPrivkey,  int keyspec) {
        System.out.println("keyspec " + keyspec) ;
        if(encodedPrivkey == null || (keyspec != AT_KEYEXCHANGE && keyspec !=AT_SIGNATURE))
            return null;
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            PKCS8EncodedKeySpec pvkKeySpec = new PKCS8EncodedKeySpec(encodedPrivkey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateCrtKey pvkKey = (RSAPrivateCrtKey)keyFactory.generatePrivate(pvkKeySpec);

            BigInteger mod = pvkKey.getModulus();
            modulus = mod.toByteArray() ;

            if(modulus[0] == 0)     //if high-order byte is zero, it's for sign bit; don't count in bit-size calculation
                bytelen = modulus.length-1 ;
            else
                bytelen =  modulus.length ;
            bitlen = 8*bytelen;


            dos.write(PRIVATEKEYBLOB);
            dos.write(CUR_BLOB_VERSION);
            dos.writeShort(RESERVED);
            writeLEInt(KEYSPECS[keyspec], dos);    //write Little Endian
            dos.writeBytes(MAGIC2) ;
            writeLEInt(bitlen, dos);		//write Little Endian
            pubexp = Integer.parseInt(pvkKey.getPublicExponent().toString()) ;
            writeLEInt(pubexp, dos);		//write Little Endian

            byte[] data = modulus;
            ReverseMemory(data);	//reverse array to Little Endian order; since data is same ref. as modulus, modulus is also reversed.
            dos.write(data, 0, bytelen) ;	// note that modulus may contain an extra zero byte (highest order byte after reversing)
            // specifying bytelen bytes to write will drop high-order zero byte

            data = pvkKey.getPrimeP().toByteArray();
            ReverseMemory(data);
            dos.write(data,0,bytelen/2) ;

            data = pvkKey.getPrimeQ().toByteArray();
            ReverseMemory(data);
            dos.write(data,0,bytelen/2) ;

            data = pvkKey.getPrimeExponentP().toByteArray();
            ReverseMemory(data);
            dos.write(data,0,bytelen/2) ;

            data = pvkKey.getPrimeExponentQ().toByteArray();
            ReverseMemory(data);
            dos.write(data,0,bytelen/2) ;

            data = pvkKey.getCrtCoefficient().toByteArray();
            ReverseMemory(data);
            dos.write(data,0,bytelen/2) ;

            data = pvkKey.getPrivateExponent().toByteArray();
            ReverseMemory(data);
            dos.write(data,0,bytelen) ;
            dos.flush();
            dos.close();
            return bos.toByteArray();
        }
        catch(Exception e)
        {System.err.println(e);
            return null ;}

    }

//  --- Returns XML encoded RSA private key string suitable for .NET  CryptoServiceProvider.FromXmlString(true) ------
//  ---  Leading zero bytes (most significant) must be removed for XML encoding for .NET; otherwise format error ---

    private static String  privatekeyinfoToXMLRSAPriKey(byte[] encodedPrivkey) {
        try{
            StringBuffer buff = new StringBuffer(1024);

            PKCS8EncodedKeySpec pvkKeySpec = new PKCS8EncodedKeySpec(encodedPrivkey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateCrtKey pvkKey = (RSAPrivateCrtKey)keyFactory.generatePrivate(pvkKeySpec);

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




//  --- Returns XML encoded RSA public  key string suitable for .NET  CryptoServiceProvider.FromXmlString(true) ------
//  ---  Leading zero bytes (most significant) must be removed for XML encoding for .NET; otherwise format error ---

    private static String  privatekeyinfoToXMLRSAPubKey(byte[] encodedPrivkey) {
        try{
            StringBuffer buff = new StringBuffer(1024);

            PKCS8EncodedKeySpec pvkKeySpec = new PKCS8EncodedKeySpec(encodedPrivkey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateCrtKey pvkKey = (RSAPrivateCrtKey)keyFactory.generatePrivate(pvkKeySpec);
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



    //----------------  Modulus bytes must be in big endian order here -------------------
    private static byte[]  toPublickeyblob(byte[] modulus,  int pubexp, int keyspec) {
        if(modulus == null || (keyspec != AT_KEYEXCHANGE && keyspec !=AT_SIGNATURE))
            return null;
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            dos.write(PUBLICKEYBLOB);
            dos.write(CUR_BLOB_VERSION);
            dos.writeShort(RESERVED);
            writeLEInt(KEYSPECS[keyspec], dos);    //write Little Endian
            dos.writeBytes(MAGIC1) ;
            writeLEInt(bitlen, dos);		//write Little Endian
            writeLEInt(pubexp, dos);		//write Little Endian

            byte[] data = modulus;
            dos.write(data, 0, bytelen) ;   // note that modulus may contain an extra zero byte (highest order byte after reversing)
            // specifying bytelen bytes to write will drop high-order zero byte
            dos.flush();
            dos.close();
            return bos.toByteArray();
        }
        catch(Exception e)
        {System.err.println(e);
            return null ;}

    }









    private static void dumpRSAPrivatekey(byte[] encodedPrivkey) {

        try{
            PKCS8EncodedKeySpec pvkKeySpec = new PKCS8EncodedKeySpec(encodedPrivkey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            //---- Display private key components in BigInteger decimal format  --
            RSAPrivateCrtKey pvkKey = (RSAPrivateCrtKey)keyFactory.generatePrivate(pvkKeySpec);
            System.out.println("\nModulus:\n" + pvkKey.getModulus().toString());
            displayData(pvkKey.getModulus().toByteArray()) ;

            System.out.println("\n\nPublic Exponent:\n" + pvkKey.getPublicExponent().toString());
            displayData(pvkKey.getPublicExponent().toByteArray()) ;

            System.out.println("\n\nPrime P:\n" + pvkKey.getPrimeP().toString());
            displayData( pvkKey.getPrimeP().toByteArray()) ;

            System.out.println("\n\nPrime Q:\n" + pvkKey.getPrimeQ().toString());
            displayData( pvkKey.getPrimeQ().toByteArray()) ;

            System.out.println("\n\nPrime Exponent P:\n" + pvkKey.getPrimeExponentP().toString());
            displayData( pvkKey.getPrimeExponentP().toByteArray()) ;

            System.out.println("\n\nPrime Exponent Q:\n" + pvkKey.getPrimeExponentQ().toString());
            displayData( pvkKey.getPrimeExponentQ().toByteArray()) ;

            System.out.println("\n\nCrtCoeff:\n" + pvkKey.getCrtCoefficient().toString());
            displayData(pvkKey.getCrtCoefficient().toByteArray()) ;

            System.out.println("\n\nPrivate Exponent:\n" + pvkKey.getPrivateExponent().toString());
            displayData(pvkKey.getPrivateExponent().toByteArray()) ;

        }
        catch(Exception e) {System.err.println(e);}
    }




    private static final String b64encode(byte[] data) { 	//Use internal sun class for B64 encoding
        return Base64.encodeToString(data , Base64.DEFAULT);
     }



    private static void writeLEInt(int i, OutputStream out) throws IOException {
        out.write (i & 0xFF);
        out.write((i >>>8)  & 0xFF);
        out.write((i >>>16) & 0xFF);
        out.write((i >>>24) & 0xFF);
    }


    private static void displayData(byte[] data)
    {
        int bytecon = 0;    //to get unsigned byte representation
        for(int i=1; i<=data.length ; i++){
            bytecon = data[i-1] & 0xFF ;   // byte-wise AND converts signed byte to unsigned.
            if(bytecon<16)
                System.out.print("0" + Integer.toHexString(bytecon).toUpperCase() + " ");   // pad on left if single hex digit.
            else
                System.out.print(Integer.toHexString(bytecon).toUpperCase() + " ");   // pad on left if single hex digit.
            if(i%16==0)
                System.out.println();
        }
    }



    private static void ReverseMemory (byte[] pBuffer)
    {
        byte b ;
        int iLength = pBuffer.length;
        for (int i = 0 ; i < iLength/ 2 ; i++)
        {
            b = pBuffer [i] ;
            pBuffer [i] = pBuffer [iLength - i - 1] ;
            pBuffer [iLength - i - 1] = b ;
        }
    }


    private static byte[] getFileBytes(String infile){
        File f = new File(infile) ;
        int sizecontent = ((int) f.length());
        byte[] data = new byte[sizecontent];
        try
        {
            FileInputStream freader = new FileInputStream(f);
            freader.read(data, 0, sizecontent) ;
            freader.close();
            return data;
        }
        catch(IOException ioe)
        {
            System.out.println(ioe.toString());
            return null;
        }
    }



}