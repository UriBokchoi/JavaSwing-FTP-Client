
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfazftp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.net.ftp.FTPClient;


/**
 *
 * @author oriol
 */

public class FrameFTP extends javax.swing.JFrame {

private JTextArea logArea;
    private FTPAccessInterface ftpAccess;

    public FrameFTP() {
        initComponents();
        ftpAccess = new FTPAccess();
        ftpAccess.addMessageListener(message -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                logArea.append(message + "\n");
            });
        });

        ftpmetodo();

    }

    

    public void ftpmetodo() {
        ftpAccess = new FTPAccess();

        setLayout(new BorderLayout(5, 5));

        JMenuBar menu = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Menu");

        JMenuItem guardarCambios = new JMenuItem("Guardar cambios");
        JMenuItem guardarYSalir = new JMenuItem("Guardar y salir");
        JMenuItem cerrarArchivo = new JMenuItem("Cerrar archvio");
        JMenuItem cerrar = new JMenuItem("Cerrar");

        fileMenu.add(guardarCambios);
        fileMenu.add(guardarYSalir);
        fileMenu.add(cerrarArchivo);
        fileMenu.add(cerrar);

        cerrar.addActionListener(e -> System.exit(0));

        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);

        //mensaje de about
        aboutItem.addActionListener(e -> {
            javax.swing.JOptionPane.showMessageDialog(this, "EditOnCloud v1.0\nDesarrollado por Oriol Cárdenas",
                    "About", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        });

        menu.add(fileMenu);
        menu.add(helpMenu);

        setJMenuBar(menu);

        // Panel north
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(new JLabel("Dirección:"));
        JTextField direccionField = new JTextField("127.0.0.1", 10);
        northPanel.add(direccionField);

        northPanel.add(new JLabel("Puerto:"));
        JTextField puertoField = new JTextField("21", 5);
        northPanel.add(puertoField);

        northPanel.add(new JLabel("Usuario:"));
        JTextField usuarioField = new JTextField("", 10);
        northPanel.add(usuarioField);

        northPanel.add(new JLabel("Contraseña:"));
        JPasswordField contraseñaField = new JPasswordField(10);
        northPanel.add(contraseñaField);

        JButton conectarButton = new JButton("Conectar:");
        JButton desconectarButton = new JButton("Desconactar:");
        conectarButton.addActionListener(e -> {
            try {
                String direccion = direccionField.getText();
                int puerto = Integer.parseInt(puertoField.getText());
                String usuario = usuarioField.getText();
                String contraseña = new String(contraseñaField.getPassword());

                if (ftpAccess.connectar(direccion, puerto, usuario, contraseña)) {
                    logArea.append("Conexión exitosa\n");
                    conectarButton.setEnabled(false);
                    desconectarButton.setEnabled(true);
                } else {
                    logArea.append("Error al conectar\n");
                }
            } catch (Exception ex) {
                logArea.append("Error: " + ex.getMessage() + "\n");
            }
        });
        desconectarButton.addActionListener(e -> {
            try {
                ftpAccess.desconectar();
                logArea.append("Desconexión exitosa\n");
                conectarButton.setEnabled(true);
                desconectarButton.setEnabled(false);
            } catch (Exception ex) {
                logArea.append("Error al desconectar: " + ex.getMessage() + "\n");
            }
        });

        desconectarButton.setEnabled(false);
        northPanel.add(conectarButton);
        northPanel.add(desconectarButton);

        JButton uploadButton = new JButton("Subir Archivo");
        uploadButton.addActionListener(e -> onUploadFile());
        northPanel.add(uploadButton);
        add(northPanel, BorderLayout.NORTH);

        JPanel westPanel = new JPanel(new BorderLayout());
        JTree fileTree = new JTree();
        JScrollPane treeScrollPane = new JScrollPane(fileTree);
        westPanel.add(treeScrollPane, BorderLayout.CENTER);
        add(westPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        JTextPane textEditor = new JTextPane();
        JScrollPane textScrollPane = new JScrollPane(textEditor);
        centerPanel.add(textScrollPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        toolBar.addSeparator();
        toolBar.add(new JComboBox<>(new String[]{"Paragraph"}));
        toolBar.add(new JComboBox<>(new String[]{"12 pt", "14 pt", "16 pt"}));
        centerPanel.add(toolBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        logArea = new JTextArea(5, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        add(logScrollPane, BorderLayout.SOUTH);

    }

    public void onUploadFile() {
         javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos HTML", "html", "htm"));
        int result = fileChooser.showOpenDialog(this);

        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            try {
                ftpAccess.subirArchivo(selectedFile.getAbsolutePath(), "/" + selectedFile.getName());
                logArea.append("Archivo subido: " + selectedFile.getName() + "\n");
            } catch (Exception e) {
                logArea.append("Error al subir el archivo: " + e.getMessage() + "\n");
            }
        }
    }
    public void onDownloadFile() {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Selecciona la ubicación para guardar el archivo");

        int result = fileChooser.showSaveDialog(this);
        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File destinationFile = fileChooser.getSelectedFile();

            try {
                String remotePath = "/ruta/del/archivo/en/el/servidor";
                ftpAccess.bajarArchivo(remotePath, destinationFile.getAbsolutePath());
                javax.swing.SwingUtilities.invokeLater(() -> {
                    logArea.append("Archivo descargado con éxito: " + destinationFile.getAbsolutePath() + "\n");
                });
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    logArea.append("Error al descargar el archivo: " + e.getMessage() + "\n");
                });
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
                          

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ClientFTP");
        setSize(new java.awt.Dimension(1000, 600));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrameFTP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrameFTP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrameFTP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrameFTP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrameFTP().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
