package edu.gmu.swe622.fss;

import java.io.*;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Implements the client for the File Sharing System.
 */
public class FSSClient {

    private String hostName;
    private Integer port;
    private Socket sock;
    private ObjectInput objectIn;
    private ObjectOutput objectOut;

    /**
     * Constructor.
     * @throws IllegalArgumentException  if either hostName or port are null
     */
    public FSSClient(String hostName, Integer port) {
        if (hostName == null || port == null) {
            throw new IllegalArgumentException("hostName and port must not be null");
        }
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Dispatches user input to client request handlers.
     * @param action  the action to carry out
     * @param args  the values required to carry out the action
     * @throws IOException  if there is an error while communicating with the server
     */
    public void doAction(Action action, String[] args) throws Exception {
        this.sock = new Socket(hostName, port);
        this.objectIn = new ObjectInputStream(this.sock.getInputStream());
        this.objectOut = new ObjectOutputStream(this.sock.getOutputStream());
        try {
            switch (action) {
                case RM:
                    this.rm(args[0]);
                    break;
                case MKDIR:
                    this.mkdir(args[0]);
                    break;
                case DIR:
                    this.dir(args[0]);
                    break;
                case RMDIR:
                    this.rmdir(args[0]);
                    break;
                case UPLOAD:
                    this.upload(args[0], args[1]);
                    break;
                case DOWNLOAD:
                    this.download(args[0], args[1]);
                    break;
                case SHUTDOWN:
                    this.shutdown();
                    break;
                default:
                    break;
            }
        } finally {
            this.sock.close();
        }
    }

    /**
     * Sends a request to remove the file specified by fileName from the server.
     * @param fileName the name of the file to remove
     * @throws IOException  if there is an error while communicating with the server
     * @throws ClassNotFoundException  if the response from the server cannot be cast to a Response object
     */
    private void rm(String fileName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.RM);
        request.setValue(fileName);
        Response response = this.send(request);
        if (response.isValid()) {
            System.out.println("File removed");
        } else {
            this.reportErrorAndExit("File could not be deleted: " + response.getErrorMessage());
        }
    }

