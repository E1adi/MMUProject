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
	Process(int id, 
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
			sleepMs = pc.getSleepMs();
			try {
				pagesFromMemory = mmu.getPages((Long[])pc.getPages().toArray());
			} catch (ClassNotFoundException | IOException e1) {
				
			}
			pagesIter = pagesFromMemory.iterator();
			dataIter = pc.getData().iterator();
			Page<byte[]> page;
			byte[] data;
			while(pagesIter.hasNext() && dataIter.hasNext()) {
				page = pagesIter.next();
				data = dataIter.next();
				page.setContent(data);
			}
			try {
				Thread.sleep(sleepMs);
			} catch (InterruptedException e) {}
		}
		
		
	}

}
