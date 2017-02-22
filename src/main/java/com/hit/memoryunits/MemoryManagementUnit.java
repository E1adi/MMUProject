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
		
		System.out.print("-");
		
		int readWriteIndex = 0;
		Long currentMissingPageID, currentKeyToReplace; 
		Page<byte[]> pageToRam;
		HardDisk hd = HardDisk.getInstance();
		List<Long> requestedPages = Arrays.asList(pageIds);
		List<Long> presentPagesInMemory, keysToReplace = null, pagesToCheckBeforReturning = new LinkedList<Long>(); 
		List<Long> missingPagesInMemoryForWrite = new LinkedList<Long>();
		List<Long> missingPagesInMemoryForRead = new LinkedList<Long>();
		List<Page<byte[]>> pagesToReturn = new LinkedList<Page<byte[]>>();
		
		for(Long pageId: requestedPages) {
			if(writePages[readWriteIndex])
				pagesToCheckBeforReturning.add(pageId);
			readWriteIndex++;
		}
		readWriteIndex = 0;
		
		presentPagesInMemory = _algo.getElement(requestedPages);
		Iterator<Long> presentPagesIterator = presentPagesInMemory.iterator();
		Iterator<Long> requestedPagesIterator = requestedPages.iterator();
		Long currentRequestedPageID, currentPresentPageID;
		while(presentPagesIterator.hasNext() && requestedPagesIterator.hasNext()) {
			currentPresentPageID = presentPagesIterator.next();
			currentRequestedPageID = requestedPagesIterator.next();
			if(currentPresentPageID == null) {											// If page is not in memory
				if(writePages[readWriteIndex]) {										// If that page is for writing
					missingPagesInMemoryForWrite.add(currentRequestedPageID);			// Add it to missingPagesForWrite list.			
				}																		//
				else {																	// Otherwise
					missingPagesInMemoryForRead.add(currentRequestedPageID);			// Add it to missingPagesForRead list.
				}
			}
			else if (!writePages[readWriteIndex]){										// If this page is already in memory,
				pagesToReturn.add(_RAM.getPage(currentRequestedPageID));				// Push it to result list as it don't need to be in ram, it is for read only.
			}
			readWriteIndex++;
		}
		
		
		if(missingPagesInMemoryForRead.size() > 0) {									// Inserting pages for read only directly from HD.
			for(Long pageId: missingPagesInMemoryForRead)
				pagesToReturn.add(hd.pageFault(pageId));
		}
		
		
		if(missingPagesInMemoryForWrite.size() > 0) {														// If there are Pages for writing that missing in memory
			keysToReplace = _algo.putElement(missingPagesInMemoryForWrite, missingPagesInMemoryForWrite);	// Consult with algo which page to replace
			Iterator<Long> missingPagesIterator = missingPagesInMemoryForWrite.iterator();
			Iterator<Long> keysToReplaceIterator = keysToReplace.iterator();
			while(missingPagesIterator.hasNext() && keysToReplaceIterator.hasNext()) {						// Iterating over missing pages for write purpose.
				currentMissingPageID = missingPagesIterator.next();
				currentKeyToReplace = keysToReplaceIterator.next();
				if(currentKeyToReplace != null) {															// If algo says that a page should be replaced 
					pageToRam = hd.pageReplacement(_RAM.getPage(currentKeyToReplace), currentMissingPageID);// Replace that page and insert new page to RAM.
					_RAM.removePage(_RAM.getPage(currentKeyToReplace));
					_RAM.addPage(pageToRam);
				}
				else {																						// If algo says that no page should be replaces, just push new page to RAM.
					pageToRam = hd.pageFault(currentMissingPageID);
					_RAM.addPage(pageToRam);
				}
			}
		}
		
		for(Long pageId : _algo.getElement(pagesToCheckBeforReturning)) {									// Checking if all pages with writing purpose are in RAM. 
			if(pageId == null) {																			// If not we choose a bad algorithm.
			// TODO	System.out.println("Bad Algorithm");
			}
		}
		
		for(Long pageId : pagesToCheckBeforReturning) {														// Adds all pages for writing purpose to return list.
			pagesToReturn.add(_RAM.getPage(pageId));
		}
		
		return pagesToReturn;	// return all found pages.
	}
}
