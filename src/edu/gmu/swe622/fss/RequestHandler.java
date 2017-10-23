package edu.gmu.swe622.fss;

import java.io.*;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for carrying out client requests within a separate thread. Handles verifying the request,
 * carrying out the request and reporting errors to the client.
 */
public class RequestHandler extends Thread {

    private ObjectInput objectIn;
    private ObjectOutput objectOut;

    /**
     * Constructor.
     * @param sock  the socket used to communicate with the client.
     * @throws IOException
     */
    public RequestHandler(Socket sock) throws IOException {
        this.objectOut = new ObjectOutputStream(sock.getOutputStream());
        this.objectOut.flush();
        this.objectIn = new ObjectInputStream(sock.getInputStream());
    }

    /**
     * Overridden implementation of Thread.run.
     */
    @Override
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

    /**
     * Reads the client request and dispatches it to the appropriate handler method for the action requested.
     * @throws IOException
     */
    private void handle() throws IOException {
        try {
            Request request = (Request) this.objectIn.readObject();
            Response response;
            switch (request.getAction()) {
                case RM:
                    response = this.rm(request);
                    break;
                case MKDIR:
                    response = this.mkdir(request);
                    break;
                case DIR:
                    response = this.dir(request);
                    break;
                case RMDIR:
                    response = this.rmdir(request);
                    break;
                case UPLOAD:
                    response = this.upload(request);
                    break;
                case DOWNLOAD:
                    response = this.download(request);
                    break;
                default:
                    response = new Response("Invalid request");
                    break;
            }
            this.writeResponse(response);
        } catch (IOException exp) {
            this.writeResponse(new Response("Action could not be completed: " + exp.getMessage()));
        } catch (ClassNotFoundException exp) {
            this.writeResponse(new Response("Action could not be completed: " + exp.getMessage()));
        }
    }

    /**
     * Returns a Path object representing the file named by fileName.
     * @param fileName  the name of the file
     * @return  a Path object repesenting the file named by fileName
     */
    private Path getPath(String fileName) {
        return FileSystems.getDefault().getPath(System.getProperty("user.dir"), fileName);
    }

    /**
     * Removes a file from the server disk.
     * @param request the client request, which should contain the name of the file to remove
     * @throws IOException
     */
    private Response rm(Request request) throws IOException {
        Response response;
        if (request.getValues().isEmpty()) {
            response = new Response("File name not specified");
        } else {
            String fileName = request.getValues().get(0);
            Path filePath = this.getPath(fileName);
            if (Files.exists(filePath)) {
                Files.deleteIfExists(filePath);
                response = Response.SUCCESSFUL;
            } else {
                response = Response.FILE_NOT_FOUND;
            }
        }
        return response;
    }

    /**
     * Writes the uploaded file to the server disk.
     * @param request  the client request, which should contain the file being uploaded and the destination
     *                 directory
     * @throws IOException
     */
    private Response upload(Request request) throws IOException {
        Response response;
        if (request.getValues().isEmpty()) {
            response = new Response("No destination directory or file name specified");
        } else if (request.getFile() == null) {
            response = new Response("No file uploaded");
        } else {
            String destination = request.getValues().get(0);
            String fileName = request.getValues().get(1);
            //String size = request.getArguments().get(2);
            //long fileSize = Long.valueOf(size);

            Path destinationPath = this.getPath(destination);
            File destinationDir = destinationPath.toFile();

            if (Files.exists(destinationPath)) {
                File file = FileSystems.getDefault().getPath(destinationDir.getAbsolutePath(), fileName).toFile();
                BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
                File uploadedFile = request.getFile();
                Utility.write(new BufferedInputStream(new FileInputStream(uploadedFile)), outStream);
                outStream.close();
                response = Response.SUCCESSFUL;
            } else {
                response = new Response("File could not be uploaded");
            }
        }
        return response;
    }

    /**
     * Downloads a file from the server.
     * @param request the client request, which should contain the file name
     * @throws IOException
     */
    private Response download(Request request) throws IOException {
        Response response;
        if (request.getValues().isEmpty()) {
            response = new Response("No file name specified");
        } else {
            String fileName = request.getValues().get(0);
            Path filePath = this.getPath(fileName);

            if (Files.exists(filePath)) {
                File file = filePath.toFile();
                response = new Response(file);
            } else {
                response = Response.FILE_NOT_FOUND;
            }
        }
        return response;
    }

    /**
     * Creates a directory on the server.
     * @param request  the client request, which should contain the name of the directory
     * @throws IOException
     */
    private Response mkdir(Request request) throws IOException {
        Response response;
        if (request.getValues().isEmpty()) {
            response = new Response("No directory specified");
        } else {
            String dirName = request.getValues().get(0);
            Path newDirPath = this.getPath(dirName);
            if (! Files.exists(newDirPath)) {
                Files.createDirectory(newDirPath);
                response = Response.SUCCESSFUL;
            } else {
                response = new Response("Directory already exists");
            }
        }
        return response;
    }

    /**
     * Lists files and directories in the directory specified by the client request.
     * @param request  request sent by client, which should contain the directory name
     * @throws IOException
     */
    private Response dir(Request request) throws IOException {
        Response response;
        if (request.getValues().isEmpty()) {
            response = new Response("No directory specified");
        } else {
            String dirName = request.getValues().get(0);
            Path dirPath = this.getPath(dirName);
            if (Files.exists(dirPath)) {
                List<String> fileNames = new ArrayList<>();
                Files.list(dirPath).forEach((path) -> fileNames.add(path.getFileName().toString()));
                response = new Response(fileNames);
            } else {
                response = Response.DIRECTORY_NOT_FOUND;
            }
        }
        return response;
    }

    /**
     * Removes a directory based on a client request, or reports if the directory does not exist.
     * @param request  the request object sent by the client, which should contain the directory name
     * @throws IOException
     */
    private Response rmdir(Request request) throws IOException {
        Response response;
        if (request.getValues().isEmpty()) {
            response = new Response("Directory name not specified");
        } else {
            String dirName = request.getValues().get(0);
            Path dirPath = this.getPath(dirName);
            if (Files.exists(dirPath)) {
                Files.delete(dirPath);
                response = Response.SUCCESSFUL;
            } else {
                response = Response.DIRECTORY_NOT_FOUND;
            }
        }
        return response;
    }

    /**
     * Writes a response to the client.
     * @param response  the response to send
     * @throws IOException
     */
    private void writeResponse(Response response) throws IOException {
        this.objectOut.writeObject(response);
        this.objectOut.flush();
    }
}
