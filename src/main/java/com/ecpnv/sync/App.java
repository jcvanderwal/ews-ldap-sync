package com.ecpnv.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import microsoft.exchange.webservices.data.Contact;
import microsoft.exchange.webservices.data.PhoneNumberDictionary;
import microsoft.exchange.webservices.data.PhoneNumberKey;
import microsoft.exchange.webservices.data.ServiceLocalException;

import org.apache.directory.api.ldap.model.exception.LdapException;

public class App {
    public static final Map<String, String> EXCHANGE_TO_LDAP;
    static {
        EXCHANGE_TO_LDAP = new HashMap<String, String>();
        EXCHANGE_TO_LDAP.put("HomePhone", "homePhone");
        EXCHANGE_TO_LDAP.put("MobilePhone", "mobile");
        EXCHANGE_TO_LDAP.put("BusinessPhone2", "otherTelephone");
        EXCHANGE_TO_LDAP.put("BusinessPhone", "telephoneNumber");
    }
    
    public static void main(String[] args) throws LdapException {

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        ExchangeReader reader = new ExchangeReader();
        List<Contact> readContacts = reader.readContacts("Group Contacts");
        LDAPClient client = new LDAPClient("10.3.0.201");

        /*
         * For now, every call to the app removes all entries on the LDAP server
         * and then imports all entries from Exchange again in order to keep
         * everything up to date
         */
        client.deleteAll();

        Map<String, Integer> keyMap = new HashMap<String, Integer>(); 
        for (Contact contact : readContacts) {
            try {
                String displayName = contact.getDisplayName();
                Map<String, String> phoneMap = createPhoneMap(contact);
//                System.out.println(displayName);
//                System.out.println(phoneMap.toString());

                for (String key : phoneMap.keySet()) {
                    if (keyMap.get(key) == null) {
                        keyMap.put(key, 1);
                    } else {
                        int newVal = keyMap.get(key) + 1;
                        keyMap.put(key, newVal);
                    }
                }

                client.add(displayName, phoneMap);

            } catch (ServiceLocalException e) {
                e.printStackTrace();
            }

        }
    }

    public static Map<String, String> createPhoneMap(Contact contact) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            PhoneNumberDictionary phoneDictionary = contact.getPhoneNumbers();

            for (PhoneNumberKey key : PhoneNumberKey.values()) {
                if (phoneDictionary.getPhoneNumber(key) != null &&
                    (key != PhoneNumberKey.BusinessFax ||
                    key != PhoneNumberKey.HomeFax ||
                    key != PhoneNumberKey.OtherFax)) {
                    if (EXCHANGE_TO_LDAP.containsKey(key)) {
                        String ldap_key = EXCHANGE_TO_LDAP.get(key);
                        map.put(ldap_key, phoneDictionary.getPhoneNumber(key).toString());
                    }
                }
            }
        } catch (ServiceLocalException e) {
            e.printStackTrace();
        }

        return map;
    }
}
