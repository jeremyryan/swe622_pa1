package edu.gmu.swe622.fss;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Server for the File Sharing System.
 */
public class FSSServer {

    /**
     * Directory which uploaded files are stored in.
     */
    public static final String SERVER_DIR = "fss";

    private ServerSocket serverSocket;
    private boolean running = true;

    /**
     * Constructor. Sets the working directory to the filesystem root and creates the folder which holds uploaded
     * files.
     * @throws  if the server directory could not be created
     */
    public FSSServer() throws Exception {
        Path root = FileSystems.getDefault().getRootDirectories().iterator().next();
        System.setProperty("user.dir", root.toString());
        Path serverDir = FileSystems.getDefault().getPath(root.toString(), SERVER_DIR);
        if (! Files.exists(serverDir)) {
            Files.createDirectory(serverDir);
        }
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
            if (! (this.serverSocket == null || this.serverSocket.isClosed())) {
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
