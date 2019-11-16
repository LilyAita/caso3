package servidorSinSeguridad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class P {
	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO SS: ";
	private static X509Certificate certSer; /* acceso default */
	private static KeyPair keyPairServidor; /* acceso default */

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int ip = Integer.parseInt(br.readLine());
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);
		// Adiciona la libreria como un proveedor de seguridad.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());	

		//Numero de threads
		System.out.println(MAESTRO + "Número de threads:");
		int numThreads= Integer.parseInt(br.readLine());

		final ExecutorService pool = Executors.newFixedThreadPool(numThreads);

		// Crea el archivo de log
		File file = null;
		keyPairServidor = S.grsa();
		certSer = S.gc(keyPairServidor);
		String ruta = "./resultados.txt";

		file = new File(ruta);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file);
		fw.close();

		D.init(certSer, keyPairServidor, file);

		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");





		try { 

			for (int i=0;true;i++)
			{
				Socket sc=ss.accept();
				System.out.println(MAESTRO + "Cliente " + i + " aceptado.");
				pool.execute(new D(sc,i));

			}
		} catch (Exception e) {
			System.out.println(MAESTRO + "Error creando el socket cliente.");
			e.printStackTrace();
		}finally {
			try {
				ss.close();
			}catch ( Exception e)
			{
				e.printStackTrace();
			}
		}

	}
}
