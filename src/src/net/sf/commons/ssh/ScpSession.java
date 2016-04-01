package net.sf.commons.ssh;


import java.io.IOException;

public interface ScpSession extends Session {
    void put(String from, String to, boolean b) throws IOException;
}
