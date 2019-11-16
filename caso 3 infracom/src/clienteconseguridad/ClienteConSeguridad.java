package clienteconseguridad;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class ClienteConSeguridad {
	/**
	 * Cadenas de Control de comunicación.
	 *  "HOLA", "ALGORITMOS", "OK", "ERROR"
	 */
	public final static  String cadenasDeControl[]= { "HOLA", "ALGORITMOS", "OK", "ERROR"};

	/**
	 * Separador principal 
	 */
	public final static  String separadorPrincipal = ":";

	//-----------------------------------------
	//-----           Algoritmos      ---------
	//-----------------------------------------

	/**
	 * Algoritmo simétrico 
	 */
	public final static  String ALGs= "AES";

	/**
	 * Algoritmo Asimétrico
	 */
	public final static  String ALGa= "RSA";
	/**
	 * Algoritmo HMAC
	 */
	public final static  String ALGhmac= "HMACSHA256";

	//-----------------------------------------
	//-----           Conexión      -----------
	//-----------------------------------------

	/**
	 * Socket 
	 */
	private Socket socket = null;

	/**
	 * Escritor de la conexión
	 */
	private PrintWriter escritor = null;

	/**
	 * Lector de la conexión
	 */
	private BufferedReader lector = null;

	/**
	 * Puerto
	 */
	public final static int PUERTO = 443;

	/**
	 * Host
	 */
	public final static String HOST = "localhost";
	/**
	 * Leer en consola.
	 */
	private BufferedReader bf;
	//---------------------------
	//---     Llaves        -----
	//---------------------------

	/**
	 * Llave pública del servidor
	 */
	private PublicKey llavePublicaServidor = null;

	/**
	 * Llave simétrica
	 */
	private SecretKey llaveSimetrica = null;


	/**
	 * Crea el Socket, el escritor, el lector y el buffer reader.
	 */
	public ClienteConSeguridad ()
	{

		imprimirConsola("Inicializando");
		try {
			bf = new BufferedReader(new InputStreamReader(System.in));
			socket = new Socket(HOST, PUERTO);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			imprimirConsola("Se inicializó el socket");

			etapa1();
			etapa2();
			etapa3();
			etapa4();


		} catch (Exception ex) {
			imprimirConsolaError("Fallo: "+ex.getMessage());

		}

	} 
	/**
	 * Cierra la conexión.
	 */
	public void cerrarConexion() {
		try {
			escritor.close();
			lector.close();
			socket.close();

		} catch (Exception ex) {

			imprimirConsolaError("Fallo: "+ex.getMessage());

		}

	}

	/**
	 * Etapa1:	seleccionar	algoritmos	e	iniciar	sesión
	 */
	public void etapa1() throws Exception
	{
		//Enviar Hola
		enviarMensaje(cadenasDeControl[0]);

		//Recibir el ok
		String recibi = recibirMensaje();

		if (recibi == null || !recibi.equals(cadenasDeControl[2]))
			throw new Exception ("El servidor no respondio como se esperaba"); 

		//Algoritmos 
		enviarMensaje(cadenasDeControl[1]+separadorPrincipal+ALGs+separadorPrincipal+ALGa+separadorPrincipal+ALGhmac);

		//OK|ERROR
		recibi = recibirMensaje();

		if (recibi == null || (!recibi.equals(cadenasDeControl[2]) && !recibi.equals(cadenasDeControl[3])) )
			throw new Exception ("El servidor no respondio como se esperaba"); 
		if (recibi.equals(cadenasDeControl[3]))
			throw new Exception ("El servidor mando error"); 

	}

	/**
	 * Etapa2:	Autenticación  del	servidor
	 * @throws Exception si hay algún error
	 */
	public void etapa2() throws Exception
	{ 
		//Autenticar certificado
		String certificadoString =  recibirMensaje();
		byte[] certificadoServidor = DatatypeConverter.parseBase64Binary(certificadoString);
		ByteArrayInputStream inStreamByte = new ByteArrayInputStream(certificadoServidor);
		X509Certificate cdServ = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(inStreamByte);

		llavePublicaServidor =cdServ.getPublicKey();
		cdServ.verify((PublicKey)llavePublicaServidor);
		imprimirConsola("Certificado valido, con algoritmo:"+cdServ.getSigAlgName());

		//Crear llave simetrica
		llaveSimetrica = AES.generadorDeLlave();



		//Mandar llave simetrica
		String res= RSA.encriptar(llaveSimetrica.getEncoded() , llavePublicaServidor);
		enviarMensaje(res);


		reto();

	}
	/**
	 * Etapa3:	Autenticación	del	cliente
	 * @throws Exception
	 */
	public void etapa3() throws Exception
	{
		//CC
		imprimirConsola("DIGITE SU CC:");
		String s= leerConsola();

		String respuesta =  AES.encriptar(s, llaveSimetrica);
		enviarMensaje(respuesta);

		//CLAVE
		imprimirConsola("DIGITE SU CLAVE:");
		s= leerConsola();

		respuesta =  AES.encriptar(s, llaveSimetrica);
		enviarMensaje(respuesta);


	}
	/**
	 * Etapa4:	solicitud	de	información	 y	validación	de	la	respuesta.
	 * @throws Exception
	 */
	public void etapa4() throws Exception
	{
		String s= recibirMensaje();

		String respuesta =  AES.desencriptar(s, llaveSimetrica);
		
		s=recibirMensaje();
		
		String hash =  RSA.desencriptar(s, llavePublicaServidor);

		if (hash.equals(HMACSHA.calcHmacSha256(llaveSimetrica, respuesta)))
		{
			imprimirConsola("El mensaje no se ha modificado");
			enviarMensaje(cadenasDeControl[2]);
		}
		else
		{
			enviarMensaje(cadenasDeControl[3]);
			throw new Exception ("El mensaje se ha modificado");

		}


	}
	/**
	 * Enviar y confirmar el reto
	 */
	public void reto() throws Exception {
		//Enviar
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		int reto = (int) (rand.nextInt());
		while (!(String.valueOf(reto).length() % 4 == 0) || reto<0 ) {
			//la cadena debe ser de longitud multiplo de 4
			reto = (int) (rand.nextInt()*10000+1);
		}

		imprimirConsola("Reto enviado: " + reto);
		enviarMensaje(reto+"");

		String hexa = AES.encriptar(String.valueOf(reto), llaveSimetrica);

		//Recibe reto encriptado y revisa si son iguales
		String respuesta = recibirMensaje();
		imprimirConsola("Reto recibido (encriptado): " + respuesta+", si encripto el reto:"+hexa);

		respuesta =  AES.desencriptar(String.valueOf(respuesta), llaveSimetrica);

		imprimirConsola("Reto recibido (desencriptado): " + respuesta);
		try {
			if(Integer.parseInt(respuesta)==reto)
			{
				imprimirConsola("Los retos coinciden");
				enviarMensaje(cadenasDeControl[2]);
			}
			else
			{
				enviarMensaje(cadenasDeControl[3]);
				throw new Exception ("Los retos no coinciden");

			}
		}
		catch (NumberFormatException e) {
			enviarMensaje(cadenasDeControl[3]);
			throw new Exception ("Los retos no coinciden");
		}



	}

	//-----------------------------------------
	//-----Enviar, imprimir y recibir  --------
	//-----------------------------------------
	/**
	 * Imprime en consola lo enviado por parámetro
	 * @param asdf String a imprimir
	 */
	public void imprimirConsola(String asdf) {
		System.out.println(asdf);
	}

	/**
	 * Imprime en consola el error
	 * @param asdf String del error
	 */
	public void imprimirConsolaError(String asdf) {
		System.err.println(asdf);
	}

	/**
	 * Envia el mensaje al servidor
	 * @param mensaje que se envia al servidor
	 */
	public void enviarMensaje(String mensaje) {

		escritor.append(mensaje + "\n");
		escritor.flush();

		imprimirConsola("Escribi: " + mensaje);

	}

	/**
	 * Recibir mensaje del servidor.
	 * @return mensaje del servidor
	 * @throws exception si envia algún fallo 
	 */
	public String recibirMensaje() throws Exception {
		String mensaje="";

		try
		{
			mensaje =  lector.readLine();
		} catch(IOException ex)
		{
			throw new Exception (ex.getMessage()); 
		}

		imprimirConsola("Recibi: " + mensaje);

		return mensaje;

	}
	/**
	 * Leer consola.
	 * @return mensaje en la consola
	 * @throws exception si envia algún fallo 
	 */
	public String leerConsola() throws Exception {
		String mensaje="";

		try
		{
			mensaje =  bf.readLine();
		} catch(IOException ex)
		{
			throw new Exception (ex.getMessage()); 
		}

		return mensaje;

	}


}
