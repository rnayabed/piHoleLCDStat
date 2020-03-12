package piHoleLCDStat;

public class programInfo {
    final static String VERSION = "2.2";
    final static String AUTHOR = "Debayan Sutradhar";
    final static String REPO_LINK = "https://github.com/dubbadhar/piHoleLCDStat/";
    final static String BUILD_DATE = "9th March 2020";

    public static void main(String[] args)
    {
        System.out.println("piHoleLCDStat"+
                "\nAuthor : "+AUTHOR+
                "\nVersion : "+VERSION+
                "\nSource : "+REPO_LINK+
                "\nBuild : "+BUILD_DATE+
                "\nJava Version : "+System.getProperty("java.version")+
                "\nJavaFX Version : "+System.getProperty("javafx.version"));
    }
}
