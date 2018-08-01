/**
 * 
 */
package net.sf.commons.ssh.verification;

import java.security.PublicKey;
import java.util.Iterator;

/**
 * @author fob
 * @date 13.08.2011
 * @since 2.0
 */
public interface VerificationRepository
{
	
	boolean check(String host,PublicKey key);
	
	Iterator<VerificationEntry> getIterator();
	
	Iterator<VerificationEntry> getIterator(String host);
	
}
