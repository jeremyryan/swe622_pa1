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
            this.objectIn.close();
            this.objectOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handle() throws IOException, ClassNotFoundException {
        Request request = (Request) this.objectIn.readObject();
        switch (request.getAction()) {
            case RM:
                this.rm(request);
                break;
            /*
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
                // just nothing
                break;
        }
    }


    private void rm(Request request) throws IOException {
        String fileName = request.getArguments().get(0);
        File file = new File(fileName);
        if (this.validateFile(file)) {
            file.delete();
        } else {
            this.writeResponse(new Response("File could not be deleted"));
        }
    }

    private void upload(Request request) throws IOException, ClassNotFoundException {
        String destination = request.getArguments().get(0);
        String fileName = request.getArguments().get(1);
        //String size = request.getArguments().get(2);
        //long fileSize = Long.valueOf(size);

        Path destinationPath = FileSystems.getDefault().getPath(System.getProperty("user.dir"), destination);
        File destinationDir = destinationPath.toFile();

        if (this.validateDirectory(destinationDir)) {
            File file = FileSystems.getDefault().getPath(destinationDir.getAbsolutePath(), fileName).toFile();
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
            File uploadedFile = request.getFile();
            Utility.write(new BufferedInputStream(new FileInputStream(uploadedFile)), outStream);
            outStream.close();
            this.writeResponse(Response.SUCCESSFUL);
        } else {
            this.writeResponse(new Response("File could not be uploaded"));
        }
    }

    private void download(Request request) throws IOException {
        String fileName = request.getArguments().get(0);
        Path filePath = FileSystems.getDefault().getPath(System.getProperty("user.dir"), fileName);

        if (Files.exists(filePath)) {
            File file = filePath.toFile();
            Response response = new Response(file);
            this.writeResponse(response);
        } else {
            this.writeResponse(Response.FILE_NOT_FOUND);
        }
    }

    private boolean validateFile(File file) throws IOException {
        boolean validated = true;
        if (! file.exists()) {
            //this.writeResponse("error File could not be found");
            validated = false;
        }
        return validated;
    }

    private boolean validateDirectory(File file) throws IOException {
        boolean validated = this.validateFile(file);
        if (! file.isDirectory()) {
            //this.writeResponse("error Specified file is not a directory");
            validated = false;
        }

        return validated;
    }

    private void writeResponse(Response response) throws IOException {
        this.objectOut.writeObject(response);
        this.objectOut.flush();
    }
}
