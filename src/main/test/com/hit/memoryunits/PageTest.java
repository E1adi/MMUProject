package com.hit.memoryunits;

import org.junit.Assert;
import org.junit.Test;



public class PageTest {

	@Test
	public void checkPage() {
		Page<String> page = new Page<String>((long)1, "Hello World!!!");
		
		page.setPageId((long)2);
		Assert.assertEquals(page.getPageId(), new Long(2));
		
		Assert.assertEquals(page.toString(), "Page ID: 2, Content: Hello World!!!");
		
		Assert.assertEquals(page.getContent(), "Hello World!!!");
		
		Assert.assertEquals(page.hashCode(), 34);
		
		page.setContent("ninininininini");
		Assert.assertEquals(page.getContent(), "ninininininini");
	}
}
