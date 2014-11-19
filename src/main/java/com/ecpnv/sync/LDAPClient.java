package com.ecpnv.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapEntryAlreadyExistsException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

public class LDAPClient {

    private static final String BASE = "OU=Phonebook,DC=3cx,DC=ECP";
    private LdapConnection connection;

    public LDAPClient(String host) throws LdapException {
        connection = new LdapNetworkConnection(host, 389);
        connection.bind();
    }

    public List<Entry> searchAll() throws CursorException, LdapException {
        List<Entry> results = new ArrayList<Entry>();
        EntryCursor cursor = connection.search(BASE, "(objectClass=person)",
                SearchScope.SUBTREE);

        while (cursor.next()) {
            Entry entry = cursor.get();
            results.add(entry);
        }

        cursor.close();

        return results;

    }

    private String dnWithKey(String key) {
        return "cn=" + key + "," + BASE;
    }
    
    public void add(String name, Map<String, String> map, Map<String, Exception> invalidList, ArrayList<String> skippedList, int j) throws LdapException {
        String[] objectArray = new String[map.size() + 2];
        objectArray[0] = "ObjectClass: top";
        objectArray[1] = "ObjectClass: organizationalPerson";

        int i = 2;

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String addAttribute = entry.getKey() + ": " + entry.getValue();
            objectArray[i] = addAttribute;
            i++;
        }
        
//        System.out.println("Adding " + name);
        
        try {
            connection.add(new DefaultEntry(dnWithKey(name), (Object[])objectArray));
        } catch (LdapEntryAlreadyExistsException e) {
            skippedList.add(name);
            System.out.println(Integer.toString(j) + ": Skipping double entry " + name);
        } catch (LdapInvalidDnException e) {
            invalidList.put(name, e);
            System.out.println(Integer.toString(j) + ": Skipping entry " + name + ":");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("OOB exception caused by " + name);
            e.printStackTrace();
        }
    }

    public Entry search(String key) {
        // Create the SearchRequest object
        SearchRequest req = new SearchRequestImpl();
        req.setScope(SearchScope.SUBTREE);
        req.addAttributes("*");
        req.setTimeLimit(0);
        try {
            req.setBase(new Dn(BASE));
            req.setFilter(String.format("(cn=%s)", key));

            // Process the request
            SearchCursor searchCursor = connection.search(req);

            while (searchCursor.next())
            {
                Response response = searchCursor.get();

                // process the SearchResultEntry
                if (response instanceof SearchResultEntry)
                {
                    return ((SearchResultEntry) response).getEntry();
                }
            }
        } catch (LdapException e) {
            e.printStackTrace();
        } catch (CursorException e) {
            e.printStackTrace();
        }

        return null;

    }

    public void delete(String key) {
        try {
            connection.delete(dnWithKey(key));
        } catch (LdapException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteAll() {
        try {
            List<Entry> allEntries = searchAll();
            
            for (Entry entry : allEntries) {
                delete(entry.get("cn").getString());
            }
        } catch (CursorException e) {
            e.printStackTrace();
        } catch (LdapException e) {
            e.printStackTrace();
        }
    }
}