    /**
     * Sends a request to create a directory named by dirName to the server.
     * @param dirName the name of the directory to create
     * @throws IOException  if there is an error while communicating with the server
     * @throws ClassNotFoundException  if the response from the server cannot be cast to a Response object
     */
    private void mkdir(String dirName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.MKDIR);
        request.setValue(dirName);
        Response response = this.send(request);
        if (response.isValid()) {
            System.out.println("Directory created");
        } else {
            this.reportErrorAndExit("Directory could not be created: " + response.getErrorMessage());
        }
    }

    /**
     * Sends a request to list the contents of a directory specified by dirName from the server.
     * The returned list of files and directories is then printed on stdout.
     * @param dirName the name of the directory on the server to list
     * @throws IOException  if there is an error while communicating with the server
     * @throws ClassNotFoundException  if the response from the server cannot be cast to a Response object
     */
    private void dir(String dirName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.DIR);
        request.setValue(dirName);
        Response response = this.send(request);
        if (response.isValid()) {
            System.out.println("Directory contents:");
            ((List<String>) response.getValue()).stream().forEach(System.out::println);
        } else {
            this.reportErrorAndExit("Directory could not be listed: " + response.getErrorMessage());
        }
    }

    /**
     * Sends a request to remove a directory specified by dirName from the server.
     * @param dirName  the name of the directory to delete.
     * @throws IOException  if there is an error while communicating with the server
     * @throws ClassNotFoundException  if the response from the server cannot be cast to a Response object
     */
    private void rmdir(String dirName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.RMDIR);
        request.setValue(dirName);
        Response response = this.send(request);
        if (response.isValid()) {
            System.out.println("Directory removed");
        } else {
            this.reportErrorAndExit("Directory could not be removed: " + response.getErrorMessage());
        }
    }

    /**
     * Sends a request to upload a file specified by localFilePath to the remote directory specified by
     * remoteDestination.
     * @param localFilePath path of the file to upload to the server
     * @param remoteDestination the name of the remote directory where the file should be created on
     *                          the server
     * @throws IOException  if there is an error while communicating with the server
     * @throws ClassNotFoundException  if the response from the server cannot be cast to a Response object
     */
    private void upload(String localFilePath, String remoteDestination)
            throws IOException, ClassNotFoundException {
        File file = new File(localFilePath);
        if (! file.exists()) {
            this.reportErrorAndExit("File could not be found: " + localFilePath);
        }

        Request request = new Request(Action.UPLOAD);
        request.setValue(remoteDestination);
        request.setFileSize(file.length());
        Response response = this.send(request);

        if (response.isValid()) {
            BufferedOutputStream outStream = new BufferedOutputStream(this.sock.getOutputStream());
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                Long uploadedBytes = response.getFileSize();
                float total = file.length();
                System.out.println("Uploading file...");
                float percentDone = 0f;
                if (uploadedBytes != null && uploadedBytes > 0 && uploadedBytes < total) {
                    randomAccessFile.seek(uploadedBytes-1);
                    percentDone = (uploadedBytes / total) * 100;
                    System.out.println(String.format("Skipping %d%% of upload", (int) percentDone));
                }
                long bytesWritten = 0;
                int b;
                while (-1 != (b = randomAccessFile.read())) {
                    outStream.write(b);
                    bytesWritten++;
                    int percent = (int) ((bytesWritten / total) * 100);
                    if (percent > percentDone) {
                        outStream.flush();
                        System.out.println(percent + "% uploaded");
                        percentDone += 10;
                    }
                }
                outStream.flush();
            }

            response = (Response) this.objectIn.readObject();
            if (response.isValid()) {
                System.out.println("File uploaded");
            } else {
                this.reportErrorAndExit("File could not be uploaded: " + response.getErrorMessage());
            }
        } else {
            this.reportErrorAndExit("File could not be uploaded: " + response.getErrorMessage());
        }
    }

    /**
     * Sends a request to download a file, specified by remoteFile, from the server to the local
     * directory specified by destination.
     * @param remoteFile  the file to download from the server
     * @param destination the destination directory for the downloaded file
     * @throws IOException  if there is an error while communicating with the server
     * @throws ClassNotFoundException  if the response from the server cannot be cast to a Response object
     */
    private void download(String remoteFile, String destination) throws IOException, ClassNotFoundException {
        Path destinationPath = FileSystems.getDefault().getPath(destination);
        String fileName = FileSystems.getDefault().getPath(remoteFile).getFileName().toString();
        if (! Files.exists(destinationPath)) {
            this.reportErrorAndExit("Destination directory could not be found.");
        }
        Path filePath = FileSystems.getDefault().getPath(destinationPath.toString(), fileName);
        File file = filePath.toFile();
        Request request = new Request(Action.DOWNLOAD);
        request.setValue(remoteFile);
        request.setFileSize(file.length());

        Response response = this.send(request);

        if (! response.isValid()) {
            this.reportErrorAndExit("File could not be downloaded: " + response.getErrorMessage());
        } else {
            System.out.println("Downloading file...");
            float percentDone = 0f;
            float total = response.getFileSize();
            long downloadedBytes = file.length();

            BufferedInputStream inStream = new BufferedInputStream(this.sock.getInputStream());
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
                if (downloadedBytes != 0 && downloadedBytes < total) {
                    randomAccessFile.seek(downloadedBytes);
                    percentDone = (downloadedBytes / total) * 100;
                    System.out.println(String.format("Skipping %d%% of download", (int) percentDone));
                }
                while (downloadedBytes < total) {
                    randomAccessFile.write(inStream.read());
                    downloadedBytes++;
                    int percent = (int) ((downloadedBytes / total) * 100);
                    if (percent > percentDone) {
                        System.out.println(percent + "% downloaded");
                        percentDone += 10;
                    }
                }
            }
            System.out.println("File downloaded");
        }
    }

    /**
     * Sends a shutdown request to the server.
     * @throws IOException  if there is an error while communicating with the server
     * @throws ClassNotFoundException  if the response from the server cannot be cast to a Response object
     */
    private void shutdown() throws IOException, ClassNotFoundException {
        Request request = new Request(Action.SHUTDOWN);
        Response response = this.send(request);
        if (! response.isValid()) {
            System.out.println("Server could not be shut down");
        }
    }

    /**
     * Sends the request to the server and returns the server response.
     * @param request  the request to send to the server
     * @return  the server response
     * @throws IOException  if there is an error while communicating with the server
     * @throws ClassNotFoundException  if the response from the server cannot be cast to a Response object
     */
    private Response send(Request request) throws IOException, ClassNotFoundException {
        this.objectOut.writeObject(request);
        this.objectOut.flush();
        return (Response) this.objectIn.readObject();
    }

    /**
     * Prints the message to stderr and exits with a return code of 1.
     * @param message  the error message to print to stderr
     */
    private void reportErrorAndExit(String message) {
        System.err.println(message);
        System.exit(1);
    }
}


