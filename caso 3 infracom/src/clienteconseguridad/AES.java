package clienteconseguridad;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class AES 
{
	/**
	 * Genera la llave simetrica
	 * @return llave simetrica
	 * @throws Exception si se presenta algún error.
	 */
	public static SecretKey generadorDeLlave() throws Exception {
		KeyGenerator keyGen = null;
		try {
			keyGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			throw new Exception (e.getMessage());
		}

		//Inicializar el generador de llaves en el tamaño de la llave 128
		keyGen.init(128);

		//Generar la clave secreta
		SecretKey secretKey = keyGen.generateKey();
		return secretKey;
	}
	/**
	 * Encriptar un mensaje
	 * @param strAEncriptar mensaje a incriptar
	 * @param llaveSecreta	llave simetrica
	 * @return mensaje incriptado
	 * @throws Exception manda si hay algún error
	 */

	public static String encriptar(String strAEncriptar, SecretKey llaveSecreta)throws Exception
	{
		while (strAEncriptar.length()%4!=0)
		{
			strAEncriptar+="0";
		}
		try
		{

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, llaveSecreta);
			byte[]  crypted = cipher.doFinal(DatatypeConverter.parseBase64Binary(strAEncriptar));
			return DatatypeConverter.printBase64Binary(crypted);
		}
		catch (Exception e)
		{
			throw new Exception ("Error encriptando: " + e.toString());
		}
	}
	/**
	 * Desencriptar mensaje.
	 * @param strToDecrypt Mensaje a desencriptar
	 * @param llaveSecreta Llave simetrica
	 * @return mensaje
	 * @throws Exception si hay algun error.
	 */
	public static String desencriptar(String strToDecrypt, SecretKey llaveSecreta)throws Exception
	{
		try
		{

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, llaveSecreta);

			byte[] original = cipher.doFinal(DatatypeConverter.parseBase64Binary(strToDecrypt));
			return DatatypeConverter.printBase64Binary(original);
			
		}
		catch (Exception e)
		{
			throw new Exception ("Error desencriptando: " + e.toString());
		}
		
	}
}
