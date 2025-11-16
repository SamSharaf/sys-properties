package name.sam12three.nbplugin.sysproperties;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.actions.NewAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.ServiceProvider;

// Not possible to use because of usage of org.​openide.​nodes.NodeOp class in the layer.xml
//@ServiceProvider(service = Node.class, path = "UI/Runtime", position = 2020)
public class SystemPropertiesNode extends PropertyNode {

    public SystemPropertiesNode() {
        super(null, SystemPropertiesNode.listAllProperties());
//        this.setName("sysprops");
        this.setDisplayName(NbBundle.getMessage(SystemPropertiesNode.class, (String)"LBL_AllPropsNode"));
        this.setShortDescription(NbBundle.getMessage(SystemPropertiesNode.class, (String)"HINT_AllPropsNode"));
    }

    public static List listAllProperties() {
        ArrayList<String> l = new ArrayList<String>();
        for (String string : System.getProperties().stringPropertyNames()) {
            if (string.startsWith("Env-") || string.startsWith("env-")) continue;
            l.add(string);
        }
        return l;
    }

    public Action[] getActions(boolean context) {
        return new Action[]{SystemAction.get(RefreshPropertiesAction.class), null, SystemAction.get(NewAction.class), null, SystemAction.get(ToolsAction.class), null, SystemAction.get(PropertiesAction.class)};
    }

    public Node cloneNode() {
        return new SystemPropertiesNode();
    }

    protected Sheet createSheet() {
        Sheet s = super.createSheet();
        Sheet.Set ss = new Sheet.Set();
        ss.setName("envvars");
        ss.setDisplayName(NbBundle.getMessage(SystemPropertiesNode.class, (String)"LBL_envvars_tab"));
        ss.setShortDescription(NbBundle.getMessage(SystemPropertiesNode.class, (String)"HINT_envvars_tab"));
        for (String string : System.getProperties().stringPropertyNames()) {
            if (!string.startsWith("Env-")) continue;
            String env = string.substring(4);
            ss.put((Node.Property)new EnvVarProp(env));
        }
        s.put(ss);
        return s;
    }

    public String getName() {
        return "SystemPropertiesNode";
    }

    public boolean canRename() {
        return false;
    }

    private static final class EnvVarProp
    extends PropertySupport.ReadOnly {
        public EnvVarProp(String env) {
            super(env, String.class, env, NbBundle.getMessage(SystemPropertiesNode.class, (String)"HINT_env_value"));
        }

        public Object getValue() {
            return System.getProperty("Env-" + this.getName());
        }
    }
}

