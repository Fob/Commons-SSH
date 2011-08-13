/**
 * 
 */
package net.sf.commons.ssh.verification;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.VFS;

/**
 * @author fob
 * @date 13.08.2011
 * @since 2.0
 */
public class MapVerificationRepository implements VerificationRepository
{
	private static final Log log = LogFactory.getLog(MapVerificationRepository.class);
	protected Map<String, List<PublicKey>> map;

	/**
	 * @param map
	 */
	public MapVerificationRepository(Map<String, List<PublicKey>> map)
	{
		super();
		this.map = map;
	}

	public MapVerificationRepository(Reader reader) throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException
	{
		BufferedReader fileReader = new KnownHostsReader(reader);
		String line = null;
		while ((line = fileReader.readLine()) != null)
		{
			VerificationEntry entry = new VerificationEntry(line);
			for (String host : entry.getHosts())
			{
				host = host.toLowerCase();
				List<PublicKey> keys = map.get(host);
				if (keys == null)
					keys = new ArrayList<PublicKey>();
				if (!keys.contains(entry.getPublicKey()))
					keys.add(entry.getPublicKey());
				map.put(host, keys);
			}
		}

	}

	public MapVerificationRepository(InputStream stream) throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException
	{
		this(new InputStreamReader(stream));
	}

	public MapVerificationRepository(String file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		this(VFS.getManager().resolveFile(new File("."), file).getContent().getInputStream());
	}

	/**
	 * @see net.sf.commons.ssh.verification.VerificationRepository#check(java.lang.String,
	 *      java.security.PublicKey)
	 */
	@Override
	public boolean check(String host, PublicKey key)
	{
		List<PublicKey> keys = map.get(host.toLowerCase());
		if (keys == null)
			return false;
		for (PublicKey publicKey : keys)
			if (publicKey.equals(key))
				return true;
		return false;
	}

	/**
	 * @see net.sf.commons.ssh.verification.VerificationRepository#getIterator()
	 */
	@Override
	public Iterator<VerificationEntry> getIterator()
	{
		Map<PublicKey, Set<String>> mapStructure = new HashMap<PublicKey, Set<String>>();
		for (Entry<String, List<PublicKey>> entry : map.entrySet())
		{
			for (PublicKey key : entry.getValue())
			{
				Set<String> hosts = mapStructure.get(key);
				if (hosts == null)
					hosts = new HashSet<String>();
				if (!hosts.contains(entry.getKey()))
					hosts.add(entry.getKey());
				mapStructure.put(key, hosts);
			}
		}

		final Iterator<Entry<PublicKey, Set<String>>> iterator = mapStructure.entrySet().iterator();

		return new Iterator<VerificationEntry>()
			{

				@Override
				public boolean hasNext()
				{
					return iterator.hasNext();
				}

				@Override
				public VerificationEntry next()
				{
					Entry<PublicKey, Set<String>> entry = iterator.next();

					return new VerificationEntry(entry.getValue(), entry.getKey());
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
	}

	/**
	 * @see net.sf.commons.ssh.verification.VerificationRepository#getIterator(java.lang.String)
	 */
	@Override
	public Iterator<VerificationEntry> getIterator(String host)
	{
		Map<PublicKey, Set<String>> mapStructure = new HashMap<PublicKey, Set<String>>();
		for (PublicKey key : map.get(host))
		{
			Set<String> hosts = mapStructure.get(key);
			if (hosts == null)
				hosts = new HashSet<String>();
			if (!hosts.contains(host))
				hosts.add(host);
			mapStructure.put(key, hosts);
		}

		final Iterator<Entry<PublicKey, Set<String>>> iterator = mapStructure.entrySet().iterator();

		return new Iterator<VerificationEntry>()
			{

				@Override
				public boolean hasNext()
				{
					return iterator.hasNext();
				}

				@Override
				public VerificationEntry next()
				{
					Entry<PublicKey, Set<String>> entry = iterator.next();

					return new VerificationEntry(entry.getValue(), entry.getKey());
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
	}

}
