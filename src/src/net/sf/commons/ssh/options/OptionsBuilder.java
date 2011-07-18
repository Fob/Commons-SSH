package net.sf.commons.ssh.options;

public abstract class OptionsBuilder
{
    protected Options options;

    protected OptionsBuilder()
    {
        this(new Options());
    }

    public OptionsBuilder(Options options)
    {
        this.options = options;
        initDefault();
    }

    protected abstract void initDefault();

    public Options getOptions()
    {
        return options;
    }
}
