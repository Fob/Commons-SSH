package net.sf.commons.ssh.impl.jsch;

import com.jcraft.jsch.SocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @since 1.4
 */
public class JschSocketFactory implements SocketFactory {

    private int connectTimeout = 0;
    private int soTimeout = 0;

    public JschSocketFactory() {
    }

    public JschSocketFactory(int connectTimeout, int soTimeout) {
        setConnectTimeout(connectTimeout);
        setSoTimeout(soTimeout);
    }

    public Socket createSocket(String host, int port) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), getConnectTimeout());
        socket.setSoTimeout(getSoTimeout());

        return socket;
    }

    public InputStream getInputStream(Socket socket) throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream(Socket socket) throws IOException {
        return socket.getOutputStream();
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

}
