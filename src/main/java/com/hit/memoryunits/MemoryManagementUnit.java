package com.hit.memoryunits;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.hit.algorithm.IAlgoCache;

public class MemoryManagementUnit {
	
	private RAM _RAM = null;
	private IAlgoCache<java.lang.Long, java.lang.Long> _algo;
	
	public MemoryManagementUnit(int ramCapacity,
            					IAlgoCache<Long, Long> algo) {
		if(ramCapacity > 0) {
			_RAM = new RAM(ramCapacity);
		}
		else {
			_RAM = new RAM(2048);
		}
		if(algo != null) {
			_algo = algo;
		}
		else {
			_algo = new com.hit.algorithm.SecondChanceAlgoCacheImpl<Long, Long>(_RAM.getInitialCapacity());
		}
	}
	
//	This method is the main method which returns array of pages that are requested from the user
//	Parameters:
//		pageIds - array of page id's
//	Returns:
//		returns array of pages that are requested from the user
//	Throws:
//		java.io.IOException
//		ClassNotFoundException
	public List<Page<byte[]>> getPages(Long[] pageIds, boolean[] writePages)
            throws java.io.IOException, ClassNotFoundException {
		List<Long> requestedPages = Arrays.asList(pageIds); 
		Iterator<Long> requestedPagesIter = requestedPages.iterator();
		List<Long> presentPagesInMemory;
		Iterator<Long> presentPagesInMemoryIter;
		List<Long> pagesForWritingThatPresentInRam = new LinkedList<Long>();
		List<Long> pagesForWritingThatMissingInRam = new LinkedList<Long>();
		List<Long> keysToReplace;
		List<Page<byte[]>> returnedPages = new LinkedList<Page<byte[]>>();		
		HardDisk hd = HardDisk.getInstance();
		
		presentPagesInMemory = _algo.getElement(requestedPages);				// Check which pages are already in RAM
		presentPagesInMemoryIter = presentPagesInMemory.iterator();
		
		int readWriteIndex = 0;
		while(requestedPagesIter.hasNext() && presentPagesInMemoryIter.hasNext()) {
			Long curRequestedPage = requestedPagesIter.next();
			Long curPresentPage = presentPagesInMemoryIter.next();
			
			if(!writePages[readWriteIndex]) {       							// If this page is for reading purpose.
				if(curPresentPage != null) {										// If this page is already in ram.
					returnedPages.add(_RAM.getPage(curRequestedPage));					// Get the page from RAM and add it to return list.
				}
				else {																// This page is not in ram.
					returnedPages.add(hd.pageFault(curRequestedPage));					// Get the page from HD and add it to return list.
				}
			}
			else {																// If this page is for writing purpose.
				if(curPresentPage != null) {										// Adding the page to a suitable list depending if it is present in RAM or NOT. 
					pagesForWritingThatPresentInRam.add(curRequestedPage);				
				}
				else {
					pagesForWritingThatMissingInRam.add(curRequestedPage);
				}
			}
		}
		
		if(pagesForWritingThatMissingInRam.size() > 0) {															// If there are Pages for writing that are missing in RAM
			keysToReplace = _algo.putElement(pagesForWritingThatMissingInRam, pagesForWritingThatMissingInRam);			// Consult with algorithms which pages to replace
			Iterator<Long> pagesForWritingThatMissingInRamIter = pagesForWritingThatMissingInRam.iterator();
			Iterator<Long> keysToReplaceIterator = keysToReplace.iterator();
			while(pagesForWritingThatMissingInRamIter.hasNext() && keysToReplaceIterator.hasNext()) {					// Iterating over all pages that are missing in RAM and are for writing purpose.
				Long currentMissingPageID = pagesForWritingThatMissingInRamIter.next();
				Long currentKeyToReplace = keysToReplaceIterator.next();
				Page<byte[] >pageToRam;
				
				if(currentKeyToReplace != null) {																			// If algorithms decides that a page should be replaced 
					pageToRam = hd.pageReplacement(_RAM.getPage(currentKeyToReplace), currentMissingPageID);					// Replace that page with the missing requested page from hard drive.
					_RAM.removePage(_RAM.getPage(currentKeyToReplace));
					_RAM.addPage(pageToRam);
				}
				else {																										// If algorithm decides that no page should be replaces. 
					pageToRam = hd.pageFault(currentMissingPageID);																// Push to RAM the requested page straight from hard drive.
					_RAM.addPage(pageToRam);
				}
			}
		}

		
		for(Long pageId : pagesForWritingThatPresentInRam) {														// Adds all pages for writing purpose which were already in RAM.
			returnedPages.add(_RAM.getPage(pageId));
		}
		for(Long pageId : pagesForWritingThatMissingInRam) {														// Adds all pages for writing purpose which were missing in RAM
			returnedPages.add(_RAM.getPage(pageId));
		}
		
		return returnedPages;	// return all found pages.
	}
}
