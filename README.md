## Symlinker for multimc
<p>MultiMC or its fork UltimMC have a way of storing each minecraft release as a separate instance
 and do not give you the option to use the default .minecraft directory or to link it to an instance.
</p>
<p>Once a new instance is created it has new empty directories for screenshots, resource packs, saves, logs, etc.</p>
<p>Some users want these directories to be shared across instances to save storage on their hard drives.</p>
<p>Symlinks exist both on Windows and Linux systems and allow you to create links which act like pointers to files located elsewhere.</p>

### For now this implementation allows you to link the original .minecraft directory to all instances
<br>**To run the program**
- modify the `config.json` file by specifying the path to MultiMC/UltimMC root directory
- put the config file on the same level as the .jar file
- execute `java -jar instanceLinker.jar`

### Libraries used:
    json-simple