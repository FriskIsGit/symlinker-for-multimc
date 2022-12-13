package base;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class Config extends HashMap<String, String>{
    public static final String JSON_NAME = "config.json";
    public static final String DOT_MC_KEY = ".minecraft";
    public static final String MULTI_MC_KEY = "MultiMC";
    public static final String OVERRIDE_SYMLINKS = "overrideSymlinkInstances";

    public HashMap<String, String> config;
    public Config(){
        this.config = new HashMap<>();
    }

    private static String getExecutingDirectory(String path){
        if(path.endsWith(".jar")){
            int lastSlash = path.lastIndexOf("/");
            if(lastSlash > 0){
                path = path.substring(0, lastSlash);
            }else{
                lastSlash = path.lastIndexOf("\\");
                if(lastSlash > 0){
                    path = path.substring(0, lastSlash);
                }else{
                    throw new IllegalStateException("Path is corrupt, exiting..");
                }
            }
        }
        return path;
    }

    protected static String getBasePath(){
        try{
            return Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        }catch (URISyntaxException uriExc){
            return System.getProperty("user.dir");
        }
    }

    public static Config readConfig() throws IOException{
        String executingPath = getExecutingDirectory(getBasePath());
        System.out.println("Executing directory: " + executingPath);
        executingPath = slashifyPath(executingPath);
        String fileName = executingPath.endsWith("/") ? executingPath + JSON_NAME : executingPath + "/" + JSON_NAME;
        return readConfig(fileName);
    }
    public static Config readConfig(String configFile) throws IOException{
        FileReader reader;
        try{
            reader = new FileReader(configFile);
        }catch (FileNotFoundException e){
            throw new RuntimeException(JSON_NAME + " file not found at " + configFile);
        }
        JSONObject jsonObject;
        JSONParser parser = new JSONParser();
        try{
            jsonObject = (JSONObject) parser.parse(reader);
        }catch (ParseException e){
            throw new RuntimeException(e);
        }
        Config config = new Config();

        String dotMc = asString(jsonObject.getOrDefault(DOT_MC_KEY, ""));
        String directory = dotMc.isEmpty() ? findDotMinecraft() : dotMc;
        config.put(DOT_MC_KEY, directory);

        String multiMc = asString(jsonObject.getOrDefault(MULTI_MC_KEY, ""));
        if(multiMc.isEmpty()){
            throw new IllegalStateException("MultiMC/UltimMC directory wasn't specified in the config file");
        }
        config.put(MULTI_MC_KEY, multiMc);

        String overrideSymlinks = asString(jsonObject.getOrDefault(OVERRIDE_SYMLINKS, "false"));
        config.put(OVERRIDE_SYMLINKS, overrideSymlinks);
        reader.close();
        return config;
    }
    public static String asString(Object obj){
        if(obj instanceof String){
            return (String)obj;
        }else{
            throw new IllegalArgumentException("Should be a string");
        }
    }
    public static String findDotMinecraft() throws FileNotFoundException{
        String OS = System.getProperty("os.name");
        File dotMc;
        if(OS.startsWith("Linux")){
            dotMc = new File("/home/.minecraft");
            if(!dotMc.exists()){
                throw new FileNotFoundException(".minecraft not found");
            }
        }else if(OS.startsWith("Windows")){
            String userHome = System.getProperty("user.home");
            File userDir = new File(userHome);
            if(!userDir.exists()){
                throw new FileNotFoundException("home directory not found");
            }
            dotMc = new File(userHome + "/AppData/Roaming/.minecraft");
            if(!dotMc.exists()){
                throw new FileNotFoundException(".minecraft not found");
            }
            return dotMc.toString();
        }else{
            throw new IllegalStateException("specify .minecraft path");
        }
        return null;
    }
    public static String slashifyPath(String str){
        return str.replace('\\', '/');
    }
}
