package com.ecpnv.sync;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import microsoft.exchange.webservices.data.BasePropertySet;
import microsoft.exchange.webservices.data.Contact;
import microsoft.exchange.webservices.data.ContactSchema;
import microsoft.exchange.webservices.data.ContactsFolder;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.ExchangeVersion;
import microsoft.exchange.webservices.data.FindFoldersResults;
import microsoft.exchange.webservices.data.FindItemsResults;
import microsoft.exchange.webservices.data.Folder;
import microsoft.exchange.webservices.data.FolderSchema;
import microsoft.exchange.webservices.data.FolderView;
import microsoft.exchange.webservices.data.Item;
import microsoft.exchange.webservices.data.ItemView;
import microsoft.exchange.webservices.data.PhoneNumberDictionary;
import microsoft.exchange.webservices.data.PhoneNumberEntry;
import microsoft.exchange.webservices.data.PhoneNumberKey;
import microsoft.exchange.webservices.data.PropertySet;
import microsoft.exchange.webservices.data.ServiceLocalException;
import microsoft.exchange.webservices.data.WebCredentials;
import microsoft.exchange.webservices.data.WellKnownFolderName;


/**
 * Hello world!
 *
 */
public class ExchangeReader 
{
    public static void main( String[] args )
    {
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentials = new WebCredentials(Constants.USERNAME, Constants.PASSWORD);
        service.setCredentials(credentials);
        service.setTraceEnabled(true);
        ContactsFolder groupContacts = null;
        FindItemsResults<Item> items = null;
        
        try {
            service.setUrl(new URI(Constants.EWS_URI));
            
            PropertySet propertyset = new PropertySet(BasePropertySet.IdOnly, FolderSchema.TotalCount);
            Folder publicFoldersRoot = Folder.bind(service, WellKnownFolderName.PublicFoldersRoot, propertyset);

            /* FolderView requires a limit of folders found. Arbitrarily set to 1000 here */
            FolderView view = new FolderView(10000);
            view.setPropertySet(new PropertySet(BasePropertySet.IdOnly, FolderSchema.DisplayName));
            ArrayList<Folder> folders = publicFoldersRoot.findFolders(view).getFolders();
            for (Folder folder : folders) {
                if (folder.getDisplayName().equals("Group Contacts")) {
                    items = folder.findItems(new ItemView(10000));
                    service.loadPropertiesForItems(items, PropertySet.FirstClassProperties);
                    System.out.println(items.getTotalCount());
                    groupContacts = (ContactsFolder)folder;
                    break;
                }
            }
            
            if (groupContacts != null && items != null) {
                int i = 0;
                for (Item item : items) {
                    Contact contact = (Contact)item;
                    try {
                        String displayName = contact.getDisplayName();
                        String phoneNumber = contact.getPhoneNumbers().getPhoneNumber(PhoneNumberKey.BusinessPhone);
                        
                        System.out.println(Integer.toString(i) + ' ' + displayName + ' ' + phoneNumber);
                    } catch (ServiceLocalException e) {
                        e.printStackTrace();
                    }
                    
                    i++;
                }
            } else {
                System.out.println("Group contacts folder not found");
            }
            
            
//            int numItems = contactsfolder.getTotalCount() < 50 ? contactsfolder.getTotalCount() : 50;
//            
//            ItemView view = new ItemView(numItems);
//            view.setPropertySet(new PropertySet(BasePropertySet.IdOnly, ContactSchema.DisplayName));
//            
//            FindItemsResults<Item> contactItems = service.findItems(WellKnownFolderName.Contacts, view);
//            service.loadPropertiesForItems(contactItems, PropertySet.FirstClassProperties);
//            for (Item item : contactItems) {
//                if (item.getItemClass().equals("IPM.Contact")) {
//                    Contact contact = (Contact) item;
//                    System.out.println(contact.getDisplayName());
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
