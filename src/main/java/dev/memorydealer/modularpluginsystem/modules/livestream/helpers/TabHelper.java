package dev.memorydealer.modularpluginsystem.modules.livestream.helpers;

import me.neznamy.tab.api.TabPlayer;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;

public class TabHelper {

    /* is the "TAB" plugin loaded? */
    private static final boolean tabPresent =
            Bukkit.getPluginManager().getPlugin("TAB") != null;

    /* Resolved at runtime (null if not found) */
    private static final Class<?>  enumClass;
    private static final Object    TABPREFIX;
    private static final Object    TAGPREFIX;
    private static final Method    applyMethod;    // setTemporaryValue / setValueTemporarily / setValue

    static {
        Class<?> eClass = null;
        Object   tabPre = null;
        Object   tagPre = null;
        Method   method = null;

        if (tabPresent) {
            // 1) locate the enum class (PropertyType or TabConstants$Property)
            try { eClass = Class.forName("me.neznamy.tab.api.PropertyType"); }          // TAB 3.x
            catch (ClassNotFoundException ignored) {
                try { eClass = Class.forName("me.neznamy.tab.api.TabConstants$Property"); }  // TAB 4/5
                catch (ClassNotFoundException ignored2) {/* still null */}
            }

            // 2) resolve constants & method
            if (eClass != null) {
                try {
                    tabPre = Enum.valueOf((Class<Enum>) eClass, "TABPREFIX");
                    tagPre = Enum.valueOf((Class<Enum>) eClass, "TAGPREFIX");

                    // look for any of the 3 method names
                    String[] names = {"setTemporaryValue", "setValueTemporarily", "setValue"};
                    for (String n : names) {
                        try {
                            method = TabPlayer.class.getMethod(n, eClass, String.class);
                            break;
                        } catch (NoSuchMethodException ignored) {}
                    }
                    if (method == null) eClass = null;  // fail -> mark inactive
                } catch (Exception ex) {
                    eClass = null;
                }
            }
        }
        enumClass  = eClass;
        TABPREFIX  = tabPre;
        TAGPREFIX  = tagPre;
        applyMethod= method;
    }

    /** @return true when TAB is loaded *and* reflection succeeded */
    public static boolean active() { return enumClass != null && applyMethod != null; }

    /** Apply both TABPREFIX & TAGPREFIX */
    public static void setPrefix(TabPlayer tp, String prefix) {
        if (!active()) return;
        try {
            applyMethod.invoke(tp, TABPREFIX, prefix);
            applyMethod.invoke(tp, TAGPREFIX, prefix);
        } catch (Throwable ignored) { }
    }

    /** Clear prefixes */
    public static void clear(TabPlayer tp) { setPrefix(tp, ""); }

    private TabHelper() {}  // util
}