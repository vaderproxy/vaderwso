package struct;

import java.util.HashMap;
import java.util.Map;

public class WSOQuery {

    public String url;
    public HashMap<String, String> post = new HashMap();
    public HashMap<String, String> cookie = new HashMap();

    public String cookieAsString() {
        String result = "";
        for (Map.Entry<String, String> entry : cookie.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!result.isEmpty()) {
                result += "; ";
            }

            result += key + "=" + value;

        }

        return result;
    }

    public boolean hasPost() {
        return !this.post.isEmpty();
    }

    public boolean hasCookie() {
        return !this.cookie.isEmpty();

    }
}
