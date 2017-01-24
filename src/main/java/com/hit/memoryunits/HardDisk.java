package com.hit.memoryunits;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class HardDisk {
	
	private static HardDisk _instance = null;
	private static Object _locker = new Object();
	private int _SIZE;
	private String DEFAULT_FILE_NAME;
	Map<java.lang.Long, Page<byte[]>> _hardDiskContent;
	
	@SuppressWarnings("unchecked")
	private HardDisk() throws FileNotFoundException, IOException, ClassNotFoundException {
		_SIZE = Constants.HardDiskSize;
		DEFAULT_FILE_NAME = Constants.HardDiskDefaultFileName;
		File hdFile = new File(DEFAULT_FILE_NAME);
		if(hdFile.exists()) {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(DEFAULT_FILE_NAME));
			try {
				_hardDiskContent = (HashMap<Long, Page<byte[]>>)inputStream.readObject();
			} 
			catch (ClassNotFoundException CNF) {
				throw CNF;
			}
			finally {
				inputStream.close();
			}
		}
		else {
			hdFile.createNewFile();
			_hardDiskContent = new HashMap<Long, Page<byte[]>>(_SIZE);
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(DEFAULT_FILE_NAME));
			outputStream.writeObject(_hardDiskContent);
			outputStream.flush();
			outputStream.close();
		}
	}
	
	public static HardDisk getInstance() throws ClassNotFoundException, FileNotFoundException, IOException {
		if(_instance == null) {
			synchronized(_locker) {
				if(_instance == null) {
					_instance = new HardDisk();
				}
			}
		}
		return _instance;
	}
	
//	This method is called when a page is not in fast memory (RAM)
//	Parameters:
//	pageId - given pageId
//	Returns:
//	the page with the given pageId
//	Throws:
//	java.io.FileNotFoundException
//	java.io.IOException
	@SuppressWarnings("unchecked")
	public Page<byte[]> pageFault(java.lang.Long pageId) throws java.io.FileNotFoundException,
                   	   											java.io.IOException, 
                   	   											ClassNotFoundException {
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(DEFAULT_FILE_NAME));
		try {
			_hardDiskContent = (HashMap<java.lang.Long, Page<byte[]>>)inputStream.readObject();
			return _hardDiskContent.get(pageId);
		} catch (ClassNotFoundException CNF) {
			throw CNF;
		}
		finally {
			inputStream.close();
		}
	}
	
//	This method is called when a page is not in fast memory (RAM) and RAM is also with full capacity
//	Parameters:
//	moveToHdPage - page which should be moved to HD
//	moveToRamId - page id of the pages which should be moved to RAM
//	Returns:
//	the page with the given pageId
//	Throws:
//	java.io.FileNotFoundException
//	java.io.IOException
	@SuppressWarnings("unchecked")
	public Page<byte[]> pageReplacement(Page<byte[]> moveToHdPage,
            							java.lang.Long moveToRamId)	throws java.io.FileNotFoundException,
            															   java.io.IOException, ClassNotFoundException {
		
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(DEFAULT_FILE_NAME));
		ObjectOutputStream outputStream = null;
		
		try {
			_hardDiskContent.putAll((Map<java.lang.Long, Page<byte[]>>)inputStream.readObject()); 
			inputStream.close();
			_hardDiskContent.put(moveToHdPage.getPageId(), moveToHdPage);
			outputStream = new ObjectOutputStream(new FileOutputStream(DEFAULT_FILE_NAME));
			outputStream.writeObject(_hardDiskContent);
			outputStream.flush();
		} 
		catch (ClassNotFoundException CNF) {
			throw CNF;
		}
		finally {
			inputStream.close();
			if(outputStream != null)
				outputStream.close();
		}
		return _hardDiskContent.get(moveToRamId);
	}
}
