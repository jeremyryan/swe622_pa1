package edu.gmu.swe622.fss;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Server for the File Sharing System.
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

    public void serve(Integer port) throws IOException, ClassNotFoundException {
        this.serverSocket = new ServerSocket(port);

        while (true) {
            this.clientSocket = this.serverSocket.accept();

            ObjectInput in = new ObjectInputStream(this.clientSocket.getInputStream());
            Request request = (Request) in.readObject();
            if (request == null) {
                this.writeResponse("error: Invalid action name");
            } else {
                this.doAction(request);
            }
            this.clientSocket.close();
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

    private void doAction(Request request) throws IOException, ClassNotFoundException {
        switch (request.getAction()) {
            /*
            case RM:
                this.rm(args);
                break;
            case DIR:
                this.dir(args);
                break;
            case MKDIR:
                this.mkdir(args);
                break;
            case RMDIR:
                this.rmdir(args);
                break;
                */
            case UPLOAD:
                this.upload(request);
                break;
            case DOWNLOAD:
                this.download(request);
                break;
            default:
                break;
        }
    }

    private void upload(Request request) throws IOException, ClassNotFoundException {
        String destination = request.getArguments().get(0);
        String fileName = request.getArguments().get(1);
        String size = request.getArguments().get(2);
        long fileSize = Long.valueOf(size);
        Path destinationPath = FileSystems.getDefault().getPath(System.getProperty("user.dir"), destination);
        File destinationDir = destinationPath.toFile();

        if (this.validateDirectory(destinationDir)) {
            File file = FileSystems.getDefault().getPath(destinationDir.getAbsolutePath(), fileName).toFile();
            ObjectOutput out = new ObjectOutputStream(this.clientSocket.getOutputStream());
            out.writeObject(Response.READY_RESPONSE);
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
            File uploadedFile = (File) new ObjectInputStream(this.clientSocket.getInputStream()).readObject();
            Utility.write(new BufferedInputStream(new FileInputStream(uploadedFile)), outStream);
            outStream.close();
        }
    }

    private void download(Request request) throws IOException {
        String fileName = request.getArguments().get(0);
        Path filePath = FileSystems.getDefault().getPath(fileName);
        ObjectOutput out = new ObjectOutputStream(this.clientSocket.getOutputStream());
        if (Files.exists(filePath)) {
            out.writeObject(Response.READY_RESPONSE);
            File file = filePath.toFile();
            out.writeObject(file);
        } else {
            out.writeObject(Response.FILE_NOT_FOUND_RESPONSE);
        }
    }

    /*
    private void rm(String[] args) throws IOException {
        String fileName = args[1];
        File file = new File(fileName);
        if (this.validateFile(file)) {
            file.delete();
        }
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
    private void writeResponse(String message) throws IOException {
        for (byte b : message.getBytes()) {
            this.clientSocket.getOutputStream().write(b);
        }
        this.clientSocket.getOutputStream().write((byte) '\n');
    }
}
