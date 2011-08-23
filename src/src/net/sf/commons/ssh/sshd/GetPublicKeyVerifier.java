package net.sf.commons.ssh.sshd;


import org.apache.commons.lang.StringUtils;
import org.apache.sshd.ClientSession;
import org.apache.sshd.client.ServerKeyVerifier;

import java.net.SocketAddress;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GetPublicKeyVerifier implements ServerKeyVerifier
{
    private ConcurrentMap<String,PublicKey> keys = new ConcurrentHashMap<String,PublicKey>();

    public boolean verifyServerKey(ClientSession sshClientSession, SocketAddress remoteAddress, PublicKey serverKey)
    {
        String host= remoteAddress.toString();
        if(host.startsWith("/"))
            host = StringUtils.substringBetween(host, "/", ":");
        else
            host = StringUtils.substringBefore(host,"/");
        keys.putIfAbsent(host,serverKey);
        keys.replace(host,serverKey);
        return true;
    }

    public PublicKey getKey(String host)
    {
        return keys.get(host);
    }
}
