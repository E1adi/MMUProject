package com.hit.memoryunits;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import org.junit.Assert;
import org.junit.Test;

public class RAMTest {

	@SuppressWarnings("unchecked")
	@Test
	public void checkRam() throws UnsupportedEncodingException {
		RAM ram = new RAM(5);
		
		Map<Long, Page<byte[]>> memory = new HashMap<Long, Page<byte[]>>(5);
		memory.put((long)1, new Page<byte[]>((long)1, "Elad".getBytes()));
		memory.put((long)2, new Page<byte[]>((long)2, "Mor".getBytes()));
		memory.put((long)3, new Page<byte[]>((long)3, "Shlomi".getBytes()));
		memory.put((long)4, new Page<byte[]>((long)4, "Noy".getBytes()));
		
		ram.setPages(memory);
		Assert.assertEquals(Arrays.toString(ram.getPage((long)1).getContent()), Arrays.toString("Elad".getBytes()));
		Assert.assertEquals(Arrays.toString(ram.getPage((long)2).getContent()), Arrays.toString("Mor".getBytes()));
		Assert.assertEquals(Arrays.toString(ram.getPage((long)3).getContent()), Arrays.toString("Shlomi".getBytes()));
		Assert.assertEquals(Arrays.toString(ram.getPage((long)4).getContent()), Arrays.toString("Noy".getBytes()));
		
		
		ram.removePages(new Page[]{ram.getPage((long)1), ram.getPage((long)2), ram.getPage((long)3), ram.getPage((long)4)});
		Assert.assertEquals(ram.getCurrentSize(), 0);
		Assert.assertEquals(ram.getInitialCapacity(), 5);
		
		
	}
}
