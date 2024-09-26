package vaderwso;

import base.*;
import struct.*;

public class VaderWSO {

    public static void main(String[] args) throws Exception {

        String curDir = System.getProperty("user.dir");
        String sep = "\\";
        if (curDir.indexOf("/") != -1) {
            sep = "/";
        }
        IniFile ifile = null;
        try {
            String iname = "wso.ini";
            if (args.length > 0) {
                iname = args[0];
            }
            ifile = new IniFile(curDir + sep + iname);
            Config.uagent = ifile.getString("main", "uagent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/1488.1");

            Config.use_proxy = ifile.getString("main", "use_proxy", "n").toLowerCase().equals("y");
            Config.proxy_host = ifile.getString("main", "proxy_host", "127.0.0.1");
            Config.proxy_port = ifile.getString("main", "proxy_port", "9150");

            Config.wso_index = ifile.getString("main", "wso_index", "wso_index");
            Config.wso_code = ifile.getString("main", "wso_code", "wso_code");
            Config.notepad = ifile.getString("main", "notepad", "notepad");
            Config.pyFile = ifile.getString("main", "py_file", "base64.py");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        if (Config.use_proxy) {
            System.setProperty("socksProxyHost", Config.proxy_host);
            System.setProperty("socksProxyPort", Config.proxy_port);
        }

        Base base = new Base();
        base.run();
    }

}
