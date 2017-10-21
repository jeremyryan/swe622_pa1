package edu.gmu.swe622.fss;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server for the File Sharing System.
 */
public class FSSServer {

    private ServerSocket serverSocket;

    public FSSServer() {
        String homeDir = System.getProperty("user.home");
        String path = homeDir + File.separator + "fss";
        File serverDir = new File(path);
        if (! serverDir.exists()) {
            serverDir.mkdir();
        }
        System.setProperty("user.dir", serverDir.getAbsolutePath());
    }

    public void serve(Integer port) throws IOException, ClassNotFoundException {
        this.serverSocket = new ServerSocket(port);

        while (true) {
            Socket clientSocket = this.serverSocket.accept();
            new RequestHandler(clientSocket).start();
        }
    }

    /*
    private void dir(String[] args) throws IOException {
        String dirName = args[1];
        File file = new File(dirName);
        this.writeResponse(":begin");
        if (this.validateDirectory(file)) {
            for (String fileName : file.list()) {
                this.writeResponse(fileName);
            }
        }
        this.writeResponse(":end");
    }

    private void mkdir(String[] args) throws IOException {
        String dirName = args[1];
        File newDir = this.newFile(dirName);
        if (! newDir.exists()) {
            newDir.mkdir();
        }
    }

    private void rmdir(String[] args) throws IOException {
        String dirName = args[1];
        File file = new File(dirName);
        if (this.validateDirectory(file)) {
            file.delete();
        }
    }

*/
}
