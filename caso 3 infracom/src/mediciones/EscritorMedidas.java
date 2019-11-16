package mediciones;

import java.io.File;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;



public class EscritorMedidas {

	private long respuesta;

	private int perdidas;
	private int tCliente;
	private int tServidor;

	private double cpuLoad;
	private double veces;


	public EscritorMedidas() {

		respuesta = 0;
		cpuLoad= 0;
		veces=0;
	
	}

	

	
	public void startRespuesta() {

		respuesta = System.nanoTime();
	}

	public void finishRespuesta() {
		respuesta = System.nanoTime() - respuesta;
	}

	public void transaccionesCliente() {

		tCliente ++;
	}

	public void transaccionesServidor() {

		tServidor ++;
	}

	public void transaccionesTotales()
	{
		perdidas = Math.abs(tCliente - tServidor);
	}



	public void getSystemCpuLoad() throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[]{ "SystemCpuLoad" });
		if (list.isEmpty()) cpuLoad = Double.NaN;
		Attribute att = (Attribute)list.get(0);
		Double value = (Double)att.getValue();
		// usually takes a couple of seconds before we get real values
		if (value == -1.0) cpuLoad = Double.NaN;
		// returns a percentage value with 1 decimal point precision
		cpuLoad += ((int)(value * 1000) / 10.0);
		veces++;
	}

	public void escribirResultado(int n) {

		try {
			File file = new File("./data/prueba_0" + n+".txt");
			file.getParentFile().mkdirs();
			PrintWriter pw = new PrintWriter(file);
			pw.println("Tiempo transaccion: " + respuesta + " ns");

			pw.println("Medida de CPU: " + cpuLoad/veces + "%");

			pw.println("Transacciones perdidas: " + perdidas);

			pw.close();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
