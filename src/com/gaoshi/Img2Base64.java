/*
 * Created by JFormDesigner on Sun Apr 03 18:40:07 CST 2022
 */

package com.gaoshi;

import java.awt.event.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;

/**
 * @author Gaoshi_nol
 */
public class Img2Base64 extends JPanel {
    String currentDirectory = System.getProperty("user.home") + File.separator + "Pictures";

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();

        JFrame jFrame = new JFrame();
        jFrame.setSize(320, 170);
        jFrame.setTitle("图片转Base64");
        jFrame.setContentPane(new Img2Base64());
        jFrame.setResizable(false);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Img2Base64() {
        initComponents();
    }

    private void filePathMouseClicked(MouseEvent e) {
        // TODO add your code here
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(currentDirectory));
        fileChooser.showOpenDialog(fileChooser);
        File image = fileChooser.getSelectedFile();
        if (image == null) {
            JOptionPane.showMessageDialog(null, "文件不存在", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        } else {
            this.filePath.setText(image.getAbsolutePath());
            currentDirectory = image.getParent();
        }
    }

    private void btnResetMouseClicked(MouseEvent e) {
        // TODO add your code here
        this.imageUrl.setText("");
        this.filePath.setText("");
    }

    private void btnConvertMouseClicked(MouseEvent e) {
        // TODO add your code here
        String filePath = this.filePath.getText();
        String imageUrl = this.imageUrl.getText();
        String base64 = "";
        if (filePath.length() > 0 && imageUrl.trim().length() > 0) {
            JOptionPane.showMessageDialog(null, "本地文件和URL需要二选一，不能同时填入", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (filePath.length() > 0) {
            File image = new File(this.filePath.getText());
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(image);
                base64 = "data:image/" + filePath.substring(filePath.lastIndexOf(".") + 1) + ";base64," + Base64.getEncoder().encodeToString(fis.readAllBytes());
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (imageUrl.trim().length() > 0) {
            if (imageUrl.lastIndexOf(".") == -1) {
                JOptionPane.showMessageDialog(null, "图片链接不正确，重填", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                if (!(imageUrl.substring(imageUrl.lastIndexOf(".") + 1).equalsIgnoreCase("jpg") || imageUrl.substring(imageUrl.lastIndexOf(".") + 1)
                        .equalsIgnoreCase("png") || imageUrl.substring(imageUrl.lastIndexOf(".") + 1).equalsIgnoreCase("jpeg"))) {
                    JOptionPane.showMessageDialog(null, "目前图片链接仅支持jpeg/jpg/png格式", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) new URL(imageUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.connect();
                base64 = "data:image/" + imageUrl.substring(imageUrl.lastIndexOf(".") + 1) + ";base64," + Base64.getEncoder().encodeToString(connection.getInputStream().readAllBytes());;
                connection.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "本地文件或URL需要填入其中一项", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // set base64 to clipboard
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(base64), null);
        base64 = "";
        System.gc();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblFile = new JLabel();
        lblImage = new JLabel();
        filePath = new JTextField();
        imageUrl = new JTextField();
        btnConvert = new JButton();
        btnReset = new JButton();

        //======== this ========
        setLayout(null);

        //---- lblFile ----
        lblFile.setText("\u56fe\u7247\u8def\u5f84");
        add(lblFile);
        lblFile.setBounds(20, 10, 60, 25);

        //---- lblImage ----
        lblImage.setText("\u56fe\u7247\u94fe\u63a5");
        add(lblImage);
        lblImage.setBounds(20, 50, 60, 20);

        //---- filePath ----
        filePath.setEditable(false);
        filePath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                filePathMouseClicked(e);
            }
        });
        add(filePath);
        filePath.setBounds(95, 10, 195, filePath.getPreferredSize().height);
        add(imageUrl);
        imageUrl.setBounds(95, 45, 195, imageUrl.getPreferredSize().height);

        //---- btnConvert ----
        btnConvert.setText("Convert");
        btnConvert.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnConvertMouseClicked(e);
            }
        });
        add(btnConvert);
        btnConvert.setBounds(35, 85, 90, 40);

        //---- btnReset ----
        btnReset.setText("Reset");
        btnReset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnResetMouseClicked(e);
            }
        });
        add(btnReset);
        btnReset.setBounds(190, 85, 85, 40);

        setPreferredSize(new Dimension(310, 135));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblFile;
    private JLabel lblImage;
    private JTextField filePath;
    private JTextField imageUrl;
    private JButton btnConvert;
    private JButton btnReset;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
