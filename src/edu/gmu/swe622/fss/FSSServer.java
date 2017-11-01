package edu.gmu.swe622.fss;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Server for the File Sharing System.
 */
public class FSSServer {

    private ServerSocket serverSocket;
    private boolean running = true;

    /**
     * Constructor.
     */
    public FSSServer() {
        String cwd = System.getProperty("user.dir");
        String path = cwd + File.separator + "fss";
        File serverDir = new File(path);
        if (! serverDir.exists()) {
            serverDir.mkdir();
        }
        System.setProperty("user.dir", serverDir.getAbsolutePath());
    }

    /**
     * Starts the server listening on port.
     * @throws IOException
     */
    public void serve(Integer port) {
        try {
            this.serverSocket = new ServerSocket(port);
            while (this.running) {
                Socket clientSocket = this.serverSocket.accept();
                new RequestHandler(clientSocket, this).start();
            }
        } catch (SocketException exp) {
            // thrown when socket is closed by shutdown()
        } catch (IOException exp) {
            exp.printStackTrace();
        } finally {
            if (! this.serverSocket.isClosed()) {
                try {
                    this.serverSocket.close();
                } catch (IOException exp) {
                    System.out.println("Could not close socket");
                    exp.printStackTrace();
                }
            }
        }
    }

    public void shutdown() throws IOException {
        System.out.println("Server shutting down");
        this.running = false;
        this.serverSocket.close();
    }
}
