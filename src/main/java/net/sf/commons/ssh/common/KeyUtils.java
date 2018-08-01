/**
 * 
 */
package net.sf.commons.ssh.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.bouncycastle.openssl.*;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;

/**
 * @author fob
 * @date 14.08.2011
 * @since 2.0
 */
public class KeyUtils
{
	public static PublicKey getKeyFromBase64(byte[] keyBytes) throws InvalidKeySpecException, IOException,
			NoSuchAlgorithmException
	{
		return getKeyFromBytes(Base64.decodeBase64(keyBytes));
	}

	public static PublicKey getKeyFromBytes(byte[] keyBytes) throws InvalidKeySpecException, IOException,
			NoSuchAlgorithmException
	{
		DataInputStream keyData = new DataInputStream(new ByteArrayInputStream(keyBytes));
		int length = keyData.readInt();
		String alg = new String(readBytes(keyData, length));
		alg = StringUtils.substringAfter(alg, "-").toLowerCase();
		if ("dss".equals(alg))
			alg = "dsa";

		KeyFactory keyFactory = KeyFactory.getInstance(alg.toUpperCase());
		KeySpec spec;
		if ("rsa".equals(alg))
		{
			length = keyData.readInt();
			BigInteger e = readBigInteger(length, keyData);
			length = keyData.readInt();
			BigInteger m = readBigInteger(length, keyData);

			spec = new RSAPublicKeySpec(m, e);
		}
		else
		{
			length = keyData.readInt();
			BigInteger p = readBigInteger(length, keyData);
			length = keyData.readInt();
			BigInteger q = readBigInteger(length, keyData);
			length = keyData.readInt();
			BigInteger g = readBigInteger(length, keyData);
			length = keyData.readInt();
			BigInteger y = readBigInteger(length, keyData);

			spec = new DSAPublicKeySpec(y, p, q, g);
		}
		return keyFactory.generatePublic(spec);
	}

	public static PublicKey getKeyFromBase64(String keyBytes) throws InvalidKeySpecException, IOException,
			NoSuchAlgorithmException
	{
		return getKeyFromBase64(keyBytes.getBytes());
	}

	public static String encodeKeyToBase64(PublicKey publicKey)
	{
		ByteArrayOutputStream keyBytes = new ByteArrayOutputStream();
		DataOutputStream keyData = new DataOutputStream(keyBytes);
		try
		{
			if (publicKey instanceof RSAPublicKey)
			{
				keyData.writeInt("ssh-rsa".length());
				keyData.write("ssh-rsa".getBytes());
				BigInteger e = ((RSAPublicKey) publicKey).getPublicExponent();
				keyData.writeInt(e.toByteArray().length);
				keyData.write(e.toByteArray());
				BigInteger m = ((RSAPublicKey) publicKey).getModulus();
				keyData.writeInt(m.toByteArray().length);
				keyData.write(m.toByteArray());
				keyData.close();

			}
			else
			{
				keyData.writeInt("ssh-dss".length());
				keyData.write("ssh-dss".getBytes());
				BigInteger p = ((DSAPublicKey) publicKey).getParams().getP();
				BigInteger g = ((DSAPublicKey) publicKey).getParams().getG();
				BigInteger q = ((DSAPublicKey) publicKey).getParams().getQ();
				BigInteger y = ((DSAPublicKey) publicKey).getY();
				keyData.writeInt(p.toByteArray().length);
				keyData.write(p.toByteArray());
				keyData.writeInt(q.toByteArray().length);
				keyData.write(q.toByteArray());
				keyData.writeInt(g.toByteArray().length);
				keyData.write(g.toByteArray());
				keyData.writeInt(y.toByteArray().length);
				keyData.write(y.toByteArray());
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException("unexpected IO exception", e);
		}
		return new String(Base64.encodeBase64(keyBytes.toByteArray()));
	}

	public static PublicKey getKeyFromPubFile(Reader reader) throws InvalidKeySpecException, IOException,
			NoSuchAlgorithmException
	{
		BufferedReader bReader = new BufferedReader(reader);
		String line = bReader.readLine();
		line = StringUtils.substringAfter(line, " ");
		line = StringUtils.substringBefore(line, " ");
		return getKeyFromBase64(line.getBytes());
	}

	public static PublicKey getKeyFromPubFile(InputStream stream) throws InvalidKeySpecException, IOException,
			NoSuchAlgorithmException
	{
		return getKeyFromPubFile(new InputStreamReader(stream));
	}

	public static PublicKey getKeyFromPubFile(String file) throws InvalidKeySpecException, IOException,
			NoSuchAlgorithmException
	{
		FileObject fileObject = VFS.getManager().resolveFile(new File("."), file);
		InputStream st = fileObject.getContent().getInputStream();
		try
		{
			return getKeyFromPubFile(st);
		}
		finally
		{
			IOUtils.close(st);
		}
	}

	public static KeyPair getPrivateKeyFromStream(InputStream stream, final String passphrase) throws IOException
	{
		JcaPEMKeyConverter  converter = new JcaPEMKeyConverter();

		Reader fRd = new BufferedReader(new InputStreamReader(stream));
		PEMParser pemParser = new PEMParser(fRd);
		Object o = pemParser.readObject();
		System.out.println(o);

		KeyPair kp;
		if(passphrase == null)
			kp = converter.getKeyPair((PEMKeyPair)o);
		else
			kp = converter.getKeyPair(((PEMEncryptedKeyPair)o)
					.decryptKeyPair(new JcePEMDecryptorProviderBuilder().build(passphrase.toCharArray())));
		return kp;
	}

	public static KeyPair getPrivateKeyFromBytes(byte[] bytes, String passphrase) throws IOException
	{
		return getPrivateKeyFromStream(new ByteArrayInputStream(bytes), passphrase);
	}

	public static KeyPair getPrivateKeyFromFile(String file, String passphrase) throws IOException
	{

		FileObject fileObject = VFS.getManager().resolveFile(new File("."), file);
		InputStream st = fileObject.getContent().getInputStream();
		try
		{
			return getPrivateKeyFromStream(st, passphrase);
		}
		finally
		{
			IOUtils.close(st);
		}
	}

	protected static byte[] readBytes(InputStream stream, int len) throws IOException
	{
		byte[] buffer = new byte[len];
		stream.read(buffer);
		return buffer;
	}

	private static BigInteger readBigInteger(int length, InputStream stream) throws IOException
	{
		byte[] buffer = new byte[length];
		stream.read(buffer);
		return new BigInteger(buffer);
	}

	public static byte[] serializePrivateKey(PrivateKey key) throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		serializePrivateKey(key, new OutputStreamWriter(stream));
		return stream.toByteArray();
	}

	public static void serializePrivateKey(PrivateKey key, Writer writer) throws IOException
	{
		PEMWriter pemWriter = new PEMWriter(writer);
		pemWriter.writeObject(key);
		pemWriter.flush();
	}
	
	public static void serializePrivateKey(PrivateKey key, Writer writer,char[] password,String alg) throws IOException
	{
		PEMWriter pemWriter = new PEMWriter(writer);
		pemWriter.writeObject(key,
				new JcePEMEncryptorBuilder(alg).setSecureRandom(new SecureRandom()).build(password));
		pemWriter.flush();
	}
}
