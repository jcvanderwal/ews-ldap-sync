package com.ecpnv.sync;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class DirectorySample {

	public DirectorySample() {
	}

	public void doLookup() {
		Properties properties = new Properties();
		properties.put(
				Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(
				Context.PROVIDER_URL, 
				"ldap://10.3.0.201:389");
		try {
			DirContext context = new InitialDirContext(properties);
			Attributes attrs = context
					.getAttributes("ou=120,ou=phonebook,dc=3cx,dc=ecp,cn=test");
			System.out.println("Common name : " + attrs.get("cn").get());
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DirectorySample sample = new DirectorySample();
		sample.doLookup();
	}
}
