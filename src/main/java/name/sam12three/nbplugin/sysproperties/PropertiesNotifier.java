package name.sam12three.nbplugin.sysproperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PropertiesNotifier {
    private static PropertiesNotifier DEFAULT = null;
    private final Set listeners = new HashSet();

    public static synchronized PropertiesNotifier getDefault() {
        if (DEFAULT == null) {
            DEFAULT = new PropertiesNotifier();
        }
        return DEFAULT;
    }

    public synchronized void addChangeListener(ChangeListener listener) {
        this.listeners.add(listener);
    }

    public synchronized void removeChangeListener(ChangeListener listener) {
        this.listeners.remove(listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void changed() {
        ArrayList listeners_;
        ChangeEvent ev = new ChangeEvent(PropertiesNotifier.class);
        PropertiesNotifier propertiesNotifier = this;
        synchronized (propertiesNotifier) {
            listeners_ = new ArrayList(this.listeners);
        }
        Iterator it = listeners_.iterator();
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
}

