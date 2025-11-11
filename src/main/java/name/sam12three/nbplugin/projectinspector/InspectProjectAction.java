package name.sam12three.nbplugin.projectinspector;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionState;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.AnnotationProcessingQuery;
import org.netbeans.api.java.queries.BinaryForSourceQuery;
import org.netbeans.api.java.queries.JavadocForBinaryQuery;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.api.java.queries.UnitTestForSourceQuery;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.ant.AntArtifactQuery;
import org.netbeans.api.project.ant.AntBuildExtender;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.CacheDirectoryProvider;
import org.netbeans.spi.project.ProjectConfiguration;
import org.netbeans.spi.project.ProjectConfigurationProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.awt.Actions;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

@ActionID(
        category = "Tools",
        id = "name.sam12three.nbplugin.projectinspector.InspectProjectAction"
)
@ActionRegistration(
        displayName = "#CTL_InspectProjectAction",
        lazy = false,
        enabledOn = @ActionState(type = Project.class, property = ActionState.NON_NULL_VALUE)
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 2000),
    @ActionReference(path = "Projects/Actions", position = 2000)
})
@Messages("CTL_InspectProjectAction=Inspect Project Metadata")
public class InspectProjectAction implements ActionListener {
    private static final String REFRESH_ICON = "name/sam12three/nbplugin/projectinspector/refresh.png";
    private final List<Project> projects;
    private final Action refreshAction = new AbstractAction("Refresh", ImageUtilities.loadImageIcon((String)"name/sam12three/nbplugin/projectinspector/refresh.png", (boolean)true)){
        {
            this.putValue("ShortDescription", "Refresh");
        }

        public void actionPerformed(ActionEvent e) {
            InspectProjectAction.this.actionPerformed(e);
        }
    };
    private InputOutput io;

    public InspectProjectAction(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RequestProcessor.getDefault().post(new Runnable(){

            public void run() {
                ProjectManager.mutex().readAccess(new Runnable(){

                    public void run() {
                        if (InspectProjectAction.this.io != null) {
                            InspectProjectAction.this.io.closeInputOutput();
                        }
                        String title = InspectProjectAction.this.projects.size() == 1 ? "Metadata: " + ProjectUtils.getInformation((Project)((Project)InspectProjectAction.this.projects.get(0))).getDisplayName() : "Project Metadata";
                        InspectProjectAction.this.io = IOProvider.getDefault().getIO(title, new Action[]{InspectProjectAction.this.refreshAction});
                        InspectProjectAction.this.io.select();
                        OutputWriter pw = InspectProjectAction.this.io.getOut();
                        try {
                            pw.reset();
                            boolean first = true;
                            for (Project p : InspectProjectAction.this.projects) {
                                if (!first) {
                                    pw.println();
                                    pw.println("-------------------------------------------");
                                    pw.println();
                                }
                                first = false;
                                InspectProjectAction.dump(p, (PrintWriter)pw);
                            }
                        }
                        catch (Exception x) {
                            x.printStackTrace((PrintWriter)pw);
                        }
                        pw.flush();
                        pw.close();
                    }
                });
            }
        });
    }

