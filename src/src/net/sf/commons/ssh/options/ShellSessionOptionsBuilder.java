package net.sf.commons.ssh.options;

public class ShellSessionOptionsBuilder extends PropertiesBuilder
{
/*    public int terminalCols = 80;
    public int terminalHeight = 0;
    public int terminalRows = 24;
    public String terminalType = "ansi"; //$NON-NLS-1$
    public int terminalWidth = 0;*/

    public static final String KEY_TERMINAL_COLS="net.sf.commons.ssh.options.ShellSessionOptionsBuilder.terminalCols";
    public static final String KEY_TERMINAL_HEIGHT="net.sf.commons.ssh.options.ShellSessionOptionsBuilder.terminalHeight";
    public static final String KEY_TERMINAL_ROWS="net.sf.commons.ssh.options.ShellSessionOptionsBuilder.terminalRows";
    public static final String KEY_TERMINAL_TYPE="net.sf.commons.ssh.options.ShellSessionOptionsBuilder.terminalType";
    public static final String KEY_TERMINAL_WIDTH="net.sf.commons.ssh.options.ShellSessionOptionsBuilder.terminalWidth";

    public ShellSessionOptionsBuilder()
    {
    }

    public ShellSessionOptionsBuilder(AbstractConfigurable options)
    {
        super(options);
    }

    @Override
    protected void setupDefault()
    {
        setTerminalCols(80);
        setTerminalHeight(0);
        setTerminalRows(24);
        setTerminalType("ansi");
        setTerminalWidth(0);
    }

    public int getTerminalCols()
    {
        return (Integer)options.getProperty(KEY_TERMINAL_COLS);
    }

    public void setTerminalCols(int terminalCols)
    {
        options.setProperty(KEY_TERMINAL_COLS,terminalCols);
    }

    public int getTerminalHeight()
    {
        return (Integer) options.getProperty(KEY_TERMINAL_HEIGHT);
    }

    public void setTerminalHeight(int terminalHeight)
    {
        options.setProperty(KEY_TERMINAL_HEIGHT,terminalHeight);
    }

    public int getTerminalRows()
    {
        return (Integer)options.getProperty(KEY_TERMINAL_ROWS);
    }

    public void setTerminalRows(int terminalRows)
    {
        options.setProperty(KEY_TERMINAL_ROWS,terminalRows);
    }

    public String getTerminalType()
    {
        return (String) options.getProperty(KEY_TERMINAL_TYPE);
    }

    public void setTerminalType(String terminalType)
    {
        options.setProperty(KEY_TERMINAL_TYPE,terminalType);
    }

    public int getTerminalWidth()
    {
        return (Integer)options.getProperty(KEY_TERMINAL_WIDTH);
    }

    public void setTerminalWidth(int terminalWidth)
    {
        options.setProperty(KEY_TERMINAL_WIDTH,terminalWidth);
    }
    //static
    public static void initDefault(AbstractConfigurable options)
    {
        setTerminalCols(options,80);
        setTerminalHeight(options,0);
        setTerminalRows(options,24);
        setTerminalType(options,"ansi");
        setTerminalWidth(options,0);
    }

    public static int getTerminalCols(AbstractConfigurable options)
    {
        return (Integer)options.getProperty(KEY_TERMINAL_COLS);
    }

    public static void setTerminalCols(AbstractConfigurable options,int terminalCols)
    {
        options.setProperty(KEY_TERMINAL_COLS,terminalCols);
    }

    public static int getTerminalHeight(AbstractConfigurable options)
    {
        return (Integer) options.getProperty(KEY_TERMINAL_HEIGHT);
    }

    public static void setTerminalHeight(AbstractConfigurable options,int terminalHeight)
    {
        options.setProperty(KEY_TERMINAL_HEIGHT,terminalHeight);
    }

    public static int getTerminalRows(AbstractConfigurable options)
    {
        return (Integer)options.getProperty(KEY_TERMINAL_ROWS);
    }

    public static void setTerminalRows(AbstractConfigurable options,int terminalRows)
    {
        options.setProperty(KEY_TERMINAL_ROWS,terminalRows);
    }

    public static String getTerminalType(AbstractConfigurable options)
    {
        return (String) options.getProperty(KEY_TERMINAL_TYPE);
    }

    public static void setTerminalType(AbstractConfigurable options,String terminalType)
    {
        options.setProperty(KEY_TERMINAL_TYPE,terminalType);
    }

    public static int getTerminalWidth(AbstractConfigurable options)
    {
        return (Integer)options.getProperty(KEY_TERMINAL_WIDTH);
    }

    public static void setTerminalWidth(AbstractConfigurable options,int terminalWidth)
    {
        options.setProperty(KEY_TERMINAL_WIDTH,terminalWidth);
    }
}
