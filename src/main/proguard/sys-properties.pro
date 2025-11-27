# Keep NetBeans entry points and APIs that are discovered via reflection or
# service lookup. We start with shrink+optimize only, so this file is ready for
# incremental obfuscation later.

# Preserve NetBeans entry points that are referenced via generated layer files
# or manifest attributes.
-keep class name.sam12three.nbplugin.sysproperties.Installer { *; }
-keep class name.sam12three.nbplugin.sysproperties.Installer$* { *; }
-keep class name.sam12three.nbplugin.sysproperties.*Action { *; }

# NetBeans loads nodes declared in layer.xml from the base package, so keep the
# classes directly under it and under its subpackages remain unobfuscated.
-keep class name.sam12three.nbplugin.sysproperties.** { *; }

# Keep generated bundle classes that are referenced indirectly.
-keep class name.sam12three.nbplugin.sysproperties.Bundle { *; }

# Keep NetBeans lookup helpers since the runtime loads them reflectively.
-keep class org.openide.util.NbBundle$Messages { *; }

# Retain annotations and metadata that NetBeans uses for registration.
-keepattributes *Annotation*,InnerClasses,EnclosingMethod,Signature
