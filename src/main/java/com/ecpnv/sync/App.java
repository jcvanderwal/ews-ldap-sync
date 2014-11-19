package com.ecpnv.sync;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.google.i18n.phonenumbers.PhoneNumberUtil;





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

        /*
         * Uncomment when phone validation is implemented
         *
         * PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
         */
        ExchangeReader reader = new ExchangeReader();
        List<Contact> readContacts = reader.readContacts("Group Contacts");

        LDAPClient client = new LDAPClient("10.3.0.201");
        ArrayList<String> skippedList = new ArrayList<String>();
        Map<String, Exception> invalidList = new HashMap<String, Exception>();
        
        /*
         * For now, every call to the app removes all entries on the LDAP server
         * and then imports all entries from Exchange again in order to keep
         * everything up to date
         */
        
        System.out.println("Deleting");
        client.deleteAll();
        System.out.println("Deleted");
        int i = 0;
        
        for (Contact contact : readContacts) {
            try {
                String displayName = contact.getDisplayName();
                Map<String, String> assetMap = createAssetMap(contact);
                
                if (!assetMap.isEmpty()) {
                    client.add(displayName, assetMap, invalidList, skippedList, i);
                }
                i++;
            } catch (ServiceLocalException e) {
                e.printStackTrace();
            }

        }
        
        System.out.println(skippedList.toString());
        System.out.println(invalidList.toString());
        
        System.exit(0);
    }

    /* Creates a map containing all the fields that need to be transferred to the LDAP server */
    public static Map<String, String> createAssetMap(Contact contact) {
        Map<String, String> map = new HashMap<String, String>();
        
        try {
            PhoneNumberDictionary phoneDictionary = contact.getPhoneNumbers();

            for (PhoneNumberKey key : PhoneNumberKey.values()) {
                if (phoneDictionary.getPhoneNumber(key) != null &&
                    (!key.equals(PhoneNumberKey.BusinessFax) ||
                    !key.equals(PhoneNumberKey.HomeFax) ||
                    !key.equals(PhoneNumberKey.OtherFax))) {
                    if (EXCHANGE_TO_LDAP.containsKey(key.toString())) {
                        String ldap_key = EXCHANGE_TO_LDAP.get(key.toString());
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
