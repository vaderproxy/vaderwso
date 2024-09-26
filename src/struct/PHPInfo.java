package struct;

public class PHPInfo {

    public String version;
    public String os;
    public long free;

    @Override
    public String toString() {
        String r = "PHP: " + version + "\r\n";
        r += "OS: " + os+"\r\n";
        r += "Free space: " + free+" MB";
        return r;
    }

}
