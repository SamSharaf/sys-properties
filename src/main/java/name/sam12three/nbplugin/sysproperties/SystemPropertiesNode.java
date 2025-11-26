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
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.ServiceProvider;

// Not possible to use because of usage of org.​openide.​nodes.NodeOp class in the layer.xml
//@ServiceProvider(service = Node.class, path = "UI/Runtime", position = 2020)
@Messages({
    "LBL_AllPropsNode=System Properties",
    "HINT_AllPropsNode=Shows all currently set system properties.",
    "LBL_NewProp=System Property",
    "LBL_NewProp_dialog=Create New Property",
    "MSG_NewProp_dialog_key=New property name:",
    "MSG_NewProp_dialog_value=New property value:",
    "HINT_OnePropNode=Represents one system property.",
    "HINT_value=Value of this system property.",
    "LBL_RefreshProps=Refresh",
    "# {0} - (short) name",
    "# {1} - value",
    "HINT_property_name_and_value=Value: {1}",
    "LBL_envvars_tab=Environment Vars",
    "HINT_envvars_tab=Environment variables defined in the operating system.",
    "HINT_env_value=Value of this environment variable.",
    "OpenIDE-Module-Name=System Properties Viewer",
    "OpenIDE-Module-Display-Category=Developing NetBeans",
    "OpenIDE-Module-Short-Description=Show system properties from the Java VM.",
    "OpenIDE-Module-Long-Description=Displays Java VM system properties and also system environment variables in a special node in the Runtime tab.\\nThe system properties can be edited using normal Explorer operations.\\nUseful for testing effects of system properties on the IDE's runtime operation."
})
public class SystemPropertiesNode extends PropertyNode {

    public SystemPropertiesNode() {
        super(null, SystemPropertiesNode.listAllProperties());
//        this.setName("sysprops");
        this.setDisplayName(Bundle.LBL_AllPropsNode());
        this.setShortDescription(Bundle.HINT_AllPropsNode());
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
        ss.setDisplayName(Bundle.LBL_envvars_tab());
        ss.setShortDescription(Bundle.HINT_envvars_tab());
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
            super(env, String.class, env, Bundle.HINT_env_value());
        }

        public Object getValue() {
            return System.getProperty("Env-" + this.getName());
        }
    }
}
