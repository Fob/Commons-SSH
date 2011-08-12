package net.sf.commons.ssh.verification;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VerificationEntry
{
    private static final Log log = LogFactory.getLog(VerificationEntry.class);
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
        entry = StringUtils.substringAfter(StringUtils.substringAfter(entry," "),"-");

        String alg = StringUtils.substringBefore(entry, " ");
        if("dss".equals(alg))
            alg = "dsa";
        else if("rsa".equals(alg))
            alg = "rsa";
        else
            throw new NoSuchAlgorithmException(alg);

        entry = StringUtils.substringAfter(entry," ");

        KeyFactory keyFactory = KeyFactory.getInstance(alg.toUpperCase());

        DataInputStream keyData=new DataInputStream(new ByteArrayInputStream(Base64.decodeBase64(entry.getBytes())));
        int length = keyData.readInt();
        keyData.skipBytes(length);
        KeySpec spec;
        if("rsa".equals(alg))
        {
            length = keyData.readInt();
            BigInteger e = readBigInteger(length,keyData);
            length = keyData.readInt();
            BigInteger m = readBigInteger(length,keyData);


            spec = new RSAPublicKeySpec(m,e);
        }
        else
        {
            length = keyData.readInt();
            BigInteger p = readBigInteger(length,keyData);
            length = keyData.readInt();
            BigInteger q = readBigInteger(length,keyData);
            length = keyData.readInt();
            BigInteger g = readBigInteger(length,keyData);
            length = keyData.readInt();
            BigInteger y = readBigInteger(length,keyData);

            spec = new DSAPublicKeySpec(y,p,q,g);
        }
        publicKey = keyFactory.generatePublic(spec);
    }

    private BigInteger readBigInteger(int length,InputStream stream) throws IOException
    {
        byte[] buffer = new byte[length];
        stream.read(buffer);
        return new BigInteger(buffer);
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
        for(String host: getHosts())
            builder.append(host).append(",");
        builder.setLength(builder.length()-1);
        builder.append(" ssh-");

        ByteArrayOutputStream keyBytes = new ByteArrayOutputStream();
        DataOutputStream keyData = new DataOutputStream(keyBytes);
        try
        {
            if (publicKey instanceof RSAPublicKey)
            {
                builder.append("rsa ");
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
                builder.append("dss ");
                keyData.writeInt("ssh-dss".length());
                keyData.write("ssh-dss".getBytes());
                BigInteger p = ((DSAPublicKey)publicKey).getParams().getP();
                BigInteger g = ((DSAPublicKey)publicKey).getParams().getG();
                BigInteger q = ((DSAPublicKey)publicKey).getParams().getQ();
                BigInteger y = ((DSAPublicKey)publicKey).getY();
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
        builder.append(new String(Base64.encodeBase64(keyBytes.toByteArray())));
        return builder.toString();
    }

}
