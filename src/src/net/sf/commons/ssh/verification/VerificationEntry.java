package net.sf.commons.ssh.verification;


import net.sf.commons.ssh.utils.KeyUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VerificationEntry
{
	//private static final Log log = LogFactory.getLog(VerificationEntry.class);
	private Set<String> hosts;
	private PublicKey publicKey;

	public VerificationEntry(Set<String> hosts, PublicKey publicKey)
	{
		this.hosts = hosts;
		this.publicKey = publicKey;
	}

	public VerificationEntry(String entry) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException
	{
		hosts = new HashSet<String>();
		hosts.addAll(Arrays.asList(StringUtils.split(StringUtils.substringBefore(entry, " ").toLowerCase(), ',')));
		entry = StringUtils.substringAfter(StringUtils.substringAfter(entry, " "), "-");
		entry = StringUtils.substringAfter(entry, " ");

		publicKey = KeyUtils.getKeyFromBase64(entry.getBytes());
	}

	public Set<String> getHosts()
	{
		return hosts;
	}

	public PublicKey getPublicKey()
	{
		return publicKey;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		for (String host : getHosts())
			builder.append(host).append(",");
		builder.setLength(builder.length() - 1);
		builder.append(" ssh-");

		if(publicKey instanceof RSAPublicKey)
			builder.append("rsa ");
		else
			builder.append("dss ");

		builder.append(KeyUtils.encodeKeyToBase64(publicKey));
		return builder.toString();
	}

}
