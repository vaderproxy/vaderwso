package lib;

public class Hex {

    public String StrToHex(String str) {
        String res = "";
        for (int i = 0; i < str.length(); i++) {
            int code = str.charAt(i);
            res = res + Integer.toHexString(code);
        }
        return res;
    }

    public static String StrToUrlHex(String str) {
        StringBuffer sBuffer = new StringBuffer("");
        for (int i = 0; i < str.length(); i++) {
            int code = str.charAt(i);
            String tmp = Integer.toHexString(code);;
            sBuffer.append("%");
            if (tmp.length() < 2) {
                sBuffer.append("0");
            }
            sBuffer.append(tmp);
        }
        return sBuffer.toString();
    }

    public String formatHex(String str) {
        String res = StrToHex(str);
        res = "0x" + res;
        return res;
    }
}
