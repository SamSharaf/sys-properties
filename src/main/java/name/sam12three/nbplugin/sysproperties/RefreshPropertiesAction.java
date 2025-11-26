package name.sam12three.nbplugin.sysproperties;

import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

public class RefreshPropertiesAction extends CallableSystemAction {
    public void performAction() {
        PropertiesNotifier.getDefault().changed();
    }

    public String getName() {
        return Bundle.LBL_RefreshProps();
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
