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
    public static final String KEY_SUBSYSTEM_NAME =
            "net.sf.commons.ssh.options.SubsystemSessionPropertiesBuilder.subsystemName";

    @PropertyType(value = Boolean.class)
    public static final String SHOULD_ALLOCATE_PSEUDO_TERMINAL =
            "net.sf.commons.ssh.options.SubsystemSessionPropertiesBuilder.pseudoTerminal";


    public static SubsystemSessionPropertiesBuilder getInstance() {
        if (instance == null) {
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

    public void enablePseudoTerminal(Configurable options, Boolean allocate) {
        options.setProperty(SHOULD_ALLOCATE_PSEUDO_TERMINAL, allocate);
    }

    public boolean shouldAllocateTerminal(Properties opt) {
         /*
        we should't allocate pseudo terminal in most of subsystem sessions. Enable pseudo terminal only in specific
        cases, e.g. you want to echo subsystem input look more:
        https://sourceforge.net/p/jsch/mailman/jsch-users/?page=119
        */
        Boolean property = (Boolean) getProperty(opt, SHOULD_ALLOCATE_PSEUDO_TERMINAL);
        return (property == null) ? false : property;
    }


}
