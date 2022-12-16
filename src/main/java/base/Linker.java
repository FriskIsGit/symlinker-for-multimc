package base;

import base.struct.DeletionResult;
import base.struct.Version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

class Linker{
    //V2V - version to version
    //M2V - mods to version
    public static final String LAUNCHER_TEMP = "_LAUNCHER_TEMP";
    public static final boolean LINK_VERSION_DIR_PRIOR_TO_1_9_0 = true;


    public static void smartSymlinkAllInstances(Path dotMcPath, Path launcherPath, boolean overrideSymlinks) {
        File[] instanceFiles = resolveInstances(launcherPath);
        for (File instance : instanceFiles){
            if(!instance.isDirectory() || instance.getName().equals(LAUNCHER_TEMP)){
                continue;
            }
            Path instanceDotMc = instance.toPath().resolve(".minecraft");
            boolean exists = Files.isDirectory(instanceDotMc);
            boolean isSymlink = Files.isSymbolicLink(instanceDotMc);
            System.out.println("Processed instance: " + instance);
            if(exists && isSymlink){
                if(!overrideSymlinks){
                    continue;
                }
                try{
                    Files.delete(instanceDotMc);
                    System.out.println("Removed .minecraft symlink for " + instance.getName());
                }catch (IOException e){
                    System.err.println("Failed to remove existing symlink");
                    throw new RuntimeException(e);
                }
            }
            if(exists && !isSymlink){
                startDeletionThreadAndAwaitFinish(instanceDotMc.toFile());
            }
            if(!instanceDotMc.toFile().mkdir()){
                System.out.println("Failed to create .minecraft dir in an instance");
                continue;
            }
            File[] rootDotMcFiles = dotMcPath.toFile().listFiles();

            boolean expectMods = true;
            assert rootDotMcFiles != null;
            for(File entry : rootDotMcFiles){
                String name = entry.getName();
                if(expectMods && name.equals("mods") && entry.isDirectory()){
                    expectMods = false;
                    //process mods on a version to version basis
                    processModsDirectory(entry, instance);
                }
                else{
                    createSymlink(instanceDotMc.resolve(name), entry.toPath());
                }
            }
        }
    }

    //this should link only those mods which correspond to an instance provided that
    //instance name starts with version name
    private static void processModsDirectory(File rootModDir, File instance){
        Path instanceDotMc = instance.toPath().resolve(".minecraft");

        File[] rootVersions = rootModDir.listFiles();
        assert rootVersions != null;
        for (File version : rootVersions){
            if(!version.isDirectory()){
                continue;
            }
            String versionName = version.getName();
            String targetVersion = instance.getName();
            if(targetVersion.startsWith(versionName)){
                if(LINK_VERSION_DIR_PRIOR_TO_1_9_0 && Version.of(versionName).releasedBefore("1.9")){
                    File instanceModDir = instanceDotMc.resolve("mods").toFile();
                    if(!instanceModDir.mkdir()){
                        System.err.println("Failed to create mods directory, skipping");
                        return;
                    }
                    Path instanceVersion = instanceModDir.toPath().resolve(versionName);
                    if(!createSymlink(instanceVersion, version.toPath())){
                        System.err.println("(V2V) Failed to symlink mods for an instance: " + instance.getName());
                    }
                }else{
                    Path instanceMods = instanceDotMc.resolve("mods");
                    if(!createSymlink(instanceMods, version.toPath())){
                        System.err.println("(M2V) Failed to symlink mods for an instance: " + instance.getName());
                    }
                }
                return;
            }
        }
        //always true
        File instanceModDir = instanceDotMc.resolve("mods").toFile();
        if(!instanceModDir.mkdir()){
            System.err.println("Failed to create mods directory for: " + instance.getName());
        }
    }

    private static File[] resolveInstances(Path launcherPath){
        Path instances = launcherPath.resolve("instances");
        boolean instancesExist = Files.isDirectory(instances);
        if(!instancesExist){
            throw new IllegalStateException("No instances directory found");
        }
        File[] instanceDirs = instances.toFile().listFiles();
        assert instanceDirs != null;
        return instanceDirs;
    }


    private static void startDeletionThreadAndAwaitFinish(File instanceDotMc){
        Thread deletionThread = new Thread(new Runnable(){
            @Override
            public void run(){
                long st = System.currentTimeMillis();
                DeletionResult result = deleteDirectoryRecursively(instanceDotMc);
                long en = System.currentTimeMillis();
                System.out.println("Deleted "
                        + result.deletedFiles + " files, "
                        + result.deletedDirectories + " directories, "
                        + "in " + (en - st) / 1000D + " seconds, "
                        + "had " + result.failures + " failures ");
            }
        });
        deletionThread.start();
        try{
            deletionThread.join();
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public static void symlinkDotMinecraftToAllInstances(Path dotMcPath, Path launcherPath, boolean overrideSymlinks) {
        File[] instanceFiles = resolveInstances(launcherPath);
        for (File instance : instanceFiles){
            if(instance.isDirectory() && !instance.getName().equals(LAUNCHER_TEMP)){
                Path instanceDotMc = instance.toPath().resolve(".minecraft");
                System.out.println("Processed instance: " + instance);

                boolean exists = Files.isDirectory(instanceDotMc);
                boolean isSymlink = Files.isSymbolicLink(instanceDotMc);
                if(exists && isSymlink){
                    if(!overrideSymlinks){
                        continue;
                    }
                    try{
                        Files.delete(instanceDotMc);
                    }catch (IOException e){
                        System.err.println("Failed to remove existing symlink");
                        throw new RuntimeException(e);
                    }
                }
                if(exists && !isSymlink){
                    startDeletionThreadAndAwaitFinish(instanceDotMc.toFile());
                }

                if(!createSymlink(instanceDotMc, dotMcPath)){
                    System.out.println("Failed to create a symlink for " + instance.getName());
                }
            }
        }
    }
    private static void deleteDirectory(File dir, DeletionResult result) throws IOException{
        File[] files = dir.listFiles();
        assert files != null;
        for(File f : files){
            Path path = f.toPath();
            if(Files.isSymbolicLink(path)){
                Files.delete(path);
                result.deletedDirectories++;
                continue;
            }
            BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
            if(basicFileAttributes.isDirectory()){
                deleteDirectory(f, result);
            }else if(basicFileAttributes.isRegularFile()){
                if(f.delete())
                    result.deletedFiles++;
                else
                    result.failures++;
            }
        }
        if(dir.delete())
            result.deletedDirectories++;
        else
            result.failures++;
    }
    //assuming rootDir is not a symlink
    //deletes files, directories and symlinks but not what they're pointing to
    //returns the number of deleted files, directories and failures,
    public static DeletionResult deleteDirectoryRecursively(File rootDir){
        DeletionResult deletionResult = new DeletionResult();
        if(rootDir == null || !rootDir.isDirectory()){
            return deletionResult;
        }
        try{
            deleteDirectory(rootDir, deletionResult);
            if(rootDir.delete())
                deletionResult.deletedDirectories++;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return deletionResult;
    }

    private static boolean createSymlink(Path link, Path original){
        try{
            Files.createSymbolicLink(link, original);
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
    public static String normalizeAndStringifyPath(String str){
        str = str.replace('\\', '/');
        StringBuilder builder = new StringBuilder(str);
        if(!str.startsWith("\"")){
            builder.insert(0, '\"');
        }
        if(!str.endsWith("\"")){
            builder.insert(builder.length(), '\"');
        }
        return builder.toString();
    }
}
