package com.hit.processes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessCycle {
	
	private List<Long> pages;
	private int sleepMs;
	private List<byte[]> data;
	
//	This constructor represents a Process Cycle constructor, which gets the relevant configuration to the process life cycle
//	Parameters:
//		pages - to read/write to them
//		sleepMs - MS to sleep
//		data - data to write
	public ProcessCycle(List<Long> pages,
            			int sleepMs,
            			List<byte[]> data) {
		if(pages != null)
			this.pages = pages;
		else
			this.pages = new ArrayList<Long>();
		
		this.sleepMs = sleepMs;
		
		if(data != null)
			this.data = data;
		else
			this.data = new ArrayList<byte[]>();
	}
	
//	Returns:
//		the pages.
	public List<Long> getPages() {
		return pages;
	}
	
//	Parameters:
//		pages - the pages to set.
	public void setPages(List<Long> pages) {
		if (pages != null)
			this.pages = pages;
	}
	
//	Returns:
//		sleep time for process.
	public int getSleepMs() {
		return sleepMs;
	}
//	Parameters:
//		sleepMS - sleep time in milliseconds.
	public void setSleepMs(int sleepMs) {
		if(sleepMs >= 0)
			this.sleepMs = sleepMs;
	}
	
//	Returns:
//		the data.
	public List<byte[]> getData() {
		return data;
	}
	
//	Parameters:
//		data to be set.
	public void setData(java.util.List<byte[]> data) {
		if(data != null)
			this.data = data;
	}
	
//	Overriding Object toString.
	public String toString() {
		return String.format("Requested pages: %s, Sleep time: %d, Data: %s", pages, sleepMs, data);
	}
}
