/*
 * Created by JFormDesigner on Sun Apr 03 18:40:07 CST 2022
 */

package com.gaoshi;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;

/**
 * @author Gaoshi_nol
 */
public class Img2Base64 extends JPanel {
    String currentDirectory = System.getProperty("user.home") + File.separator + "Pictures";

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();

        JFrame jFrame = new JFrame();
        jFrame.setSize(320, 300);
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
                base64 = "data:image/" + imageUrl.substring(imageUrl.lastIndexOf(".") + 1) + ";base64," + Base64.getEncoder().encodeToString(connection.getInputStream().readAllBytes());
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

    private void btnClipboardMouseClicked(MouseEvent e) {
        // TODO add your code here
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable cc = sysc.getContents(null);
        if (cc == null) {
            return;
        } else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                // this statement can output base64
                // System.out.println(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor));
                // below is another way
                BufferedImage image = (BufferedImage) cc.getTransferData(DataFlavor.imageFlavor);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(image, "png", baos);
                    InputStream ins = new ByteArrayInputStream(baos.toByteArray());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("data:image/png;base64," + Base64.getEncoder().encodeToString(ins.readAllBytes())), null);
                    JOptionPane.showMessageDialog(null, "base64已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (UnsupportedFlavorException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            return;
        }
    }

    private void btnClipboard1MouseClicked(MouseEvent e) {
        // TODO add your code here
        try {
            String base64 = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
            String imageDataBytes = base64.substring(base64.indexOf(",") + 1);
            InputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(imageDataBytes.getBytes()));
            Image image = ImageIO.read(stream);
            ImageTransferable imageTransferable = new ImageTransferable(image);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imageTransferable, null);
            JOptionPane.showMessageDialog(null, "图像已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (UnsupportedFlavorException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    //copied from https://stackoverflow.com/questions/7834768/setting-images-to-clipboard-java
    static class ImageTransferable implements Transferable {
        private final Image image;

        public ImageTransferable(Image image) {
            this.image = image;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (isDataFlavorSupported(flavor)) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == DataFlavor.imageFlavor;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblFile = new JLabel();
        lblImage = new JLabel();
        filePath = new JTextField();
        imageUrl = new JTextField();
        btnConvert = new JButton();
        btnReset = new JButton();
        btnClipboard = new JButton();
        separator1 = new JSeparator();
        separator2 = new JSeparator();
        btnClipboard1 = new JButton();

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
        btnConvert.setBounds(35, 80, 90, 40);

        //---- btnReset ----
        btnReset.setText("Reset");
        btnReset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnResetMouseClicked(e);
            }
        });
        add(btnReset);
        btnReset.setBounds(190, 80, 85, 40);

        //---- btnClipboard ----
        btnClipboard.setText("\u4ece\u526a\u8d34\u677f\u8bfb\u53d6\u56fe\u50cf\u8f6cbase64");
        btnClipboard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnClipboardMouseClicked(e);
            }
        });
        add(btnClipboard);
        btnClipboard.setBounds(60, 145, 190, 35);
        add(separator1);
        separator1.setBounds(70, 130, 160, 33);
        add(separator2);
        separator2.setBounds(10, 195, 295, 10);

        //---- btnClipboard1 ----
        btnClipboard1.setText("\u4ece\u526a\u8d34\u677f\u8bfb\u53d6base64\u8f6c\u56fe\u50cf");
        btnClipboard1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnClipboardMouseClicked(e);
                btnClipboard1MouseClicked(e);
            }
        });
        add(btnClipboard1);
        btnClipboard1.setBounds(60, 210, 190, 35);

        setPreferredSize(new Dimension(310, 265));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblFile;
    private JLabel lblImage;
    private JTextField filePath;
    private JTextField imageUrl;
    private JButton btnConvert;
    private JButton btnReset;
    private JButton btnClipboard;
    private JSeparator separator1;
    private JSeparator separator2;
    private JButton btnClipboard1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
