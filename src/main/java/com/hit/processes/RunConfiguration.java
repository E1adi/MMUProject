package com.hit.processes;

import java.util.List;

public class RunConfiguration {

	private List<ProcessCycles> processesCycles;
	
	public RunConfiguration(List<ProcessCycles> processesCycles) {
		this.setProcessesCycles(processesCycles);
	}
	
//	Returns:
//		the processesCycles
	public List<ProcessCycles> getProcessesCycles() {
		return processesCycles;
	}
	
	
//	Parameters:
//		processesCycles - the processesCycles to set
	public void setProcessesCycles(List<ProcessCycles> processesCycles) {
		if(processesCycles != null) {
			this.processesCycles = processesCycles;
		}
	}
}
