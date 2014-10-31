package com.ecpnv.sync;

//import javax.naming.NamingEnumeration;
//import javax.naming.NamingException;
//import javax.naming.directory.Attribute;
//import javax.naming.directory.Attributes;
//import javax.naming.directory.BasicAttribute;
//import javax.naming.directory.BasicAttributes;
//import javax.naming.directory.SearchControls;
//import javax.naming.directory.SearchResult;

import microsoft.exchange.webservices.data.Contact;

public class LdapWriter {

	private String server;
	private String username;
	private String password;

	public LdapWriter(String server, String username, String password) {
		this.server = server;
		this.username = username;
		this.password = password;
	}

	public void write(Contact contact){
		
	}
	
//    private String getUserAttribs (String searchAttribValue) throws NamingException{
//    	Attributes attributes=new BasicAttributes();
//    	Attribute objectClass=new BasicAttribute("objectClass");
//    	objectClass.add("inetOrgPerson");
//    	attributes.put(objectClass);
//
//    	Attribute sn=new BasicAttribute("sn");
//    	Attribute cn=new BasicAttribute("cn");
//
//    	sn.add("sahul");
//    	cn.add("vetcha");
//
//    	attributes.put(sn);
//    	attributes.put(cn);
//    	attributes.put("title","software engg")
//    	ctx.createSubcontext("uid=sahul,ou=some organization7,o=some company7,ou=system",attributes);
//	}

}
