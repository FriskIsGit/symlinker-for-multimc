package base.struct;

public class VersionTest{
    public static int success, fail;
    public static void main(String[] args){
        runAndValidate("1.9.2", "1.9",   false);
        runAndValidate("1.7.2", "1.9",   true);
        runAndValidate("1.7", "1.9.2",   true);
        runAndValidate("1.9.2", "1.7",   false);
        runAndValidate("1.9", "1.9.2",   true);
        runAndValidate("0.3",   "0.9",   true);
        runAndValidate("0.3.9", "0.4.9", true);
        runAndValidate("1.9.2", "1.9",   false);
        runAndValidate("1.8",   "1.8",   false);
        runAndValidate("1.8.2", "1.8.7", true);
        runAndValidate("1.8.2", "1.9.2", true);
        runAndValidate("1.8.9", "1.9", true);
        runAndValidate("1.9", "1.8.9", false);
        runAndValidate("1.8.2", "1.19.2", true);
        runAndValidate("1.8.2", "1.18",  true);
        runAndValidate("1.5.2", "1.22",  true);
        runAndValidate("1.7",   "1.17",  true);
        runAndValidate("1.8",   "1.19.2", true);
        runAndValidate("1.16",  "1.19.2", true);
        runAndValidate("1.16.2","1.16.2", false);
        runAndValidate("1.14.2","1.14.1", false);
        runAndValidate("1.12.2","1.12.3", true);
        runAndValidate("1.9",   "1.90", true);
        //invalid
        runAndValidate("1..9",   "1.19.2", false);
        runAndValidate("..",     "1.19.2", false);
        runAndValidate(".3.9",   ".4.9",   false);
        runAndValidate("m",      "1.19.2", false);
        runAndValidate("1.-22.9","1.19.2", false);
        System.out.println("Successful/All : " + success + "/" + (success+fail));
    }
    public static void runAndValidate(String v, String other, boolean expected){
        boolean actual = Version.of(v).releasedBefore(other);
        if(actual == expected){
            success++;
        }
        else{
            System.err.println("Determined that " + other + " released before " + v);
            fail++;
        }
    }
}
