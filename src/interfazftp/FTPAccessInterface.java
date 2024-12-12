/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfazftp;

/**
 *
 * @author oriol
 */
public interface FTPAccessInterface {
    boolean connectar(String server, int port, String user, String pass) throws Exception;
    void desconectar() throws Exception;
    boolean subirArchivo(String localPath, String remotePath) throws Exception;
    boolean bajarArchivo(String remotePath, String localPath) throws Exception;
}