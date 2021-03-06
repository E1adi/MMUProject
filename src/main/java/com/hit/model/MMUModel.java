package com.hit.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import com.google.gson.Gson;
import com.hit.algorithm.IAlgoCache;
import com.hit.algorithm.LRUAlgoCacheImpl;
import com.hit.algorithm.MFUAlgoCacheImpl;
import com.hit.algorithm.SecondChanceAlgoCacheImpl;
import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.processes.Process;
import com.hit.processes.ProcessCycles;
import com.hit.processes.RunConfiguration;
import com.hit.util.MMULogger;

public class MMUModel extends Observable implements Model{

	public int numProcesses;
	public int ramCapacity;
	private String IAlgoName;
	private List<String> commands;
	MMULogger logger = MMULogger.getInstance();
	
	public MMUModel(String[] configuration) {
		ramCapacity = Integer.parseInt(configuration[1]);
		IAlgoName = configuration[0];
	}
	
	public List<String> getCommands() {
		return commands;
	}
	
	@Override
	public void readData() {
		try {
			commands = Files.readAllLines(Paths.get(MMULogger.DEFAULT_FILE_NAME));
		} catch (IOException e) {
			MMULogger.getInstance().write(e.getMessage(), Level.SEVERE);
			MMULogger.getInstance().close();
		}
	}

	@Override
	public void start() {
		MemoryManagementUnit mmu = new MemoryManagementUnit(ramCapacity, algorithmsFactory(IAlgoName, ramCapacity));
		RunConfiguration runConfiguration;
		List<Process> processesList;
		
		logger.write(MessageFormat.format("RC:{0}{1}", ramCapacity, System.lineSeparator()), Level.INFO);
		runConfiguration = readConfigurationFile();
		processesList = processesCreator(runConfiguration, mmu);
		numProcesses = processesList.size();
		
		runProcesses(processesList);
		System.out.println("Done.");
		
		logger.close();
		
		setChanged();
		notifyObservers(new Integer(numProcesses));
	}
	
	private void runProcesses(List<Process> processesList) {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		for(Process process: processesList) {
			executor.execute(new Thread(process));
		}
		
		executor.shutdown();
		
		try 
		{
			executor.awaitTermination(2, TimeUnit.MINUTES);
		} 
		catch (InterruptedException exception) 
		{
			MMULogger.getInstance().write(exception.getMessage(), Level.SEVERE);
		}
	}

	
	private List<Process> processesCreator(RunConfiguration runConfiguration, MemoryManagementUnit mmu) {
		List<Process> processes = new ArrayList<Process>();
		int processId = 0;
		
		for(ProcessCycles pc: runConfiguration.getProcessesCycles()) {
			processes.add(new Process(processId, mmu, pc));
			processId++;
		}
		
		logger.write(MessageFormat.format("PN:{0}{1}{1}",processId ,System.lineSeparator()), Level.INFO);
		return processes;
	}
	
	private RunConfiguration readConfigurationFile()
	{
		FileReader configurationFile = null;
		try 
		{
			configurationFile = new FileReader("resources/configuration/Configuration.json");
		} 
		catch (FileNotFoundException exception) 
		{
			logger.write(exception.getMessage(), Level.SEVERE);
		}
		return new Gson().fromJson(configurationFile, RunConfiguration.class);
	}
	
	private IAlgoCache<Long, Long> algorithmsFactory(String algoName, int capacity) {
		switch(algoName.toLowerCase()) {
			case "lru": {
				return new LRUAlgoCacheImpl<Long, Long>(capacity);			
			}
			case "mfu": {
				return new MFUAlgoCacheImpl<Long, Long>(capacity);			
			}
			case "second chance": {
				return new SecondChanceAlgoCacheImpl<Long, Long>(capacity);
			}
			default: {
				return null;			
			}
		}
	}
}