    /*
     * WARNING - void declaration
     */
    private static void dump(Project p, final PrintWriter pw) throws Exception {
        AntBuildExtender antBuildExtender;
        AntArtifact[] artifacts;
        PrivilegedTemplates pt;
        RecommendedTemplates rt;
        PropertyEvaluator eval;
        Lookup l;
        block52: {
            ProjectConfigurationProvider<ProjectConfiguration> pcp;
            CacheDirectoryProvider cdp;
            ActionProvider ap;
            SourceGroup[] rsrcGroups;
            LogicalViewProvider lvp;
            pw.println("Project: \"" + ProjectUtils.getInformation((Project)p).getDisplayName() + "\" (" + ProjectUtils.getInformation((Project)p).getName() + ")");
            pw.println("Location: " + FileUtil.getFileDisplayName((FileObject)p.getProjectDirectory()));
            pw.println("Implementation class: " + p.getClass().getName());
            l = p.getLookup();
            pw.println("Raw lookup contents:");
            for (Object o : l.lookupAll(Object.class)) {
                pw.println("  " + o);
            }
            SubprojectProvider spp = (SubprojectProvider)l.lookup(SubprojectProvider.class);
            if (spp != null && !spp.getSubprojects().isEmpty()) {
                pw.println();
                pw.println("Subprojects:");
                for (Project sp : spp.getSubprojects()) {
                    pw.println("  " + FileUtil.getFileDisplayName((FileObject)sp.getProjectDirectory()));
                }
            }
            if ((lvp = (LogicalViewProvider)l.lookup(LogicalViewProvider.class)) != null) {
                EventQueue.invokeAndWait(new Runnable(){

                    public void run() {
                        Node root = lvp.createLogicalView();
                        pw.println();
                        pw.println("Logical view:");
                        pw.println("- " + root.getDisplayName());
                        for (Node child : root.getChildren().getNodes(true)) {
                            pw.println("  + " + child.getDisplayName());
                        }
                        pw.println("Root node lookup:");
                        for (Object o : root.getLookup().lookupAll(Object.class)) {
                            pw.println("  " + o);
                        }
                        pw.println("Root node actions:");
                        for (Action a : root.getActions(false)) {
                            if (a != null) {
                                String label = (String)a.getValue("Name");
                                label = label != null ? Actions.cutAmpersand((String)label) : "???";
                                pw.println("  " + label + " [" + a.getClass().getName() + "]");
                                continue;
                            }
                            pw.println("  -----------------");
                        }
                    }
                });
            }
            Sources s = ProjectUtils.getSources((Project)p);
            pw.println();
            pw.println("Generic source roots:");
            for (SourceGroup g : s.getSourceGroups("generic")) {
                FileObject r = g.getRootFolder();
                pw.println("  \"" + g.getDisplayName() + "\" (" + g.getName() + "): " + FileUtil.getFileDisplayName((FileObject)r));
                InspectProjectAction.dumpSharability(r, p, pw, "    ");
            }
            SourceGroup[] javaGroups = s.getSourceGroups("java");
            if (javaGroups.length > 0) {
                pw.println();
                pw.println("Java source roots:");
                for (SourceGroup g : javaGroups) {
                    URL sU;
                    ClassPath classPath;
                    URL[] tests;
                    URL[] sources;
                    FileObject r = g.getRootFolder();
                    pw.println("  \"" + g.getDisplayName() + "\" (" + g.getName() + "): " + FileUtil.getFileDisplayName((FileObject)r));
                    pw.println("    source level: " + SourceLevelQuery.getSourceLevel((FileObject)r));
                    pw.println("    encoding: " + FileEncodingQuery.getEncoding((FileObject)r).displayName());
                    URL[] builtTo = BinaryForSourceQuery.findBinaryRoots((URL)r.toURL()).getRoots();
                    if (builtTo.length > 0) {
                        int var15_41 = 0;
                        pw.print("    binaries:");
                        URL[] uRLArray = builtTo;
                        int n = uRLArray.length;
                        boolean bl = false;
                        while (var15_41 < n) {
                            URL uRL = uRLArray[var15_41];
                            FileObject r2 = URLMapper.findFileObject((URL)uRL);
                            pw.print(" " + (r2 != null ? FileUtil.getFileDisplayName((FileObject)r2) : uRL));
                            ++var15_41;
                        }
                        pw.println();
                    }
                    if ((sources = UnitTestForSourceQuery.findSources((FileObject)r)).length > 0) {
                        int var16_59 = 0;
                        pw.print("    tested source roots:");
                        URL[] uRLArray = sources;
                        int n = uRLArray.length;
                        boolean bl = false;
                        while (var16_59 < n) {
                            URL u3 = uRLArray[var16_59];
                            FileObject r2 = URLMapper.findFileObject((URL)u3);
                            pw.print(" " + (r2 != null ? FileUtil.getFileDisplayName((FileObject)r2) : u3));
                            ++var16_59;
                        }
                        pw.println();
                    }
                    if ((tests = UnitTestForSourceQuery.findUnitTests((FileObject)r)).length > 0) {
                        pw.print("    test roots:");
                        for (URL u4 : tests) {
                            FileObject r2 = URLMapper.findFileObject((URL)u4);
                            pw.print(" " + (r2 != null ? FileUtil.getFileDisplayName((FileObject)r2) : u4));
                        }
                        pw.println();
                    }
                    if ((classPath = ClassPath.getClassPath((FileObject)r, (String)"classpath/source")) != null) {
                        pw.print("    classpath/source:");
                        for (FileObject r2 : classPath.getRoots()) {
                            pw.print(" " + FileUtil.getFileDisplayName((FileObject)r2));
                        }
                        pw.println();
                    }
                    for (String kind : new String[]{"classpath/compile", "classpath/execute", "classpath/boot", "classpath/endorsed", "classpath/processor"}) {
                        ClassPath classPath2 = ClassPath.getClassPath((FileObject)r, (String)kind);
                        if (classPath2 == null) continue;
                        pw.println("    " + kind + ":");
                        for (ClassPath.Entry entry : classPath2.entries()) {
                            URL[] javadoc;
                            URL uRL = entry.getURL();
                            FileObject r2 = entry.getRoot();
                            pw.println("      " + (r2 != null ? FileUtil.getFileDisplayName((FileObject)r2) : uRL.toString()));
                            SourceForBinaryQuery.Result2 sfbq = SourceForBinaryQuery.findSourceRoots2((URL)uRL);
                            FileObject[] source = sfbq.getRoots();
                            if (source.length > 0) {
                                if (sfbq.preferSources()) {
                                    pw.print("        sources (authoritative):");
                                } else {
                                    pw.print("        sources (informational):");
                                }
                                for (FileObject r3 : source) {
                                    pw.print(" " + FileUtil.getFileDisplayName((FileObject)r3));
                                }
                                pw.println();
                            }
                            if ((javadoc = JavadocForBinaryQuery.findJavadoc((URL)uRL).getRoots()).length <= 0) continue;
                            pw.print("        Javadoc:");
                            for (URL u2 : javadoc) {
                                FileObject r3 = URLMapper.findFileObject((URL)u2);
                                pw.print(" " + (r3 != null ? FileUtil.getFileDisplayName((FileObject)r3) : u2));
                            }
                            pw.println();
                        }
                    }
                    AnnotationProcessingQuery.Result result = AnnotationProcessingQuery.getAnnotationProcessingOptions((FileObject)r);
                    if (result.annotationProcessingEnabled().isEmpty()) continue;
                    pw.println("    annotation processing on " + result.annotationProcessingEnabled() + ":");
                    if (result.annotationProcessorsToRun() != null) {
                        pw.println("      processors: " + result.annotationProcessorsToRun());
                    }
                    if ((sU = result.sourceOutputDirectory()) != null) {
                        FileObject sF = URLMapper.findFileObject((URL)sU);
                        pw.println("      source output: " + (sF != null ? FileUtil.getFileDisplayName((FileObject)sF) : sU));
                    }
                    pw.println("      options: " + result.processorOptions());
                }
            }
            if ((rsrcGroups = s.getSourceGroups("resources")).length > 0) {
                pw.println();
                pw.println("Java resource roots:");
                for (SourceGroup g : rsrcGroups) {
                    FileObject r = g.getRootFolder();
                    pw.println("  \"" + g.getDisplayName() + "\" (" + g.getName() + "): " + FileUtil.getFileDisplayName((FileObject)r));
                }
            }
            if ((ap = (ActionProvider)l.lookup(ActionProvider.class)) != null) {
                pw.println();
                pw.println("Actions:");
                for (String cmd : new TreeSet<String>(Arrays.asList(ap.getSupportedActions()))) {
                    pw.println("  " + cmd);
                }
            }
            if ((cdp = (CacheDirectoryProvider)l.lookup(CacheDirectoryProvider.class)) != null) {
                pw.println();
                pw.println("Cache directory: " + FileUtil.getFileDisplayName((FileObject)cdp.getCacheDirectory()));
            }
            if ((pcp = (ProjectConfigurationProvider)l.lookup(ProjectConfigurationProvider.class)) != null) {
                pw.println();
                pw.println("Configurations:");
                for (ProjectConfiguration cfg : pcp.getConfigurations()) {
                    pw.print("  " + cfg.getDisplayName());
                    if (cfg.equals(pcp.getActiveConfiguration())) {
                        pw.print(" [active]");
                    }
                    pw.println();
                }
            }
            if ((eval = (PropertyEvaluator)p.getLookup().lookup(PropertyEvaluator.class)) != null) {
                pw.println();
                pw.println("Properties (from PropertyEvaluator found in lookup):");
            }
            if (eval == null) {
                try {
                    for (Field projectConfiguration : p.getClass().getDeclaredFields()) {
                        if (!PropertyEvaluator.class.isAssignableFrom(projectConfiguration.getType())) continue;
                        projectConfiguration.setAccessible(true);
                        eval = (PropertyEvaluator)projectConfiguration.get(p);
                        pw.println();
                        pw.println("Properties (from field " + projectConfiguration.getName() + "):");
                        break;
                    }
                    if (eval != null) break block52;
                    for (Method method : p.getClass().getDeclaredMethods()) {
                        if (!PropertyEvaluator.class.isAssignableFrom(method.getReturnType()) || method.getParameterTypes().length != 0) continue;
                        method.setAccessible(true);
                        eval = (PropertyEvaluator)method.invoke((Object)p, new Object[0]);
                        pw.println();
                        pw.println("Properties (from method " + method.getName() + "()):");
                        break;
                    }
                }
                catch (Exception x) {
                    Exceptions.printStackTrace((Throwable)x);
                }
            }
        }
        if (eval != null) {
            Map<String, String> props = eval.getProperties();
            if (props != null) {
                for (Map.Entry<String, String> entry : new TreeMap<>(props).entrySet()) {
                    pw.println("  " + (String)entry.getKey() + "=" + (String)entry.getValue());
                }
            } else {
                pw.println("  <unknown>");
            }
        }
        if ((rt = (RecommendedTemplates)l.lookup(RecommendedTemplates.class)) != null) {
            int var15_51 = 0;
            pw.println();
            pw.println("Recommended template categories:");
            String[] sources = rt.getRecommendedTypes();
            int entry = sources.length;
            boolean bl = false;
            while (var15_51 < entry) {
                String string = sources[var15_51];
                pw.println("  " + string);
                ++var15_51;
            }
        }
        if ((pt = (PrivilegedTemplates)l.lookup(PrivilegedTemplates.class)) != null) {
            int var16_66 = 0;
            pw.println();
            pw.println("Recommended templates:");
            String[] entry = pt.getPrivilegedTemplates();
            int n = entry.length;
            boolean bl = false;
            while (var16_66 < n) {
                String template = entry[var16_66];
                pw.print("  " + template);
                final FileObject fo = FileUtil.getConfigFile((String)template);
                if (fo != null) {
                    final DataObject d = DataObject.find((FileObject)fo);
                    EventQueue.invokeAndWait(new Runnable(){

                        public void run() {
                            String displayName = d.getNodeDelegate().getDisplayName();
                            if (!displayName.equals(fo.getName())) {
                                pw.print(" (\"" + displayName + "\")");
                            }
                        }
                    });
                }
                pw.println();
                ++var16_66;
            }
        }
        if ((artifacts = AntArtifactQuery.findArtifactsByType((Project)p, (String)"jar")).length > 0) {
            pw.println();
            pw.println("Ant artifacts (build products):");
            for (AntArtifact aa : artifacts) {
                pw.println("  " + aa.getID() + " (" + aa.getScriptLocation() + "#" + aa.getTargetName() + " or #" + aa.getCleanTargetName() + ")");
                for (URI uRI : aa.getArtifactLocations()) {
                    pw.println("    " + uRI);
                }
            }
        }
        if ((antBuildExtender = (AntBuildExtender)l.lookup(AntBuildExtender.class)) != null) {
            pw.println();
            pw.println("Extensible build script targets:");
            for (String target : antBuildExtender.getExtensibleTargets()) {
                pw.println("  " + target);
            }
        }
    }

