package com.hit.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.hit.algorithm.IAlgoCache;
import com.hit.algorithm.LRUAlgoCacheImpl;
import com.hit.algorithm.MFUAlgoCacheImpl;
import com.hit.algorithm.MRUAlgoCacheImpl;
import com.hit.algorithm.SecondChanceAlgoCacheImpl;
import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.processes.Process;
import com.hit.processes.ProcessCycles;
import com.hit.processes.RunConfiguration;
import com.hit.util.MMULogger;

public class MMUModel extends Observable implements Model{

	public int numProcesses;
	public int ramCapacity;
	
	
	
	public MMUModel(String[] configuration) {
		
	}
	
	public List<String> getCommands() {
		return null;
	}
	
	@Override
	public void readData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
	
	private void runProcesses(List<Process> processesList) {
		Executor executor = Executors.newCachedThreadPool();
				
		for(Process process: processesList) {
			executor.execute(new Thread(process));
		}
		
		((ExecutorService) executor).shutdown();
	}

	
	private List<Process> processesCreator(RunConfiguration runConfiguration, MemoryManagementUnit mmu) {
		List<Process> processes = new ArrayList<Process>();
		int processId = 0;
		
		for(ProcessCycles pc: runConfiguration.getProcessesCycles()) {
			processes.add(new Process(processId, mmu, pc));
			processId++;
		}
		
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
			MMULogger logger = MMULogger.getInstance();
			logger.write(exception.getMessage(), Level.SEVERE);
		}
		return new Gson().fromJson(configurationFile, RunConfiguration.class);
	}
	
	private IAlgoCache<Long, Long> algorithmsFactory(String algoName, int capacity) {
		switch(algoName.toLowerCase()) {
			case "lru": {
				return new LRUAlgoCacheImpl<Long, Long>(capacity);			
			}
			case "mru": {
				return new MRUAlgoCacheImpl<Long, Long>(capacity);			
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
