/**
 * 
 */
package net.sf.commons.ssh.impl.j2ssh;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sshtools.j2ssh.io.ByteArrayReader;
import com.sshtools.j2ssh.io.ByteArrayWriter;
import com.sshtools.j2ssh.transport.publickey.InvalidSshKeyException;
import com.sshtools.j2ssh.transport.publickey.InvalidSshKeySignatureException;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;
import com.sshtools.j2ssh.transport.publickey.dsa.SshDssPublicKey;
import com.sshtools.j2ssh.util.SimpleASNReader;

/**
 * @author fob
 * @date 28.08.2011
 * @since 2.0
 */
class SshDssPrivateKey extends SshPrivateKey {
    private static Log log = LogFactory.getLog(SshDssPrivateKey.class);
    DSAPrivateKey prvkey;

    /**
     * Creates a new SshDssPrivateKey object.
     *
     * @param prvkey
     */
    public SshDssPrivateKey(DSAPrivateKey prvkey) {
        this.prvkey = prvkey;
    }

    /**
     * Creates a new SshDssPrivateKey object.
     *
     * @param key
     *
     * @throws InvalidSshKeyException
     */
    public SshDssPrivateKey(byte[] key) throws InvalidSshKeyException {
        try {
            DSAPrivateKeySpec dsaKey;

            // Extract the key information
            ByteArrayReader bar = new ByteArrayReader(key);
            String header = bar.readString();

            if (!header.equals(getAlgorithmName())) {
                throw new InvalidSshKeyException();
            }

            BigInteger p = bar.readBigInteger();
            BigInteger q = bar.readBigInteger();
            BigInteger g = bar.readBigInteger();
            BigInteger x = bar.readBigInteger();
            dsaKey = new DSAPrivateKeySpec(x, p, q, g);

            KeyFactory kf = KeyFactory.getInstance("DSA");
            prvkey = (DSAPrivateKey) kf.generatePrivate(dsaKey);
        } catch (Exception e) {
            throw new InvalidSshKeyException();
        }
    }

    /**
     *
     *
     * @param obj
     *
     * @return
     */
    public boolean equals(Object obj) {
        if (obj instanceof SshDssPrivateKey) {
            return prvkey.equals(((SshDssPrivateKey) obj).prvkey);
        }

        return false;
    }

    /**
     *
     *
     * @return
     */
    public int hashCode() {
        return prvkey.hashCode();
    }

    /**
     *
     *
     * @return
     */
    public String getAlgorithmName() {
        return "ssh-dss";
    }

    /**
     *
     *
     * @return
     */
    public int getBitLength() {
        return prvkey.getX().bitLength();
    }

    /**
     *
     *
     * @return
     */
    public byte[] getEncoded() {
        try {
            ByteArrayWriter baw = new ByteArrayWriter();
            baw.writeString("ssh-dss");
            baw.writeBigInteger(prvkey.getParams().getP());
            baw.writeBigInteger(prvkey.getParams().getQ());
            baw.writeBigInteger(prvkey.getParams().getG());
            baw.writeBigInteger(prvkey.getX());

            return baw.toByteArray();
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     *
     *
     * @return
     */
    public SshPublicKey getPublicKey() {
        try {
            DSAPublicKeySpec spec = new DSAPublicKeySpec(getY(),
                    prvkey.getParams().getP(), prvkey.getParams().getQ(),
                    prvkey.getParams().getG());
            KeyFactory kf = KeyFactory.getInstance("DSA");

            return new SshDssPublicKey((DSAPublicKey) kf.generatePublic(spec));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     *
     * @param data
     *
     * @return
     *
     * @throws InvalidSshKeySignatureException
     */
    public byte[] generateSignature(byte[] data)
        throws InvalidSshKeySignatureException {
        try {
            Signature sig = Signature.getInstance("SHA1withDSA");
            sig.initSign(prvkey);

            /*java.util.Random rnd = new java.util.Random();
                         byte[] buffer = new byte[20];
                         rnd.nextBytes(buffer);
                         sig.update(buffer);
                         byte[] test = sig.sign();*/
            sig.update(data);

            byte[] signature = sig.sign();
            byte[] decoded = new byte[40];
            SimpleASNReader asn = new SimpleASNReader(signature);
            asn.getByte();
            asn.getLength();
            asn.getByte();

            byte[] r = asn.getData();
            asn.getByte();

            byte[] s = asn.getData();

            if (r.length >= 20) {
                System.arraycopy(r, r.length - 20, decoded, 0, 20);
            } else {
                System.arraycopy(r, 0, decoded, 20 - r.length, r.length);
            }

            if (s.length >= 20) {
                System.arraycopy(s, s.length - 20, decoded, 20, 20);
            } else {
                System.arraycopy(s, 0, decoded, 20 + (20 - s.length), s.length);
            }

            if (log.isDebugEnabled()) {
                log.debug("s length is " + String.valueOf(s.length));
                log.debug("r length is " + String.valueOf(r.length));

                String str = "";

                for (int i = 0; i < signature.length; i++) {
                    str += (Integer.toHexString(signature[i] & 0xFF) + " ");
                }

                log.debug("Java signature is " + str);
                str = "";

                for (int i = 0; i < decoded.length; i++) {
                    str += (Integer.toHexString(decoded[i] & 0xFF) + " ");
                }

                log.debug("SSH signature is " + str);
            }

            ByteArrayWriter baw = new ByteArrayWriter();
            baw.writeString(getAlgorithmName());
            baw.writeBinaryString(decoded);

            return baw.toByteArray();
        } catch (Exception e) {
            throw new InvalidSshKeySignatureException(e);
        }
    }

    private BigInteger getY() {
        return prvkey.getParams().getG().modPow(prvkey.getX(),
            prvkey.getParams().getP());
    }
}
