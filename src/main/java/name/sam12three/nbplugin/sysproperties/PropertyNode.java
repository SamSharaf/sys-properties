package name.sam12three.nbplugin.sysproperties;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.actions.DeleteAction;
import org.openide.actions.NewAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;

public class PropertyNode extends AbstractNode {
    private static final String ICON_BASE_PATH = "name/sam12three/nbplugin/sysproperties/resources/";
    protected final String property;
    protected String value;
    protected List kids;
    private ChangeListener listener;
    private Sheet sheet;

    public PropertyNode(String prop, List<Children> kids) {
        super((kids.isEmpty() ? Children.LEAF : new PropertyChildren(prop)));
        this.property = prop;
        this.kids = kids;
        if (this.property != null) {
            super.setName(this.property);
            this.setDisplayName(PropertyNode.shorten(this.property));
            this.value = System.getProperty(this.property);
        } else {
            this.value = null;
        }
        this.updateShortDescription();
        this.updateIcon();
        this.listener = new ChangeListener(){

            public void stateChanged(ChangeEvent ev) {
                PropertyNode.this.fireAllChanges();
            }
        };
        PropertiesNotifier.getDefault().addChangeListener(WeakListeners.change((ChangeListener)this.listener, (Object)PropertiesNotifier.getDefault()));
    }

    private void updateShortDescription() {
        if (this.value != null) {
            this.setShortDescription(Bundle.HINT_property_name_and_value(this.getDisplayName(), this.value));
        } else {
            this.setShortDescription(this.getDisplayName());
        }
    }

    private static String shorten(String property) {
        int p = property.lastIndexOf(46);
        if (p > -1) {
            return property.substring(p + 1);
        }
        return property;
    }

    private void fireAllChanges() {
        this.value = this.property == null ? null : System.getProperty(this.property);
        this.kids = this.property == null ? SystemPropertiesNode.listAllProperties() : PropertyChildren.findSubProperties(this.property);
        this.updateIcon();
        this.updateSheet();
        this.updateShortDescription();
        this.firePropertyChange(null, null, null);
    }

    private void updateIcon() {
        String base;
        if (this.property == null) {
            base = "propertiesRoot.gif";
        } else if (!this.kids.isEmpty()) {
            base = this.value != null ? "propertyFolder.gif" : "folder.gif";
        } else {
            base = "property.gif";
        }
        this.setIconBaseWithExtension(ICON_BASE_PATH + base);
    }

    public NewType[] getNewTypes() {
        return new NewType[]{new SystemPropertyNewType(this.property)};
    }

    public Action[] getActions(boolean context) {
        return new Action[]{SystemAction.get(RenameAction.class), SystemAction.get(DeleteAction.class), null, SystemAction.get(NewAction.class), null, SystemAction.get(ToolsAction.class), SystemAction.get(PropertiesAction.class)};
    }

    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    }

    public Node cloneNode() {
        return new PropertyNode(this.property, this.kids);
    }

    protected Sheet createSheet() {
        this.sheet = super.createSheet();
        this.updateSheet();
        return this.sheet;
    }

    public void updateSheet() {
        if (this.sheet == null) {
            return;
        }
        Sheet.Set props = Sheet.createPropertiesSet();
        this.sheet.put(props);
        if (this.value != null) {
            props.put((Node.Property)new PropertySupport.Name((Node)this));
            props.put((Node.Property)new ValueProp(this.property));
        }
        Iterator it = this.kids.iterator();
        while (it.hasNext()) {
            props.put((Node.Property)new ValueProp((String)it.next()));
        }
    }

    public boolean canCopy() {
        return true;
    }

    public boolean canCut() {
        return false;
    }

    public boolean canRename() {
        if (this.value != null) {
            return DeleteChecker.isDeletable(this.property);
        }
        return false;
    }

    public void setName(String nue) {
        String old = this.getName();
        if (old.equals(nue)) {
            return;
        }
        Properties p = System.getProperties();
        String value = (String)p.remove(this.property);
        if (value != null) {
            p.setProperty(nue, value);
        }
        System.setProperties(p);
        PropertiesNotifier.getDefault().changed();
    }

    public boolean canDestroy() {
        if (this.value != null) {
            return DeleteChecker.isDeletable(this.property);
        }
        return false;
    }

    public void destroy() throws IOException {
        Properties p = System.getProperties();
        p.remove(this.property);
        System.setProperties(p);
        PropertiesNotifier.getDefault().changed();
    }

    private static class ValueProp
    extends PropertySupport.ReadWrite {
        private String property;

        public ValueProp(String property) {
            super(property, String.class, property, Bundle.HINT_value());
            this.property = property;
        }

        public Object getValue() {
            return System.getProperty(this.property);
        }

        public void setValue(Object nue) {
            System.setProperty(this.property, (String)nue);
            PropertiesNotifier.getDefault().changed();
        }
    }
}
