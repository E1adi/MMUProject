package com.hit.driver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.hit.algorithm.IAlgoCache;
import com.hit.view.CLI;
import com.hit.algorithm.LRUAlgoCacheImpl;
import com.hit.algorithm.MFUAlgoCacheImpl;
import com.hit.algorithm.MRUAlgoCacheImpl;
import com.hit.algorithm.SecondChanceAlgoCacheImpl;
import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.processes.ProcessCycles;
import com.hit.processes.RunConfiguration;
import com.hit.util.MMULogger;
import com.hit.processes.Process;

public class MMUDriver {

//	Throws:
//		java.lang.InterruptedException
//		java.lang.reflect.InvocationTargetException
	public static void main(String[] args) 
					 throws InterruptedException,
							InvocationTargetException {
		int capacity;
		IAlgoCache<Long, Long> cacheAlgo;
		MemoryManagementUnit mmu;
		RunConfiguration runConfiguration;
		List<Process> processesList;
		MMULogger logger = MMULogger.getInstance();
		CLI cli = new CLI(System.in, System.out);
		
		while(true) {
			String[] configuration = cli.getConfiguration();
			cli.write("Processing...");
			capacity = Integer.parseInt(configuration[1]);
			logger.write("RC:" + new Integer(capacity).toString() + System.lineSeparator(), Level.INFO);
			
			cacheAlgo = algorithmsFactory(configuration[0], capacity);
			mmu = new MemoryManagementUnit(capacity, cacheAlgo);
			
			runConfiguration = readConfigurationFile();
			
			processesList = processesCreator(runConfiguration, mmu);
			
			runProcesses(processesList);
			cli.write("Done.");
		}

	}
	
	private static void runProcesses(List<Process> processesList) {
		Executor executor = Executors.newCachedThreadPool();
				
		for(Process process: processesList) {
			executor.execute(new Thread(process));
		}
		
		((ExecutorService) executor).shutdown();
	}
	
	private static List<Process> processesCreator(RunConfiguration runConfiguration, MemoryManagementUnit mmu) {
		List<Process> processes = new ArrayList<Process>();
		int processId = 0;
		
		for(ProcessCycles pc: runConfiguration.getProcessesCycles()) {
			processes.add(new Process(processId, mmu, pc));
			processId++;
		}
		
		return processes;
	}

	private static RunConfiguration readConfigurationFile()
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
	
	private static IAlgoCache<Long, Long> algorithmsFactory(String algoName, int capacity) {
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
