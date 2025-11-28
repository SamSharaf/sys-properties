
``` bash
$ JAVA_HOME=$(/usr/libexec/java_home -v 17) gradle native2ascii 
$ mv ./build/native2ascii/Bundle_ar.properties ./Bundle_ar.properties 
```

VS Code doesn’t rewrite Arabic text on its own, but certain settings and extensions can make it save characters using \uXXXX escapes when writing .properties files. That usually happens because:

- File/encoding setting: If the file is treated as Latin-1, VS Code (or a plugin) may convert non-ASCII characters to escapes so the file stays ASCII-compatible.
- Java properties extension: Some extensions force Unicode escapes on save to match the old native2ascii expectation for .properties files.
- Format-on-save task: If you run a tool (e.g., native2ascii, msgcat, certain build tasks) via save hooks, they might rewrite the file as escaped text.
To avoid it, save the file with UTF-8 encoding and disable any extension or task that auto-converts to escaped form. For Java properties, modern JVMs load UTF-8 fine, so keeping the text readable is usually safe unless you need strict ASCII output.


### how to save the file with UTF-8 encoding?
In VS Code you can pick the encoding per file. For UTF‑8:

1. Open the file and click the status bar item that shows the current encoding (e.g. “UTF-8” or “ISO-8859-1”).
2. Choose Save with Encoding….
3. Select UTF-8 and confirm. VS Code rewrites the file using UTF‑8.

If the encoding indicator isn’t visible, press Ctrl/Cmd + Shift + P, run Change File Encoding, choose Save with Encoding, and pick UTF-8. To make UTF‑8 the default, set "files.encoding": "utf8" in your workspace settings.json.


### I saved it with utf-8 encoding but it displays in mojibake inside netbeans
NetBeans still shows mojibake because the platform loads Bundle*.properties through the standard ResourceBundle, which assumes ISO‑8859‑1. Even if you save the file as UTF‑8, NetBeans interprets the bytes as Latin‑1 and you see garbled text.

Options:

Keep the file UTF‑8 but convert non‑ASCII characters to \uXXXX escapes (e.g. via native2ascii or VS Code’s command palette). This preserves readable text at runtime even though the file looks escaped; that’s the only way NetBeans will render it correctly without code changes.

### For example:
```bash
$ native2ascii -encoding UTF-8 ./Bundle_ar.properties > /tmp/Bundle_ar.ascii
```
```bash
$ native2ascii -encoding UTF-8 ./Bundle_ar.properties ./Bundle_ar.properties.native && mv ./Bundle_ar.properties.native ./Bundle_ar.properties
```
Make sure the -encoding flag matches the current file encoding (UTF‑8). After conversion the file will contain \u0639\u0631\u0636 instead of literal Arabic text, which NetBeans’ ResourceBundle loader will interpret correctly.