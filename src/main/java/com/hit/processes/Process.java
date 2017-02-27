package com.hit.processes;

import java.io.IOException;
import java.text.MessageFormat;
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
	private MMULogger logger = MMULogger.getInstance();
	
//	This constructor represents a process constructor, which gets 3 configure parameters to simulate real process
//	Parameters:
//		id - of the process.
//		mmu - reference to the MMU object.
//		processCycles - process cycles configuration.
	public Process(int id, 
				   MemoryManagementUnit mmu, 
				   ProcessCycles processCycles) {
		
		this.setId(id);
		this.mmu = mmu;
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
		
		List<Page<byte[]>> pagesFromMemory = null;
		Iterator<Page<byte[]>> pagesIter;
		Iterator<byte[]> dataIter;
		
		for(ProcessCycle pc : processCycles.getProcessCycles()) {
			
			boolean writePages[] = new boolean[pc.getData().size()];
			
			int index = 0;
			for (byte[] data : pc.getData()) 
			{
				if(data != null)
					writePages[index] = true;
				index++;;
			}
			
			
			synchronized(mmu) {   // Locking MMU
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
					if(page != null) {            // Check if algorithm extracts a page that was just requested to be inserted
						
						logger.write(MessageFormat.format("GP:P{0} {1} {2}{3}{3}", getId(), 
																				   page.getPageId().toString(), 
																				   Arrays.toString(data), 
																				   System.lineSeparator()), Level.INFO);
						if(data.length == 0) {				
							page.setContent(data);
						}
					}
					else {
						logger.write(MessageFormat.format("Algorithm bug in process #{0} which asked MMU for pages {1},{2}" + 
														  "while trying to get a page which is not in ram, algorithm decides to page out a page that was also requested by the process in the same getPages call.{2}", id, 
														  																																							   pc.getPages(),
																																																					   System.lineSeparator()), Level.SEVERE);
					}
				}
			}
			try {
				Thread.sleep(pc.getSleepMs());
			} catch (InterruptedException e) {
				logger.write(e.getMessage(), Level.SEVERE);
			}
		}
	}

}
