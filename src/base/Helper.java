package base;

import com.sun.jna.platform.FileUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import org.apache.commons.codec.binary.Base64;
import struct.Config;

public class Helper {

    public static String readFile(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = "\r\n";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String replace(String in, String ths, String that) {
        StringBuilder sb = new StringBuilder(in);
        int idx = sb.indexOf(ths);
        while (idx > -1) {
            sb.replace(idx, idx + ths.length(), that);
            idx = sb.indexOf(ths);
        }

        return sb.toString();
    }

    public static ArrayList<String> fileToArrWithDuplicates(String filename) throws IOException {
        List<String> strings = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        ArrayList<String> res = new ArrayList();
        int i = 1;
        String line;
        while ((line = reader.readLine()) != null) {
            if (res.contains(line));
            line = line.trim();
            if (!line.equals("")) {
                res.add(line);
            }
            i++;
        }
        return res;
    }

    public static ArrayList<String> fileToArray(String filename) throws IOException {
        List<String> strings = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        ArrayList res = new ArrayList();
        int i = 1;
        while ((line = reader.readLine()) != null) {
            if (res.contains(line)) {
                continue;
            }
            line = line.trim();
            if (!line.equals("")) {
                res.add(line);
            }
            /*
            if ((i % 1000) == 0) {
                Helper.write("line = " + i);
            }
             */
            i++;
        }
        return res;

    }

    public static String arrayToString(List arr) {
        StringBuffer rtxt = new StringBuffer();
        int asize = arr.size();
        for (int i = 0; i < asize; i++) {
            if (i > 0) {
                rtxt.append("\r\n");
            }
            //i++;
            try {
                rtxt.append(arr.get(i));
            } catch (Exception ex) {
                rtxt.toString();
            }
        }
        return rtxt.toString();
    }

    public static void strToFile(String file, String str) {
        try {
            File newTextFile = new File(file);
            FileWriter fw = new FileWriter(newTextFile, !true);
            fw.write(str);
            fw.close();

        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    public static void write(String text) {
        System.out.println(text);
    }

    public static void error(String text) {
        System.out.println(text);
        System.exit(0);
    }

    public static void delay(long d) {
        try {
            Thread.sleep(d);
        } catch (InterruptedException ex) {

        }
    }

    public static int getInt(String str, int def) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return def;
        }
    }

    public static void log(String text) {
        Base.wso_frame.logs.append(text + "\r\n");
        Base.wso_frame.logs.setCaretPosition(Base.wso_frame.logs.getDocument().getLength() - 1);
    }

    public static void log(int text) {
        Base.wso_frame.logs.append(text + "\r\n");
        Base.wso_frame.logs.setCaretPosition(Base.wso_frame.logs.getDocument().getLength() - 1);

    }

    public static void log_inline(String text) {
        Base.wso_frame.logs.append(text);
        Base.wso_frame.logs.setCaretPosition(Base.wso_frame.logs.getDocument().getLength() - 1);

    }

    public static HashMap<String, String> parse_query_string(String post) {
        HashMap<String, String> post_map = new HashMap();
        String[] param_list = post.split("&");
        for (int i1 = 0; i1 < param_list.length; i1++) {
            String pv = param_list[i1];
            String[] pv_arr = pv.split("=");
            if (pv_arr.length != 2) {
                continue;
            }
            try {
                post_map.put(pv_arr[0], java.net.URLDecoder.decode(pv_arr[1], StandardCharsets.UTF_8.name()));
            } catch (Exception e) {
                // not going to happen - value came from JDK's own StandardCharsets
            }
        }
        return post_map;
    }

    public static String generate_post_code(HashMap<String, String> post_map) {
        String result = "";
        for (Map.Entry<String, String> entry : post_map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String enc = "";
            try {
                enc = new String(Base64.encodeBase64(value.getBytes()), "UTF8");
            } catch (Exception ex) {
                //Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
            }

            String one = "$_POST['" + key + "']=base64_decode('" + enc + "');";
            result += one;
        }

        return result;
    }

    public static String replaceOnce(String in, String ths, String that) {
        StringBuilder sb = new StringBuilder(in);
        int idx = sb.indexOf(ths);
        if (idx > -1) {
            sb.replace(idx, idx + ths.length(), that);
            idx = sb.indexOf(ths);
        }

        return sb.toString();

    }

    public static String encodeFileToBase64Binary(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        byte[] data = Files.readAllBytes(path);
        File file = new File(fileName);
        byte[] encoded = Base64.encodeBase64(data);
        return new String(encoded, StandardCharsets.US_ASCII);
    }

}
