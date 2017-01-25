package com.hit.processes;

import java.util.List;

public class RunConfiguration {

	private List<ProcessCycles> processCycles;
	
	public RunConfiguration(List<ProcessCycles> processCycles) {
		this.setProcessesCycles(processCycles);
	}
	
//	Returns:
//		the processesCycles
	public List<ProcessCycles> getProcessesCycles() {
		return processCycles;
	}
	
	
//	Parameters:
//		processesCycles - the processesCycles to set
	public void setProcessesCycles(List<ProcessCycles> processCycles) {
		this.processCycles = processCycles;
	}
	
	@Override
	public String toString()
	{
		return processCycles.toArray().toString();
	}
}
