package main;


import task.ClienteConSeguridadTask;
import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class MainGeneratorConSeguridad {
	private LoadGenerator generator;
	
	public MainGeneratorConSeguridad ()
	{
		Task work = createTask();
		int numberOfTask = 100;
		int gapBetweenTask = 1000;
		generator = new LoadGenerator("Cliente - Servidor", numberOfTask, work,gapBetweenTask );
		generator.generate();
	}
	
	private Task createTask () {
		return new ClienteConSeguridadTask();
	}
	public static void main(String[] args) {
		MainGeneratorConSeguridad gen = new MainGeneratorConSeguridad();
	}
}
