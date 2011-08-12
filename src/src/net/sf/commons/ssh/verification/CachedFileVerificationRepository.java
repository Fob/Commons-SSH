package net.sf.commons.ssh.verification;


import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class CachedFileVerificationRepository extends FileVerificationRepository
{
    private Map<String,List<VerificationEntry>> repository;

    public CachedFileVerificationRepository(String file)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        super(file);
        repository = new HashMap<String,List<VerificationEntry>>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.file.getContent().getInputStream()));
        try
        {
            String line=null;
            while ((line = reader.readLine())!=null)
            {
                line = StringUtils.trim(line);
                if(StringUtils.isBlank(line))
                    continue;
                if(line.startsWith("#"))
                    continue;

                VerificationEntry entry = new VerificationEntry(line);

                for(String host:entry.getHosts())
                {
                    List<VerificationEntry> entries = repository.get(host);
                    if(entries == null)
                        entries = new ArrayList<VerificationEntry>();
                    entries.add(entry);
                    repository.put(host,entries);
                }
            }
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                log.debug(e);
            }
        }
    }

    @Override
    public Iterator<VerificationEntry> getVerificationEntries()
    {
        return super.getVerificationEntries();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Iterator<VerificationEntry> getVerificationEntries(String host)
    {
        return super.getVerificationEntries(host);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
