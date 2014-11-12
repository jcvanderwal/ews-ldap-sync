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
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentials = new WebCredentials(Constants.USERNAME, Constants.PASSWORD);
        service.setCredentials(credentials);
        service.setTraceEnabled(true);
        ContactsFolder groupContacts = null;
        List<Contact> contacts = new ArrayList<Contact>();

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
                    int pageSize = 1000;
                    int offset = 0;
                    boolean moreItems = true;
                    ItemView iview = new ItemView(pageSize, offset);

                    while (moreItems) {
                        try {
                            try {
                                FindItemsResults<Item> itemsResults = folder.findItems(iview);
                                service.loadPropertiesForItems(itemsResults, PropertySet.FirstClassProperties);
                                moreItems = itemsResults.isMoreAvailable();
                                
                                for (Item item: itemsResults) {
                                    if (item instanceof Contact) {
                                        Contact contact = (Contact) item;
                                        contacts.add(contact);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            
                            if (moreItems) {
                                iview.setOffset(offset + pageSize);
                                offset += pageSize;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                    
                    groupContacts = (ContactsFolder) folder;
                    break;
                }
            }

            if (groupContacts != null && !contacts.isEmpty()) {
                System.out.println("Contacts size: " + contacts.size());
                return contacts;
            } else {
                System.out.println("Group contacts folder not found");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
