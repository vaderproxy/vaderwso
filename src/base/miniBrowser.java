package base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;
import lib.Base64;
import struct.Config;

public class miniBrowser {

    public Cookies cookies;

    public String loc_need = "";

    public String last_reloc = "";

    public String specify_cookie;

    public int timeout_default = 60000;

    public String[] base64auth = null;

    public String uagent = null;

    public String current_url;

    public miniBrowser() {
        this.cookies = new Cookies();
    }

    public String getWPBase(String site) {
        site = site.toLowerCase();
        site = site.replaceAll("https?://(www\\.)?", "");
        int ind = site.lastIndexOf('/');
        if (ind > -1) {
            site = site.substring(0, ind);
        }
        return site;
    }

    public String getSiteBase(String site) {
        site = site.toLowerCase();
        site = site.replaceAll("https?://(www\\.)?", "");
        int ind = site.indexOf('/');
        if (ind > -1) {
            site = site.substring(0, ind);
        }
        return site;
    }

    public String getHostName(String site) {
        String host = getSiteBase(site);
        int ind = host.indexOf('.');
        if (ind > -1) {
            host = host.substring(0, ind);
        }
        return host;
    }

    public void setBase64(String b64) {
        this.base64auth = b64.split(",");
    }

    public static synchronized void wlog(Exception ex, String file) {

        if (!Config.debug_mode) {
            return;
        }

        try {
            String curDir = System.getProperty("user.dir");
            String sep = "\\";
            if (curDir.indexOf("/") != -1) {
                sep = "/";
            }
            String fname = curDir + sep + Config.debug;
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fname, true)));
            ex.printStackTrace(out);
            out.close();
        } catch (Exception ext) {
            System.out.println("I can't save log");
        }
    }

    public static synchronized void wlog(Exception ex) {
        //wlog(ex, Config.debug);
    }

    public String excutePost(String targetURL, String urlParameters, int follow) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setReadTimeout(this.timeout_default);
            connection.setConnectTimeout(this.timeout_default);
            if (urlParameters.length() > 0) {
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Length", Integer.toString((urlParameters.getBytes()).length));
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            } else {
                connection.setRequestMethod("GET");
            }
            connection.setInstanceFollowRedirects(false);
            if (this.specify_cookie != null) {
                connection.setRequestProperty("Cookie", this.specify_cookie);
            } else {
                connection.setRequestProperty("Cookie", this.cookies.cookieSTR());
            }
            if (this.uagent == null) {
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/597.36");
            } else {
                connection.setRequestProperty("User-Agent", this.uagent);
            }
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            connection.setRequestProperty("Content-Language", "en-US");
            if (this.base64auth != null) {
                String authStr = this.base64auth[0] + ":" + this.base64auth[1];
                String authEncoded = Base64.encodeToString(authStr.getBytes(), false);
                connection.setRequestProperty("Authorization", "Basic " + authEncoded);
            }
            connection.setUseCaches(false);
            connection.setDoInput(true);
            if (urlParameters.length() > 0) {
                connection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
            }
            String loc = null;
            for (int i = 1;; i++) {
                String headerName = connection.getHeaderFieldKey(i);
                String headerValue = connection.getHeaderField(i);
                if (headerName != null) {
                    if (headerName.equals("Set-Cookie")) {
                        this.cookies.setCookies(headerValue);
                    }
                    if (headerName.equals("Location")) {
                        String loctemp = headerValue;
                        String base1 = getSiteBase(targetURL);
                        if (!Pattern.matches("https?://.*?", loctemp)) {
                            if (loctemp.indexOf('/') != 0) {
                                loctemp = '/' + loctemp;
                            }
                            loctemp = "http://" + base1 + loctemp;
                        }
                        String base2 = getSiteBase(loctemp);
                        loc = loctemp;
                    }
                }
                if (headerName == null && headerValue == null) {
                    break;
                }
            }
            if (follow == -1) {
                return "";
            }
            if (follow != 0 && loc != null) {
                if (follow < 0) {
                    follow++;
                } else {
                    follow--;
                }
                this.last_reloc = loc;
                return excutePost(loc, urlParameters, follow);
            }
            if (follow < 0) {
                return "";
            }

            String curr_url = connection.getURL().toString();
            this.current_url = curr_url;
            InputStream is = null;
            try {
                is = connection.getInputStream();
            } catch (Exception e) {
                is = connection.getErrorStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append("\r\n");
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String excutePost(String targetURL, String urlParameters) {
        if (urlParameters == null) {
            urlParameters = "";
        }
        return excutePost(targetURL, urlParameters, 3);
    }

    public String getPage(String targetURL) {
        return excutePost(targetURL, "");
    }
}
