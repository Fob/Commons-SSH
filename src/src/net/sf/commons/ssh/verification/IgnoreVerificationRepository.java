package net.sf.commons.ssh.verification;

import java.security.PublicKey;
import java.util.Iterator;

/**
 * @author fob
 * @date 13.08.2011
 * @since 2.0
 */
public class IgnoreVerificationRepository implements VerificationRepository
{

	/**
	 * @see net.sf.commons.ssh.verification.VerificationRepository#check(java.lang.String, java.security.PublicKey)
	 */
	public boolean check(String host, PublicKey key)
	{
		return true;
	}

	/**
	 * @see net.sf.commons.ssh.verification.VerificationRepository#getIterator()
	 */
	public Iterator<VerificationEntry> getIterator()
	{
		return null;
	}

	/**
	 * @see net.sf.commons.ssh.verification.VerificationRepository#getIterator(java.lang.String)
	 */
	public Iterator<VerificationEntry> getIterator(String host)
	{
		return null;
	}

}
