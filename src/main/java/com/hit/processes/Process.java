package com.hit.processes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.memoryunits.Page;
import com.hit.util.MMULogger;

public class Process implements Runnable{
	
	private int id;
	private MemoryManagementUnit mmu;
	private ProcessCycles processCycles;
	
	
//	This constructor represents a process constructor, which gets 3 configure parameters to simulate real process
//	Parameters:
//		id - of the process.
//		mmu - reference to the MMU object.
//		processCycles - process cycles configuration.
	public Process(int id, 
			MemoryManagementUnit mmu, 
			ProcessCycles processCycles) {
		this.setId(id);
		
		if(mmu != null) {
			this.mmu = mmu;
		}
		else {
			this.mmu = new MemoryManagementUnit(0, null);
		}
		
		this.processCycles = processCycles;
	}
	
//	Returns:
//		id the id of the process
	public int getId() {
		return id;
	}
	
//	Parameters:
//		id - the id of the process
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		
		int sleepMs;
		List<Page<byte[]>> pagesFromMemory = null;
		Iterator<Page<byte[]>> pagesIter;
		Iterator<byte[]> dataIter;
		MMULogger logger = MMULogger.getInstance();
		
		logger.write("PN:" + new Long(Thread.currentThread().getId()).toString() + 
					 System.lineSeparator() + 
					 System.lineSeparator(), Level.INFO);
		
		for(ProcessCycle pc : processCycles.getProcessCycles()) {
			
			boolean writePages[] = new boolean[pc.getData().size()];
			
			int index = 0;
			for (byte[] data : pc.getData()) 
			{
				if(data != null)
					writePages[index] = true;
				index++;;
			}
			
			sleepMs = pc.getSleepMs();
			synchronized(mmu) {
				try {
					pagesFromMemory = mmu.getPages(pc.getPages().toArray(new Long[1]), writePages);
				} catch (ClassNotFoundException | IOException e1) {
					logger.write(e1.getMessage(), Level.SEVERE);
				}
				pagesIter = pagesFromMemory.iterator();
				dataIter = pc.getData().iterator();
				Page<byte[]> page;
				byte[] data;
				while(pagesIter.hasNext() && dataIter.hasNext()) {
					page = pagesIter.next();
					data = dataIter.next();
					if(data == null) {
						logger.write("GP:P" + new Long(Thread.currentThread().getId()).toString() + 
									 " " + page.getPageId().toString() +
									 " []" + 
									 System.lineSeparator() +
									 System.lineSeparator(), Level.INFO);
						continue;
					}
					logger.write("GP:P" + new Long(Thread.currentThread().getId()).toString() + 
								 " " + page.getPageId().toString() +
								 " " + Arrays.toString(data) + 
								 System.lineSeparator() +
								 System.lineSeparator(), Level.INFO);
					page.setContent(data);
				}
			}
			try {
				Thread.sleep(sleepMs);
			} catch (InterruptedException e) {
				logger.write(e.getMessage(), Level.SEVERE);
			}
		}
	}

}
