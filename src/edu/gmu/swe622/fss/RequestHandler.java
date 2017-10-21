package edu.gmu.swe622.fss;

import java.io.*;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by jmr on 10/21/17.
 */
public class RequestHandler extends Thread {

    private ObjectInput objectIn;
    private ObjectOutput objectOut;

    public RequestHandler(Socket sock) throws IOException {
        this.objectOut = new ObjectOutputStream(sock.getOutputStream());
        this.objectOut.flush();
        this.objectIn = new ObjectInputStream(sock.getInputStream());
    }

    public void run() {
        try {
            this.handle();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.objectIn.close();
                this.objectOut.close();
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
    }

    private void handle() throws IOException {
        try {
            Request request = (Request) this.objectIn.readObject();
            switch (request.getAction()) {
                case RM:
                    this.rm(request);
                    break;
                case MKDIR:
                    this.mkdir(request);
                    break;
                case DIR:
                    this.dir(request);
                    break;
                case RMDIR:
                    this.rmdir(request);
                break;
                case UPLOAD:
                    this.upload(request);
                    break;
                case DOWNLOAD:
                    this.download(request);
                    break;
                default:
                    // oh just nothing
                    break;
            }
        } catch (IOException exp) {
            this.writeResponse(new Response("Action could not be completed: " + exp.getMessage()));
        } catch (ClassNotFoundException exp) {
            this.writeResponse(new Response("Action could not be completed: " + exp.getMessage()));
        }
    }


    private Path getPath(String fileName) {
        return FileSystems.getDefault().getPath(System.getProperty("user.dir"), fileName);
    }

    private void rm(Request request) throws IOException {
        String fileName = request.getArguments().get(0);
        Path filePath = this.getPath(fileName);
        if (Files.exists(filePath)) {
            Files.deleteIfExists(filePath);
            this.writeResponse(Response.SUCCESSFUL);
        } else {
            this.writeResponse(Response.FILE_NOT_FOUND);
        }
    }

    private void upload(Request request) throws IOException, ClassNotFoundException {
        String destination = request.getArguments().get(0);
        String fileName = request.getArguments().get(1);
        //String size = request.getArguments().get(2);
        //long fileSize = Long.valueOf(size);

        Path destinationPath = this.getPath(destination);
        File destinationDir = destinationPath.toFile();

        if (Files.exists(destinationPath)) {
            File file = FileSystems.getDefault().getPath(destinationDir.getAbsolutePath(), fileName).toFile();
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
            File uploadedFile = request.getFile();
            System.out.println("server file len = " + uploadedFile.length());
            Utility.write(new BufferedInputStream(new FileInputStream(uploadedFile)), outStream);
            outStream.close();
            this.writeResponse(Response.SUCCESSFUL);
        } else {
            this.writeResponse(new Response("File could not be uploaded"));
        }
    }

    private void download(Request request) throws IOException {
        String fileName = request.getArguments().get(0);
        Path filePath = this.getPath(fileName);

        if (Files.exists(filePath)) {
            File file = filePath.toFile();
            Response response = new Response(file);
            this.writeResponse(response);
        } else {
            this.writeResponse(Response.FILE_NOT_FOUND);
        }
    }

    private void mkdir(Request request) throws IOException {
        String dirName = request.getArguments().get(0);
        Path newDirPath = this.getPath(dirName);
        if (! Files.exists(newDirPath)) {
            Files.createDirectory(newDirPath);
            this.writeResponse(Response.SUCCESSFUL);
        } else {
            this.writeResponse(new Response("Directory already exists"));
        }
    }

    private void dir(Request request) throws IOException {
        String dirName = request.getArguments().get(0);
        Path dirPath = this.getPath(dirName);
        if (Files.exists(dirPath)) {
            Files.list(dirPath).forEach((Path path) -> System.out.println(path));
        } else {
           this.writeResponse(new Response("Directory was not found"));
        }
    }

    private void rmdir(Request request) throws IOException {
        String dirName = request.getArguments().get(0);
        Path dirPath = this.getPath(dirName);
        if (Files.exists(dirPath)) {
            Files.delete(dirPath);
            this.writeResponse(Response.SUCCESSFUL);
        } else {
            this.writeResponse(new Response("Directory does not exist"));
        }
    }

    private void writeResponse(Response response) throws IOException {
        this.objectOut.writeObject(response);
        this.objectOut.flush();
    }
}
