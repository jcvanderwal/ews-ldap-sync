package com.ecpnv.sync;

import java.util.List;

import microsoft.exchange.webservices.data.Contact;
import microsoft.exchange.webservices.data.PhoneNumberKey;
import microsoft.exchange.webservices.data.ServiceLocalException;

public class App {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        ExchangeReader reader = new ExchangeReader();
        List<Contact> readContacts = reader.readContacts("Group Contacts");

        for (Contact contact : readContacts) {
            try {
                String displayName = contact.getDisplayName();
                String phoneNumber = contact.getPhoneNumbers().getPhoneNumber(PhoneNumberKey.BusinessPhone);

                System.out.println(displayName + " - " + phoneNumber);
            } catch (ServiceLocalException e) {
                e.printStackTrace();
            }

        }

    }

}
