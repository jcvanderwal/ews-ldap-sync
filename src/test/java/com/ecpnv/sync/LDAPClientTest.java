package com.ecpnv.sync;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.apache.directory.api.ldap.model.entry.Entry;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPClientTest {

    private static final String SANDER_GINN = "Sander Ginn";
    private static final String MARC_ALVARES = "Marc Alvares";
    private LDAPClient client;

    @Before
    public void setUp() throws Exception {
        client = new LDAPClient("10.3.0.201");
    }

    @Test
    public void test0DeleteAll() throws Exception {
        client.deleteAll();
        assertThat(client.searchAll().size(), is(0));
    }
    
    @Test
    public void test1Add() throws Exception {
        Map<String, String> testMap = new HashMap<String, String>();
        testMap.put("mobile", "+31 655 12312");
        client.add(MARC_ALVARES, testMap);
    }
    
    @Test
    public void test2Add() throws Exception {
        Map<String, String> testMap = new HashMap<String, String>();
        testMap.put("mobile", "+31 625 259833");
        testMap.put("telephoneNumber", "+31 20 000000");
        client.add(SANDER_GINN, testMap);
    }

    @Test
    public void test3Count() throws Exception {
        List<Entry> search = client.searchAll();
        assertThat(search.toString(), search.size(), is(2));
    }

    @Test
    public void test4Search() throws Exception {
        assertNotNull(client.search(MARC_ALVARES));
    }

    @Test
    public void test5Delete() throws Exception {
        //when
        client.delete(MARC_ALVARES);
        //then
        assertNull(client.search(MARC_ALVARES));
    }
}
