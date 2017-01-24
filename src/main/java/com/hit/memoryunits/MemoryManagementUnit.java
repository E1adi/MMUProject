package com.hit.memoryunits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.hit.algorithm.IAlgoCache;

public class MemoryManagementUnit {
	
	private RAM _RAM = null;
	private IAlgoCache<java.lang.Long, java.lang.Long> _algo;
	
	public MemoryManagementUnit(int ramCapacity,
            com.hit.algorithm.IAlgoCache<java.lang.Long, java.lang.Long> algo) {
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
	@SuppressWarnings("unchecked")
	public Page<byte[]>[] getPages(java.lang.Long[] pageIds)
            throws java.io.IOException, ClassNotFoundException {
		Long currentPresentPageID, currentRequestedPageID;
		Long currentMissingPageID, currentKeyToReplace; 
		Page<byte[]> pageToRam;
		HardDisk hd = HardDisk.getInstance();
		List<Long> requestedPages = Arrays.asList(pageIds);
		List<Long> presentPagesInMemory, keysToReplace = null;
		List<Long> missingPagesInMemory = new ArrayList<Long>();	
		List<Page<byte[]>> pagesToReturn = new ArrayList<Page<byte[]>>();
		
		presentPagesInMemory = _algo.getElement(Arrays.asList(pageIds));
		if((presentPagesInMemory != null)) {
			Iterator<Long> presentPagesIterator = presentPagesInMemory.iterator();
			Iterator<Long> requestedPagesIterator = requestedPages.iterator();
			while(presentPagesIterator.hasNext() && requestedPagesIterator.hasNext()) {
				currentPresentPageID = presentPagesIterator.next();
				currentRequestedPageID = requestedPagesIterator.next();
				if(currentPresentPageID == null) {
					missingPagesInMemory.add(currentRequestedPageID);
				}
				else {
					pagesToReturn.add(_RAM.getPage(currentPresentPageID));
				}
			}
		}
		else {
			missingPagesInMemory.addAll(Arrays.asList(pageIds));
		}
		if(missingPagesInMemory.size() > 0) {
			keysToReplace = _algo.putElement(missingPagesInMemory, missingPagesInMemory);
			if(keysToReplace != null) {
				Iterator<Long> missingPagesIterator = missingPagesInMemory.iterator();
				Iterator<Long> keysToReplaceIterator = keysToReplace.iterator();
				while(missingPagesIterator.hasNext() && keysToReplaceIterator.hasNext()) {
					currentMissingPageID = missingPagesIterator.next();
					currentKeyToReplace = keysToReplaceIterator.next();
					if(currentKeyToReplace != null) {
						pageToRam = hd.pageReplacement(_RAM.getPage(currentKeyToReplace), currentMissingPageID);
						_RAM.removePage(_RAM.getPage(currentKeyToReplace));
						_RAM.addPage(pageToRam);
						pagesToReturn.add(pageToRam);
					}
					else {
						pageToRam = hd.pageFault(currentMissingPageID);
						_RAM.addPage(pageToRam);
						pagesToReturn.add(pageToRam);
					}
				}
			}
			else {
				for(Long pageId: missingPagesInMemory) {
					pageToRam = hd.pageFault(pageId);
					_RAM.addPage(pageToRam);
					pagesToReturn.add(pageToRam);
				}
			}
		} 
		return (Page<byte[]>[]) pagesToReturn.toArray(new Page[1]);
	}
}
