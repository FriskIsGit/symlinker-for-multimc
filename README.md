## Symlinker for multimc
<p>MultiMC or its fork UltimMC have a way of storing each minecraft release as a separate instance
 and do not give you the option to use the default .minecraft directory or to link it to an instance.
</p>
<p>Once a new instance is created it has new empty directories for screenshots, resource packs, saves, logs, etc.</p>
<p>Some users want these directories to be shared across instances to save storage on their hard drives.</p>
<p>Additionally newer Forge versions don't recognize mods located in their respective directories,
instead they're only loaded from .minecraft/mods consequently ruining backward compatibility with previous versions.</p>
<p>Symlinks exist both on Windows and Linux systems and allow you to create links which act like pointers to files located elsewhere.</p>

### This implementation allows you to smart-link the original .minecraft files to all equivalent instances
<br>**To run the program**
- download required files: `config.json`, `instanceLinker.jar`
- modify the `config.json` file by specifying the path to MultiMC/UltimMC root directory
- put the config file on the same path as the .jar file
- execute `java -jar instanceLinker.jar` from the command line or terminal
- choose the desired symlink option, the first is recommended as it's more fine-grained
- type either `1` or `2` and press enter

### External libraries used:
    json-simple

### Side notes:
- any option will undo the other and override its changes
- you can override the default auto-detected .minecraft dir in `config.json`