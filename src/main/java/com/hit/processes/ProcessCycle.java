package com.hit.processes;

import java.text.MessageFormat;
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
		this.setPages(pages);
		this.setData(data);
		this.setSleepMs(sleepMs);
	}
	
//	Returns:
//		the pages.
	public List<Long> getPages() {
		return pages;
	}
	
//	Parameters:
//		pages - the pages to set.
	public void setPages(List<Long> pages) {
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
			this.data = data;
	}
	
//	Overriding Object toString.
	public String toString() {
		return MessageFormat.format("Requested pages: {0}, Sleep time: {1}, Data: {2}", pages, sleepMs, data);
	}
}
