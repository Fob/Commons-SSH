package net.sf.commons.ssh.session;


import net.sf.commons.ssh.options.*;

public class ShellSessionPropertiesBuilder extends SessionPropertiesBuilder {
    private static ShellSessionPropertiesBuilder instance = null;

    @PropertyType(value = Integer.class, required = true)
    public static final String KEY_TERMINAL_COLS = "net.sf.commons.ssh.options.ShellSessionOptionsBuilder.terminalCols";
    @PropertyType(value = Integer.class, required = true)
    public static final String KEY_TERMINAL_HEIGHT = "net.sf.commons.ssh.options.ShellSessionOptionsBuilder.terminalHeight";
    @PropertyType(value = Integer.class, required = true)
    public static final String KEY_TERMINAL_ROWS = "net.sf.commons.ssh.options.ShellSessionOptionsBuilder.terminalRows";
    @PropertyType(value = String.class, required = true)
    public static final String KEY_TERMINAL_TYPE = "net.sf.commons.ssh.options.ShellSessionOptionsBuilder.terminalType";
    @PropertyType(value = Integer.class, required = true)
    public static final String KEY_TERMINAL_WIDTH = "net.sf.commons.ssh.options.ShellSessionOptionsBuilder.terminalWidth";
    @PropertyType(value = Boolean.class, required = true)
    public static final String KEY_SEPARATE_ERROR_STREAM = "net.sf.commons.ssh.options.ShellSessionOptionsBuilder.errorStream";
//    @PropertyType(value = String.class,required = false)
//    public static final String KEY_START_SUBSYSTEM = "net.sf.commons.ssh.options.ShellSessionOptionsBuilder.subSystem";

    public synchronized static ShellSessionPropertiesBuilder getInstance() {
        if (instance == null) {
            instance = new ShellSessionPropertiesBuilder();
        }
        return instance;
    }

    ShellSessionPropertiesBuilder() {
        defaultProperties.put(KEY_TERMINAL_COLS, Integer.valueOf(80));
        defaultProperties.put(KEY_TERMINAL_HEIGHT, Integer.valueOf(0));
        defaultProperties.put(KEY_TERMINAL_ROWS, Integer.valueOf(24));
        defaultProperties.put(KEY_TERMINAL_TYPE, "ansi");
        defaultProperties.put(KEY_TERMINAL_WIDTH, Integer.valueOf(0));
        defaultProperties.put(KEY_SEPARATE_ERROR_STREAM, true);
    }

    public int getTerminalCols(Properties opt) {
        return (Integer) getProperty(opt, KEY_TERMINAL_COLS);
    }

    public void setTerminalCols(Configurable options, int terminalCols) {
        options.setProperty(KEY_TERMINAL_COLS, terminalCols);
    }

    public int getTerminalHeight(Properties opt) {
        return (Integer) getProperty(opt, KEY_TERMINAL_HEIGHT);
    }

    public void setTerminalHeight(Configurable options, int terminalHeight) {
        options.setProperty(KEY_TERMINAL_HEIGHT, terminalHeight);
    }

    public int getTerminalRows(Properties opt) {
        return (Integer) getProperty(opt, KEY_TERMINAL_ROWS);
    }

    public void setTerminalRows(Configurable options, int terminalRows) {
        options.setProperty(KEY_TERMINAL_ROWS, terminalRows);
    }

    public String getTerminalType(Properties opt) {
        return (String) getProperty(opt, KEY_TERMINAL_TYPE);
    }

    public void setTerminalType(Configurable options, String terminalType) {
        options.setProperty(KEY_TERMINAL_TYPE, terminalType);
    }

    public int getTerminalWidth(Properties opt) {
        return (Integer) getProperty(opt, KEY_TERMINAL_WIDTH);
    }

    public void setTerminalWidth(Configurable options, int terminalWidth) {
        options.setProperty(KEY_TERMINAL_WIDTH, terminalWidth);
    }

    public void setSeparateErrorStream(Configurable config, boolean value) {
        setProperty(config, KEY_SEPARATE_ERROR_STREAM, value);
    }

    public boolean isSeparateErrorStream(Properties config) {
        return (Boolean) getProperty(config, KEY_SEPARATE_ERROR_STREAM);
    }
}
