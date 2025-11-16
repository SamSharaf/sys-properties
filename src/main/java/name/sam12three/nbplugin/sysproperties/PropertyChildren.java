package name.sam12three.nbplugin.sysproperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

public class PropertyChildren extends Children.Keys {
    protected String property;
    private ChangeListener listener = null;

    public PropertyChildren(String property) {
        this.property = property;
    }

    protected void addNotify() {
        this.updateKeys();
        this.listener = new ChangeListener(){

            public void stateChanged(ChangeEvent ev) {
                PropertyChildren.this.updateKeys();
            }
        };
        PropertiesNotifier.getDefault().addChangeListener(this.listener);
    }

    protected void removeNotify() {
        if (this.listener != null) {
            PropertiesNotifier.getDefault().removeChangeListener(this.listener);
            this.listener = null;
        }
        this.setKeys(Collections.EMPTY_SET);
    }

    protected Node[] createNodes(Object key) {
        String prop = (String)key;
        if (prop.startsWith("*")) {
            prop = prop.substring(1);
        }
        return new Node[]{new PropertyNode(prop, PropertyChildren.findSubProperties(prop))};
    }

    public static List findSubProperties(String prop) {
        ArrayList<String> subprops = new ArrayList<String>();
        for (String string : System.getProperties().stringPropertyNames()) {
            if (string.startsWith("Env-") || string.startsWith("env-") || !string.startsWith(prop + ".")) continue;
            subprops.add(string);
        }
        return subprops;
    }

    private void updateKeys() {
        TreeSet<String> keys = new TreeSet<String>();
        Enumeration<?> e = System.getProperties().propertyNames();
        while (e.hasMoreElements()) {
            String prop = (String)e.nextElement();
            if (prop.startsWith("Env-") || prop.startsWith("env-") || this.property != null && !prop.startsWith(this.property + '.')) continue;
                    int idx = this.property == null ? prop.indexOf(46) : prop.indexOf(46, this.property.length() + 1);
            if (idx == -1) {
                        keys.add(prop);
                continue;
                    }
            keys.add("*" + prop.substring(0, idx));
                }
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String prop = (String)it.next();
            if (prop.startsWith("*") || !keys.contains("*" + prop)) continue;
            it.remove();
        }
            this.setKeys(keys);
    }
}

// Set<String> keys = new TreeSet<>();
// for (String prop : System.getProperties().stringPropertyNames()) {
//     final boolean isBeginningPropertyName = this.property == null || prop.startsWith(this.property + '.');
//     if (isBeginningPropertyName) {
//         if (prop.startsWith("Env-") || prop.startsWith("env-")) {
//             int idx = this.property == null ? prop.indexOf(46) : prop.indexOf(46, this.property.length() + 1);
//             if (idx != -1) {
//                 keys.add("*" + prop.substring(0, idx));
//             } else {
//                 keys.add(prop);
//             }
//         }
//     } 
// }

//        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
//            String prop = iterator.next();
//            if (prop.startsWith("*") || !keys.contains("*" + prop)) {
//                iterator.remove();
//            this.setKeys(keys);
//            }
//        }