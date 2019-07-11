package com.sangupta.clitools.misc;

import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import io.airlift.airline.SingleCommand;

import java.util.Hashtable;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.sangupta.clitools.CliTool;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.ConsoleUtils;
import com.sangupta.jerry.util.ReadableUtils;

@Command(name = "ldap", description = "Connect to an LDAP server")
public class LDAP implements CliTool {

	@Option(name = { "--serverUrl", "-su" }, description = "The LDAP server URL to use")
	private String ldapServerUrl;

	@Option(name = { "--branch", "-b" }, description = "The LDAP branch to use")
	private String ldapBranch;

	@Option(name = { "--ctx", "-c" }, description = "The context factory to use, default is com.sun.jndi.ldap.LdapCtxFactory")
	private String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";

	@Option(name = { "--authentication", "-a" }, description = "Security authentication method to use, default is simple")
	private String securityAuthentication = "simple";
	
	@Option(name = { "--username", "-u" }, description = "The LDAP username to validate against")
	private String username;
	
	@Option(name = { "--password", "-p" }, description = "The LDAP password to use for validation")
	private String password;
	
	private String SECURITY_PROTOCOL = "ssl";
	
	@Inject
	public HelpOption helpOption;
	
	public static void main(String[] args) {
		if(AssertUtils.isEmpty(args )) {
			args = new String[] { "--help" };
		}
		
		LDAP ldap = SingleCommand.singleCommand(LDAP.class).parse(args);
		if(ldap.helpOption.showHelpIfRequested()) {
			return;
		}
		
		if(AssertUtils.isEmpty(ldap.password)) {
			ldap.password = ConsoleUtils.readPassword("Password: ", true);
		}
		
		long start = System.currentTimeMillis();
		try {
			ldap.authenticateUser();
		} finally {
			long end = System.currentTimeMillis();
			System.out.println("\nLDAP query took " + ReadableUtils.getReadableTimeDuration(end - start));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void authenticateUser() {
		if(AssertUtils.isEmpty(this.ldapServerUrl)) {
			System.out.println("LDAP url is required.");
			return;
		}
		
		if(AssertUtils.isEmpty(this.ldapBranch)) {
			System.out.println("LDAP branch is required.");
			return;
		}
		
		if(AssertUtils.isEmpty(this.username)) {
			System.out.println("LDAP username is required.");
			return;
		}
		
		Hashtable<String, String> env = new Hashtable<String, String>(10, 0.75f);
		
		String localnode = "cn=" + this.username.toLowerCase();

		env.put(Context.INITIAL_CONTEXT_FACTORY, this.initialContextFactory);
		env.put(Context.SECURITY_AUTHENTICATION, this.securityAuthentication);
		env.put(Context.SECURITY_PROTOCOL, this.SECURITY_PROTOCOL);
		env.put(Context.PROVIDER_URL, this.ldapServerUrl);
		env.put(Context.SECURITY_PRINCIPAL, localnode + ", " + this.ldapBranch);
		env.put(Context.SECURITY_CREDENTIALS, this.password);
		DirContext ctx = null;

		try {
			// this call will throw a NamingException
			// if the authentication data is invalid
			ctx = new InitialDirContext(env);
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			NamingEnumeration results = ctx.search(ldapBranch, localnode, controls);

			if (results.hasMore()) {
				SearchResult user = (SearchResult) results.next();
				System.out.println("Details for user: " + user.getName());
				Attributes attributes = user.getAttributes();
				if(attributes == null) {
					System.out.print("\tNo attributes found.");
					return;
				}
				
				NamingEnumeration<String> names = attributes.getIDs();
				while(names.hasMore()) {
					String attributeName = names.nextElement();
					Attribute attribute = attributes.get(attributeName);
					System.out.println("\t" + attributeName + ": " + attribute.get().toString());
				}
			}
		} catch (javax.naming.NamingException e) {
			System.out.println("Error fetching details from LDAP: " + e.getMessage());
		}

	}
}
