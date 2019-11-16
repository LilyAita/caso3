package mediciones;

import java.io.PrintWriter;
import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;



public class EscritorMedidas {

	private long verificacion;	
	private long respuesta;

	private int perdidas;
	private int tCliente;
	private int tServidor;

	private double cpuLoad;

	private boolean falla;

	public EscritorMedidas() {

		verificacion = 0;
		respuesta = 0;

		falla = false;
	}

	public void startVerificacion() {

		verificacion = System.nanoTime();
	}

	public void finishVerificacion() {

		verificacion = System.nanoTime() - verificacion;
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

	public void registrarFallo() {

		falla = true;
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
		cpuLoad = ((int)(value * 1000) / 10.0);
	}

	public void escribirResultado(int n) {

		try {

			PrintWriter pw = new PrintWriter("./data/prueba_0" + n+".txt", "UTF-8");
			pw.println("Tiempo verificación: " + verificacion + " ns");
			pw.println("Tiempo respuesta: " + respuesta + " ns");

			pw.println("Medida de CPU: " + cpuLoad + "%");

			pw.println("Transacciones perdidas: " + perdidas);

			String fallo = falla ? "Fallido" : "Correcto";

			pw.println("Estado: " + fallo);
			pw.close();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
