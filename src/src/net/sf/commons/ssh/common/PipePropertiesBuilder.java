package net.sf.commons.ssh.common;

import net.sf.commons.ssh.options.*;

public class PipePropertiesBuilder extends PropertiesBuilder
{
    private static PipePropertiesBuilder instance = null;

    @PropertyType(Integer.class)
    public static final String INITIAL_SIZE = "net.sf.commons.ssh.common.PipePropertiesBuilder.initial";
    @PropertyType(Integer.class)
    public static final String MAXIMUM_SIZE = "net.sf.commons.ssh.common.PipePropertiesBuilder.maximum";
    @PropertyType(Integer.class)
    public static final String STEP_SIZE = "net.sf.commons.ssh.common.PipePropertiesBuilder.step";
    @PropertyType(Integer.class)
    public static final String MODIFIER_SIZE = "net.sf.commons.ssh.common.PipePropertiesBuilder.mod";
    @PropertyType(BufferAllocator.class)
    public static final String ALLOCATOR = "net.sf.commons.ssh.common.PipePropertiesBuilder.allocator";

    protected PipePropertiesBuilder()
    {
        defaultProperties.put(INITIAL_SIZE, 1024);
        defaultProperties.put(MAXIMUM_SIZE, 2 * 1024 * 1024);
        defaultProperties.put(STEP_SIZE, 1024);
        defaultProperties.put(MODIFIER_SIZE, 2);
        defaultProperties.put(ALLOCATOR, new SoftBufferAllocator());
    }

    public synchronized static PipePropertiesBuilder getInstance()
    {
        if (instance == null)
        {
            instance = new PipePropertiesBuilder();
        }
        return instance;
    }

    public Integer getInitialSize(Properties conf)
    {
        return (Integer) getProperty(conf, INITIAL_SIZE);
    }

    public Integer getMaximumSize(Properties conf)
    {
        return (Integer) getProperty(conf, MAXIMUM_SIZE);
    }

    public Integer getStepSize(Properties conf)
    {
        return (Integer) getProperty(conf, STEP_SIZE);
    }

    public Integer getModifier(Properties conf)
    {
        return (Integer) getProperty(conf, MODIFIER_SIZE);
    }

    public BufferAllocator getAllocator(Properties conf)
    {
        return (BufferAllocator) getProperty(conf, ALLOCATOR);
    }


    public void setInitialSize(Configurable conf, int size)
    {
        setProperty(conf, INITIAL_SIZE, size);
    }

    public void setMaximumSize(Configurable conf, int size)
    {
        setProperty(conf, MAXIMUM_SIZE, size);
    }

    public void setStepSize(Configurable conf, int size)
    {
        setProperty(conf, STEP_SIZE, size);
    }

    public void setModifier(Configurable conf, int size)
    {
        setProperty(conf, MODIFIER_SIZE, size);
    }

    public void setAllocator(Configurable conf, BufferAllocator allocator)
    {
        setProperty(conf, ALLOCATOR, allocator);
    }

    public void setAllocator(Configurable conf, String allocator)
    {
        setProperty(conf, ALLOCATOR, allocator);
    }

    @Override
    protected TypeConverter createConverter()
    {
        return new DefaultConverter(this.getClass())
        {
            @ConvertMethod(from = String.class, to = BufferAllocator.class)
            public BufferAllocator stringToBytes(String value)
            {
                try
                {
                    return (BufferAllocator) Class.forName(value).newInstance();
                }
                catch (Exception e)
                {
                    throw new UnexpectedRuntimeException("value "+value+" can't be converted to BufferAllocator",e);
                }
            }
        };
    }
}
