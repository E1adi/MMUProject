package com.hit.processes;

import java.util.List;

public class ProcessCycles {
	
	private List<ProcessCycle> processCycles;

//	This constructor represents a Processes Cycle constructor, which gets a list of Process Cycle and set it as his member.
//	Parameters:
//		processCycles - List of ProcessCycle to set as member.
	public ProcessCycles(List<ProcessCycle> processCycles) {
		this.setProcessCycles(processCycles);
	}
	
//	Returns:
//		ProcessCycle list.
	public List<ProcessCycle> getProcessCycles() {
		return processCycles;
	}
	
//	Parameters:
//		processCycles - ProcessCycle list to set.
	public void setProcessCycles(List<ProcessCycle> processCycles) {
		if(processCycles != null) {
			this.processCycles = processCycles;
		}
	}
	
//	Overriding Object toString.
	public String toString() {
		return processCycles.toString();
	}
}
