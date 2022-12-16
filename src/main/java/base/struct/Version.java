package base.struct;

import java.util.Arrays;

public class Version{
    public final String v;
    private static final int MAX_LENGTH = 7;
    private static final int MIN_LENGTH = 3;
    public Version(String v){
        this.v = v;
    }
    public static Version of(String v){
        return new Version(v);
    }

    /**
     * Determines which version was released before.
     * If it cannot be determined returns false.
     * Valid version formats include:
     * <p>0.00.00, 0.0.0, 0.00, 0.0 </p>
     * Additionally, if any version name is invalid or custom - returns false
     */
    public boolean releasedBefore(Version other){
        return releasedBefore(other.v);
    }
    public boolean releasedBefore(String other){
        String version = this.v;
        if (version.length() > MAX_LENGTH || other.length() > MAX_LENGTH){
            return false;
        }
        if (version.length() < MIN_LENGTH || other.length() < MIN_LENGTH){
            return false;
        }
        String[] versionParts = version.split("\\.", 3);
        String[] otherParts = other.split("\\.", 3);
        int versionLen = versionParts.length, otherLen = otherParts.length;
        int len = Math.min(versionLen, otherLen);
        for(int i = 0; i < len; i++){
            int num1 = castToInt(versionParts[i]);
            int num2 = castToInt(otherParts[i]);
            if(num1 < 0 || num2 < 0){
                return false;
            }
            if(num1 < num2){
                return true;
            }
            if(num1 > num2){
                return false;
            }
        }
        return versionLen < otherLen;
    }

    private static int castToInt(String strNum){
        try{
            return Integer.parseInt(strNum);
        }catch (NumberFormatException nfExc){
            return -1;
        }
    }
}
