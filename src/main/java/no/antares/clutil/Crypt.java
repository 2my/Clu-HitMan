package no.antares.clutil;import java.io.*;import java.security.Key;import java.security.SecureRandom;import javax.crypto.Cipher;import javax.crypto.KeyGenerator;import sun.misc.BASE64Decoder;import sun.misc.BASE64Encoder;public class Crypt {	private static final String DES_CIPHER = "DES/ECB/PKCS5Padding";	private static final File KEY_FILE = new File( "des.key" );	private static final String ENCODING = "ISO8859-1";	/** Encrypts or decrypts password, generates decryption key if not found. */	public static void main(String[] args) {		if ("-e".equals(args[0])) {			System.out.println(Crypt.encryptPassword(args[1]));		} else if ("-d".equals(args[0])) {			System.out.println(Crypt.decryptPassword(args[1]));		}	}	/** Encrypts password, generates decryption key if not found. */	private static String encryptPassword(String password) {		try {			Cipher cipher = Cipher.getInstance(DES_CIPHER);			cipher.init(Cipher.ENCRYPT_MODE, getKey());			byte[] stringBytes = password.getBytes( ENCODING );			byte[] raw = cipher.doFinal(stringBytes);			BASE64Encoder encoder = new BASE64Encoder();			return encoder.encode(raw);		} catch (Exception e) {			throw new RuntimeException("Error encrypting password", e);		}	}	/** Decrypts password, generates decryption key if not found. */	public static String decryptPassword(String encyptedPassword) {		try {			Cipher cipher = Cipher.getInstance(DES_CIPHER);			cipher.init(Cipher.DECRYPT_MODE, getKey());			BASE64Decoder decoder = new BASE64Decoder();			byte[] raw = decoder.decodeBuffer(encyptedPassword);			byte[] stringBytes = cipher.doFinal(raw);			return new String(stringBytes, ENCODING);		} catch (Exception e) {			throw new RuntimeException("Error decrypting password", e);		}	}	private static Key getKey() {		Key key = null;		try {			ObjectInputStream in = new ObjectInputStream(new FileInputStream( KEY_FILE ));			key = (Key) in.readObject();			in.close();		} catch (java.io.FileNotFoundException fnfe) {			try {				KeyGenerator generator = KeyGenerator.getInstance("DES");				generator.init(new SecureRandom());				key = generator.generateKey();				ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream( KEY_FILE ) );				out.writeObject(key);				out.close();			} catch (Exception e) {				throw new RuntimeException("Error in crypting", e);			}		} catch (Exception e) {			throw new RuntimeException("Error in crypting", e);		}		return key;	}}