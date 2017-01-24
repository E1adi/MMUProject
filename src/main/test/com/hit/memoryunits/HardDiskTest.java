package com.hit.memoryunits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;

public class HardDiskTest {
	@Test
	public void checkHD() {
		try {
			List<String> pagesContent = new ArrayList<String>();
			pagesContent.add("Elad");
			pagesContent.add("Mor");
			pagesContent.add("Shlomi");
			pagesContent.add("Orel");
			pagesContent.add("Ben");
			pagesContent.add("Nissim");
			
			HardDisk HD = HardDisk.getInstance();
			
			for(int i=0; i<pagesContent.size(); i++) {
				HD.pageReplacement(new Page<byte[]>((long)i+1, pagesContent.get(i).getBytes("UTF-8")), (long)i);
			}

			for(int i=0; i<pagesContent.size(); i++) {
				Assert.assertEquals(new String(HD.pageFault((long)i+1).getContent(), "UTF-8"), pagesContent.get(i));
			}
	
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
