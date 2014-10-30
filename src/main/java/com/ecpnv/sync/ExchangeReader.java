package com.ecpnv.sync;

import java.net.URI;
import java.net.URISyntaxException;

import microsoft.exchange.webservices.data.BasePropertySet;
import microsoft.exchange.webservices.data.Contact;
import microsoft.exchange.webservices.data.ContactSchema;
import microsoft.exchange.webservices.data.ContactsFolder;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.ExchangeVersion;
import microsoft.exchange.webservices.data.FindItemsResults;
import microsoft.exchange.webservices.data.FolderSchema;
import microsoft.exchange.webservices.data.Item;
import microsoft.exchange.webservices.data.ItemView;
import microsoft.exchange.webservices.data.PropertySet;
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
        
        try {
            service.setUrl(new URI(Constants.EWS_URI));
            PropertySet propertyset = new PropertySet(BasePropertySet.IdOnly, FolderSchema.TotalCount);
            ContactsFolder contactsfolder = ContactsFolder.bind(service, WellKnownFolderName.Contacts, propertyset);
            int numItems = contactsfolder.getTotalCount() < 50 ? contactsfolder.getTotalCount() : 50;
            
            ItemView view = new ItemView(numItems);
            view.setPropertySet(new PropertySet(BasePropertySet.IdOnly, ContactSchema.DisplayName));
            
            FindItemsResults<Item> contactItems = service.findItems(WellKnownFolderName.Contacts, view);
            service.loadPropertiesForItems(contactItems, PropertySet.FirstClassProperties);
            for (Item item : contactItems) {
                if (item.getItemClass().equals("IPM.Contact")) {
                    Contact contact = (Contact) item;
                    System.out.println(contact.getDisplayName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
