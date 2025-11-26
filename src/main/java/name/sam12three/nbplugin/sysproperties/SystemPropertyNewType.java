package name.sam12three.nbplugin.sysproperties;

import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.datatransfer.NewType;

class SystemPropertyNewType
extends NewType {
    private String propertyName = null;

    public SystemPropertyNewType(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getName() {
        return Bundle.LBL_NewProp();
    }

    public void create() throws IOException {
        String title = Bundle.LBL_NewProp_dialog();
        String msg = Bundle.MSG_NewProp_dialog_key();
        NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine(msg, title);
        if (this.propertyName != null) {
            desc.setInputText(this.propertyName + ".");
        }
        if (!DialogDisplayer.getDefault().notify((NotifyDescriptor)desc).equals(NotifyDescriptor.OK_OPTION)) {
            return;
        }
        String key = desc.getInputText();
        msg = Bundle.MSG_NewProp_dialog_value();
        desc = new NotifyDescriptor.InputLine(msg, title);
        if (!DialogDisplayer.getDefault().notify((NotifyDescriptor)desc).equals(NotifyDescriptor.OK_OPTION)) {
            return;
        }
        String value = desc.getInputText();
        System.setProperty(key, value);
        PropertiesNotifier.getDefault().changed();
    }
}
