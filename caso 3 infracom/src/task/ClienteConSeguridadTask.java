package task;

import clienteconseguridad.ClienteConSeguridad;
import clientesinseguridad.ClienteSinSeguridad;
import uniandes.gload.core.Task;

public class ClienteConSeguridadTask extends Task {

	@Override
	public void fail() {
		// TODO Auto-generated method stub
		System.out.println(Task.MENSAJE_FAIL);
	}

	@Override
	public void success() {
		// TODO Auto-generated method stub
		System.out.println(Task.OK_MESSAGE);
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		ClienteConSeguridad cliente = new ClienteConSeguridad();
		
	}

}
