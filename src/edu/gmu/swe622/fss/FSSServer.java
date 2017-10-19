package edu.gmu.swe622.fss;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by jmr on 10/15/2017.
 */
public class FSSServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public FSSServer() {
        String homeDir = System.getProperty("user.home");
        String path = homeDir + File.separator + "fss";
        File serverDir = new File(path);
        if (! serverDir.exists()) {
            serverDir.mkdir();
        }
        System.setProperty("user.dir", serverDir.getAbsolutePath());
    }

    public void serve(Integer port) throws IOException {
        this.serverSocket = new ServerSocket(port);

        while (true) {
            this.clientSocket = this.serverSocket.accept();

            BufferedReader clientReader = new BufferedReader(
                    new InputStreamReader(this.clientSocket.getInputStream()));

            String clientRequest = clientReader.readLine();

            System.out.println("clientRequest = " + clientRequest);

            String[] args = clientRequest.split("\\s");
            Action action = Action.findByName(args[0]);

            if (action == null) {
                this.writeResponse("error: Invalid action name");
            } else {
                this.doAction(action, args);
            }
            this.clientSocket.close();
        }

    }

    private void doAction(Action action, String[] args) throws IOException {
        switch (action) {
            case RM:
                this.rm(args);
                break;
            case DIR:
                this.dir(args);
                break;
            case DOWNLOAD:
                this.download(args);
                break;
            case MKDIR:
                this.mkdir(args);
                break;
            case RMDIR:
                this.rmdir(args);
                break;
            case UPLOAD:
                this.upload(args);
                break;
            default:
                break;
        }
    }

    private void rm(String[] args) throws IOException {
        String fileName = args[1];
        File file = new File(fileName);
        if (this.validateFile(file)) {
            file.delete();
        }
    }

    private boolean validateFile(File file) throws IOException {
        boolean validated = true;
        if (! file.exists()) {
            this.writeResponse("error File could not be found");
            validated = false;
        }
        return validated;
    }

    private boolean validateDirectory(File file) throws IOException {
        boolean validated = this.validateFile(file);
        if (! file.isDirectory()) {
            this.writeResponse("error Specified file is not a directory");
            validated = false;
        }

        return validated;
    }

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

    private void download(String[] args) throws IOException {
        String remoteFile = args[1];
        File file = this.newFile(remoteFile);
        if (this.validateFile(file)) {
            BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
            Utility.write(inStream, this.clientSocket.getOutputStream());
            inStream.close();
        }
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

    private File newFile(String path) {
        return new File(System.getProperty("user.dir") + File.separator + path);
    }

    private void upload(String[] args) throws IOException {
        String localFileName = args[1];
        String destination = args[2];
        String start = args[2];
        File uploadFile = new File(localFileName);
        String fileName = uploadFile.getName();
        File dir = this.newFile(destination);
        if (this.validateDirectory(dir)) {
            this.writeResponse("ready-for-file");
            File outFile = new File(dir.getAbsolutePath() + File.separator + fileName);
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(outFile));
            Utility.write(this.clientSocket.getInputStream(), outStream);
            outStream.close();
        }
    }

    private void writeResponse(String message) throws IOException {
        for (byte b : message.getBytes()) {
            this.clientSocket.getOutputStream().write(b);
        }
        this.clientSocket.getOutputStream().write((byte) '\n');
    }
}
