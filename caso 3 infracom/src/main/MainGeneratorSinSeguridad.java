package main;

import clienteconseguridad.ClienteConSeguridad;
import task.ClienteSinSeguridadTask;
import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class MainGeneratorSinSeguridad {
	private LoadGenerator generator;
	
	public MainGeneratorSinSeguridad ()
	{
		Task work = createTask();
		int numberOfTask = 100;
		int gapBetweenTask = 1000;
		generator = new LoadGenerator("Cliente - Servidor", numberOfTask, work,gapBetweenTask );
		generator.generate();
	}
	
	private Task createTask () {
		return new ClienteSinSeguridadTask();
	}
	public static void main(String[] args) {
		MainGeneratorSinSeguridad gen = new MainGeneratorSinSeguridad();
	}
}
