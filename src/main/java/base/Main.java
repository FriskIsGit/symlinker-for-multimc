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

        System.out.println(formQuery(overrideSymlinks));
        if(!scanner.nextLine().startsWith("y")){
            System.out.println("Aborting");
            System.exit(0);
        }

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

    private static String formQuery(boolean overrideSymlinks){
        StringBuilder query = new StringBuilder("Make .minecraft soft links(symlinks) to all instances ");
        query.append("[it will delete .minecraft inside all instances ");
        if(overrideSymlinks){
            query.append("including ");
        }else{
            query.append("excluding ");
        }
        query.append("existing symlinks]? (y/)");
        return query.toString();
    }
}