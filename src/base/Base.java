package base;

import forms.WSOFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;
import struct.*;

public class Base {


    public static WSOFrame wso_frame;

    public Base() {

        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    //No need to implement.
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    //No need to implement.
                }
            }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void resetFiles() {
        WSOFrame wso = wso_frame;
        wso.file_manager.setRowHeight(40);
        DefaultTableModel tm = new DefaultTableModel(new Object[0][], new Object[]{"#", "File", "Size", "Permissions", "Modified"}) {
            @Override
            public Class<?> getColumnClass(int col) {

                //here it really returns the right column class (Integer.class)
                if (col == 0) {
                    return Icon.class;
                }
                return String.class;
            }

            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        wso.file_manager.setModel(tm);

        wso.file_manager.getColumnModel().getColumn(0).setPreferredWidth(50);
        wso.file_manager.getColumnModel().getColumn(0).setMaxWidth(50);
        wso.file_manager.getColumnModel().getColumn(1).setPreferredWidth(250);
        wso.file_manager.getColumnModel().getColumn(2).setPreferredWidth(250);
        wso.file_manager.getColumnModel().getColumn(3).setPreferredWidth(250);

        wso.file_manager.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }


    public void run() throws Exception {
        WSOFrame wso = new forms.WSOFrame();
        Base.wso_frame = wso;
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - wso.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - wso.getHeight()) / 2);
        wso.setLocation(x, y);
        wso.setResizable(false);
        wso.setVisible(true);

        wso.file_manager.setRowHeight(40);
        DefaultTableModel tm = new DefaultTableModel(new Object[0][], new Object[]{"#", "File", "Size", "Permissions", "Modified"}) {
            @Override
            public Class<?> getColumnClass(int col) {

                if (col == 0) {
                    return Icon.class;
                }
                return String.class;
            }

            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        wso.file_manager.setModel(tm);

        wso.file_manager.getColumnModel().getColumn(0).setPreferredWidth(50);
        wso.file_manager.getColumnModel().getColumn(0).setMaxWidth(50);
        wso.file_manager.getColumnModel().getColumn(1).setPreferredWidth(250);
        wso.file_manager.getColumnModel().getColumn(2).setPreferredWidth(150);
        wso.file_manager.getColumnModel().getColumn(2).setMaxWidth(100);
        wso.file_manager.getColumnModel().getColumn(3).setPreferredWidth(150);
        wso.file_manager.getColumnModel().getColumn(3).setMaxWidth(100);
        wso.file_manager.getColumnModel().getColumn(4).setPreferredWidth(250);

        wso.file_manager.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        DefaultCaret caret = (DefaultCaret) wso.logs.getCaret();
        caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
        wso.logs.setLineWrap(true);
        wso.logs.setWrapStyleWord(true);
        wso.logs.setEditable(false);
        resetFiles();
    }

}
