package base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) throws IOException{
        Config config = Config.readConfig();
        String dotMc = config.get(Config.DOT_MC_KEY);
        String launcher = config.get(Config.MULTI_MC_KEY);
        File dotMcFile = new File(dotMc);
        File launcherFile = new File(launcher);
        if(dotMcFile.exists() && launcherFile.exists()){
            System.out.println(".minecraft and multimc found");
        }
        Path dotMcPath = dotMcFile.toPath();
        Path launcherPath = launcherFile.toPath();
        boolean overrideSymlinks = Boolean.parseBoolean(config.get(Config.OVERRIDE_SYMLINKS));

        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.println("What to do? (1/2), other inputs will cause an exit..");
        System.out.println(formQueryToSmartSymlink(overrideSymlinks));
        System.out.println();
        System.out.println(formQueryToSymlinkAll(overrideSymlinks));
        String response = scanner.nextLine();

        if(response.startsWith("1")){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    long st = System.currentTimeMillis();
                    Linker.smartSymlinkAllInstances(dotMcPath, launcherPath, overrideSymlinks);
                    long en = System.currentTimeMillis();
                    System.out.println("Completed in " + (en-st)/1000D + " seconds");
                }
            }).start();
        }
        else if(response.startsWith("2")){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    long st = System.currentTimeMillis();
                    Linker.symlinkDotMinecraftToAllInstances(dotMcPath, launcherPath, overrideSymlinks);
                    long en = System.currentTimeMillis();
                    System.out.println("Completed in " + (en-st)/1000D + " seconds");
                }
            }).start();
        }
        else{
            System.out.println("Aborting");
            System.exit(0);
        }
    }

    private static String formQueryToSmartSymlink(boolean overrideSymlinks){
        StringBuilder query = new StringBuilder("#1 Symlink every file or directory in .minecraft\n");
        query.append("to every file or directory inside {version}/.minecraft (instance dir)\n");
        query.append("aside from ./minecraft/mods directory\n");
        query.append("which will be linked on a version to version basis\n");
        query.append("[it will delete .minecraft inside all instances ");
        if(overrideSymlinks){
            query.append("including ");
        }else{
            query.append("excluding ");
        }
        query.append("existing symlinks]?");
        return query.toString();
    }

    private static String formQueryToSymlinkAll(boolean overrideSymlinks){
        StringBuilder query = new StringBuilder("#2 Make .minecraft soft links(symlinks) to all instances\n");
        query.append("[it will delete .minecraft inside all instances ");
        if(overrideSymlinks){
            query.append("including ");
        }else{
            query.append("excluding ");
        }
        query.append("existing symlinks]?");
        return query.toString();
    }
}