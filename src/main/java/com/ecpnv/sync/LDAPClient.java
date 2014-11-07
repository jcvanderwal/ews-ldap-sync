package com.ecpnv.sync;

import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

public class LDAPClient {

    private static final String BASE = "OU=Phonebook,DC=3cx,DC=ECP";
    private LdapConnection connection;

    public LDAPClient(String host) throws LdapException {
        connection = new LdapNetworkConnection(host, 389);
        connection.bind();
    }

    public List<String> search(String x) throws CursorException, LdapException {

        List<String> results = new ArrayList<String>();

        EntryCursor cursor = connection.search(BASE, "(objectClass=person)",
                SearchScope.SUBTREE);

        while (cursor.next()) {
            Entry entry = cursor.get();
            System.out.println(entry);
            results.add(entry.toString());
        }

        cursor.close();

        return results;

    }

    public void add(String name, String phoneNumber) throws LdapException {
        connection.add(new DefaultEntry(
                "cn=" + name + "," + BASE,
                "ObjectClass: top",
                "ObjectClass: person",
                "cn: " + name,
                "telephoneNumber: " + phoneNumber
                ));
    }

}
