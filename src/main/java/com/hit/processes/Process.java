package com.hit.processes;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.memoryunits.Page;

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
					e1.printStackTrace();
				}
				pagesIter = pagesFromMemory.iterator();
				dataIter = pc.getData().iterator();
				Page<byte[]> page;
				byte[] data;
				while(pagesIter.hasNext() && dataIter.hasNext()) {
					page = pagesIter.next();
					data = dataIter.next();
					if(data == null)
						continue;
					page.setContent(data);
				}
			}
			try {
				Thread.sleep(sleepMs);
			} catch (InterruptedException e) {}
		}
	}

}
