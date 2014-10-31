package com.ecpnv.sync;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import junit.framework.TestCase;

import org.junit.Test;

public class DirectorySampleTest extends TestCase {

	@Test
	public void test() {
		DirectorySample test = new DirectorySample();
		test.doLookup();

	}

}
