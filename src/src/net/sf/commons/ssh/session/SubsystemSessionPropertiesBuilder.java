package net.sf.commons.ssh.session;

import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertyType;


/**
 * @author veentoo
 * @date 4/28/2016
 */
public class SubsystemSessionPropertiesBuilder extends ShellSessionPropertiesBuilder {

    private static volatile SubsystemSessionPropertiesBuilder instance = null;

    @PropertyType(value = String.class, required = true)
    public static final String KEY_SUBSYSTEM_NAME ="net.sf.commons.ssh.options.SubsystemSessionPropertiesBuilder.subsystemName";

    public static SubsystemSessionPropertiesBuilder getInstance()
    {
        if (instance == null)
        {
            synchronized (SubsystemSessionPropertiesBuilder.class) {
                if (instance == null) {
                    instance = new SubsystemSessionPropertiesBuilder();
                }
            }
        }
        return instance;
    }

    public String getSubsystemName(Properties opt) {
        return (String) getProperty(opt, KEY_SUBSYSTEM_NAME);
    }

    public void setSubsystemName(Configurable options, String subsystemName) {
        options.setProperty(KEY_SUBSYSTEM_NAME, subsystemName);
    }

}
