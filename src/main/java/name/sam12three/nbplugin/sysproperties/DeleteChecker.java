/*
 * Decompiled with CFR 0.152.
 */
package name.sam12three.nbplugin.sysproperties;

public class DeleteChecker {
    public static final String[] defaultPropertyNames = new String[]{"java.version", "java.vendor", "java.vendor.url", "java.home", "java.vm.specification.version", "java.vm.specification.vendor", "java.vm.specification.name", "java.vm.version", "java.vm.vendor", "java.vm.name", "java.specification.version", "java.specification.vendor", "java.specification.name", "java.class.version", "java.class.path", "java.ext.dirs", "os.name", "os.arch", "os.version", "file.separator", "path.separator", "line.separator", "user.name", "user.home", "user.dir"};

    public static final boolean isDeletable(String name) {
        for (int x = 0; x < defaultPropertyNames.length; ++x) {
            if (!name.equals(defaultPropertyNames[x])) continue;
            return false;
        }
        return true;
    }
}

