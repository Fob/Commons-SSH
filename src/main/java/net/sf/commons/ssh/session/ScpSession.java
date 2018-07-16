package net.sf.commons.ssh.session;

/**
 * Created by anku0315 on 27.04.2016.
 */
import java.io.IOException;

public interface ScpSession extends Session {
    void put(String from, String to, boolean b) throws IOException;
}