    private static void dumpSharability(FileObject fo, Project p, PrintWriter pw, String prefix) {
        pw.print(prefix + fo.getNameExt());
        Project owner = FileOwnerQuery.getOwner((FileObject)fo);
        if (owner != p) {
            pw.println(" (different owner project: " + (owner != null ? ProjectUtils.getInformation((Project)owner).getDisplayName() : "none") + ")");
            return;
        }
        switch (SharabilityQuery.getSharability((FileObject)fo)) {
            case MIXED: {
                pw.println();
                if (!fo.isFolder()) break;
                FileObject[] kids = fo.getChildren();
                Arrays.sort(kids, new Comparator<FileObject>(){

                    @Override
                    public int compare(FileObject fo1, FileObject fo2) {
                        return fo1.getNameExt().compareTo(fo2.getNameExt());
                    }
                });
                for (FileObject kid : kids) {
                    if (!VisibilityQuery.getDefault().isVisible(kid)) continue;
                    InspectProjectAction.dumpSharability(kid, p, pw, prefix + "  ");
                }
                break;
            }
            case NOT_SHARABLE: {
                pw.println(" (not sharable)");
                break;
            }
            case SHARABLE: {
                pw.println();
                break;
            }
            case UNKNOWN: {
                pw.println(" (sharability unknown)");
            }
        }
    }
}
