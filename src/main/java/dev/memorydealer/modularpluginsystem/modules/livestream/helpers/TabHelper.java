package dev.memorydealer.modularpluginsystem.modules.livestream.helpers;

import me.neznamy.tab.api.TabPlayer;

import java.lang.reflect.Method;

public class TabHelper {

    private static final Class<?>  enumClass;           // the enum type
    private static final Object    TABPREFIX;           // enum constant
    private static final Object    TAGPREFIX;
    private static final Method    setValue;            // TabPlayer#setValue(enum,String)

    static {
        Class<?> tmp = null;  Object tpre = null, tagpre = null;  Method m = null;

        try { // TAB 3.x
            tmp = Class.forName("me.neznamy.tab.api.PropertyType");
        } catch (ClassNotFoundException ignored) {
            try { // TAB 4.x
                tmp = Class.forName("me.neznamy.tab.api.TabConstants$Property");
            } catch (ClassNotFoundException ignored2) {/* no TAB */}
        }
        if (tmp != null) {
            try {
                tpre = Enum.valueOf((Class<Enum>) tmp, "TABPREFIX");
                tagpre = Enum.valueOf((Class<Enum>) tmp, "TAGPREFIX");
                m = TabPlayer.class.getMethod("setValue", tmp, String.class);
            } catch (Exception ignored) { tmp = null; }
        }
        enumClass = tmp;
        TABPREFIX = tpre;
        TAGPREFIX = tagpre;
        setValue  = m;
    }

    public static boolean active() { return enumClass != null && setValue != null; }

    public static void setPrefix(TabPlayer tp, String text) {
        if (!active()) return;
        try {
            setValue.invoke(tp, TABPREFIX, text);
            setValue.invoke(tp, TAGPREFIX, text);
        } catch (Exception ignored) { }
    }
    public static void clear(TabPlayer tp){ setPrefix(tp, ""); }
}