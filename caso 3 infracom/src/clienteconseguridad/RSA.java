package clienteconseguridad;

import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

public class RSA 
{
	/**
	 * Encriptar un mensaje
	 * @param strAEncriptar mensaje a incriptar
	 * @param llave Pública del Servidor	
	 * @return mensaje encriptado
	 * @throws Exception manda si hay algún error
	 */

	public static String encriptar(String strAEncriptar, PublicKey llavePublicaServidor)throws Exception
	{
		try
		{
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, llavePublicaServidor);
			String hexa = DatatypeConverter.printBase64Binary(cipher.doFinal(strAEncriptar.getBytes()));
			return hexa;
		}
		catch (Exception e)
		{
			throw new Exception ("Error encriptando: " + e.toString());
		}
	}
	/**
	 * Encriptar un mensaje
	 * @param strAEncriptar mensaje a incriptar
	 * @param llave Publica del Servidor	
	 * @return mensaje encriptado
	 * @throws Exception manda si hay algún error
	 */
	public static String encriptar(byte [] strAEncriptar, PublicKey llavePublicaServidor)throws Exception
	{
		try
		{
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, llavePublicaServidor);
			String hexa = DatatypeConverter.printBase64Binary(cipher.doFinal(strAEncriptar));
			return hexa;
		}
		catch (Exception e)
		{
			throw new Exception ("Error encriptando: " + e.toString());
		}
	}
	/**
	 * Desencriptar un mensaje
	 * @param strAEncriptar mensaje a incriptar
	 * @param llave Publica del Servidor	
	 * @return mensaje incriptado
	 * @throws Exception manda si hay algún error
	 */
	public static String desencriptar(String strAEncriptar, PublicKey llavePublicaServidor)throws Exception
	{
		try
		{
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, llavePublicaServidor);
			String hexa = DatatypeConverter.printBase64Binary(cipher.doFinal(DatatypeConverter.parseBase64Binary(strAEncriptar)));
			return hexa;
		}
		catch (Exception e)
		{
			throw new Exception ("Error desencriptando: " + e.toString());
		}
	}

}
