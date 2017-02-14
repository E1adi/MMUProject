package com.hit.processes;

import java.util.List;

public class RunConfiguration {

	private List<ProcessCycles> processesCycles;
	
	public RunConfiguration(List<ProcessCycles> processCycles) {
		this.setProcessesCycles(processCycles);
	}
	
//	Returns:
//		the processesCycles
	public List<ProcessCycles> getProcessesCycles() {
		return processesCycles;
	}
	
	
//	Parameters:
//		processesCycles - the processesCycles to set
	public void setProcessesCycles(List<ProcessCycles> processesCycles) {
		this.processesCycles = processesCycles;
	}
	
	@Override
	public String toString()
	{
		return processesCycles.toArray().toString();
	}
}
