package com.ecpnv.sync;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class LDAPClientTest {

	@Test
	public void test() throws Exception {

		LDAPClient client = new LDAPClient("10.3.0.201");
		List<String> search = client.search(null);
		assertThat(search.toString(), search.size(), is(2));

	}

	@Test
	public void testAdd() throws Exception {
		LDAPClient client = new LDAPClient("10.3.0.201");
		client.add("Marc Alvares", "+31 655 12312");
	}

}
