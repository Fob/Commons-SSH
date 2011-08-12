package net.sf.commons.ssh.verification;


import org.apache.commons.vfs.FileSystemException;

import java.io.IOException;
import java.util.Iterator;

public interface VerificationRepository
{
    boolean check(String host, VerificationEntry entry);
    Iterator<VerificationEntry> getVerificationEntries();
    Iterator<VerificationEntry> getVerificationEntries(final String host);
}
