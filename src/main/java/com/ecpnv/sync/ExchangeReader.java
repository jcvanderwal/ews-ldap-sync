package com.ecpnv.sync;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import microsoft.exchange.webservices.data.BasePropertySet;
import microsoft.exchange.webservices.data.Contact;
import microsoft.exchange.webservices.data.ContactsFolder;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.ExchangeVersion;
import microsoft.exchange.webservices.data.FindItemsResults;
import microsoft.exchange.webservices.data.Folder;
import microsoft.exchange.webservices.data.FolderSchema;
import microsoft.exchange.webservices.data.FolderView;
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
    public List<Contact> readContacts(String folderName)
    {
        List<Contact> results = new ArrayList<Contact>();

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

            /*
             * FolderView requires a limit of folders found. Arbitrarily set to
             * 1000 here
             */
            FolderView view = new FolderView(10000);
            view.setPropertySet(new PropertySet(BasePropertySet.IdOnly, FolderSchema.DisplayName));
            ArrayList<Folder> folders = publicFoldersRoot.findFolders(view).getFolders();
            for (Folder folder : folders) {
                if (folder.getDisplayName().equals(folderName)) {
                    items = folder.findItems(new ItemView(10000));
                    service.loadPropertiesForItems(items, PropertySet.FirstClassProperties);
                    System.out.println(items.getTotalCount());
                    groupContacts = (ContactsFolder) folder;
                    break;
                }
            }

            if (groupContacts != null && items != null) {
                for (Item item : items) {
                    Contact contact = (Contact) item;
                    results.add(contact);
                }
            } else {
                System.out.println("Group contacts folder not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
