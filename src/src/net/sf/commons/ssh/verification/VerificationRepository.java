package net.sf.commons.ssh.verification;



import java.security.PublicKey;
import java.util.Iterator;

public interface VerificationRepository
{
	boolean check(String host,PublicKey key);

	Iterator<VerificationEntry> getIterator();

	Iterator<VerificationEntry> getIterator(String host);
}
