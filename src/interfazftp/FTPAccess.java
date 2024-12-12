/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfazftp;

/**
 *
 * @author oriol
 */
import org.apache.commons.net.ftp.FTPClient;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FTPAccess implements FTPAccessInterface {
    private FTPClient ftpClient = new FTPClient();
    
    private boolean conectado = false; 

    @Override
    public boolean connectar(String server, int puerto, String usuario, String contra) throws Exception {
        ftpClient.connect(server, puerto);
        conectado = ftpClient.login(usuario, contra); // actualizar el estado de conexion
        return conectado;
    }

    @Override
    public void desconectar() throws Exception {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
            conectado = false; // actualizar el estado de conexion
        }
    }

    @Override
    public boolean subirArchivo(String localPath, String remotePath) throws Exception {
        if (!conectado) {
            throw new IllegalStateException("Error: No estás conectado ");
        }
        try (InputStream input = new FileInputStream(localPath)) {
            return ftpClient.storeFile(remotePath, input);
        }
    }

    @Override
    public boolean bajarArchivo(String remotePath, String localPath) throws Exception {
        if (!conectado) {
            throw new IllegalStateException("Error: No estás conectado");
        }
        try (OutputStream output = new FileOutputStream(localPath)) {
            return ftpClient.retrieveFile(remotePath, output);
        }
    }

    // comprueba si esta conectado
    public boolean estaConectado() {
        return conectado;
    }
}