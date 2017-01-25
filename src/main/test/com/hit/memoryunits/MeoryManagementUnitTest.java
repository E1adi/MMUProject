package com.hit.memoryunits;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import com.hit.algorithm.*;

public class MeoryManagementUnitTest {

	@Test
	public void checkMMU() {
		MemoryManagementUnit Driver = new MemoryManagementUnit(5, new LRUAlgoCacheImpl<Long, Long>(5));
		List<Page<byte[]>> pagesFromRam;
		try {
			pagesFromRam = Driver.getPages(new Long[]{(long)1, (long)2, (long)3} , new boolean[]{true, true, true});
			for(Integer i = 0; i<3; i++) {	
				Assert.assertEquals(pagesFromRam.get(i).getPageId(), new Long(i+1));
			}
			pagesFromRam = Driver.getPages(new Long[]{(long)2, (long)3, (long)4}, new boolean[]{true, true, true});
			for(Integer i = 0; i<3; i++) {	
				Assert.assertEquals(pagesFromRam.get(i).getPageId(), new Long(i+2));
			}
			pagesFromRam = Driver.getPages(new Long[]{(long)4, (long)5, (long)6}, new boolean[]{true, true, true});
			for(Integer i = 0; i<3; i++) {	
				Assert.assertEquals(pagesFromRam.get(i).getPageId(), new Long(i+4));
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
