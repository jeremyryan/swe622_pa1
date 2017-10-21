package edu.gmu.swe622.fss;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;

/**
 * Created by jmr on 10/15/2017.
 */
public class FSSClient {

    private Socket sock;
    private ObjectInput objectIn;
    private ObjectOutput objectOut;

    /**
     *
     * @throws IOException
     */
    public FSSClient() throws IOException {
        String serverVar = System.getenv("PA1_SERVER");
        if (serverVar == null) {
            throw new IllegalStateException("environment variable PA1_SERVER must be set");
        }
        String[] serverVarItems = serverVar.split(":");
        String hostName = serverVarItems[0];
        String portParam = serverVarItems[1];
        if (hostName == null) {
            throw new IllegalStateException("no hostname could be found; make sure PA1_SERVER is set: hostname:port");
        }
        if (portParam == null) {
            throw new IllegalStateException("no port could be found; make sure PA1_SERVER is set: hostname:port");
        }
        Integer port = Integer.valueOf(portParam);
        this.sock = new Socket(hostName, port);
        this.objectIn = new ObjectInputStream(this.sock.getInputStream());
        this.objectOut = new ObjectOutputStream(this.sock.getOutputStream());
    }

    /**
     *
     * @param action
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void doAction(Action action, String[] args) throws IOException, ClassNotFoundException {
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
                default:
                    break;
            }
        } catch (IOException exp) {
            System.out.println("Action could not be completed: " + exp.getMessage());
        } finally {
            try {
                this.objectIn.close();
                this.objectOut.close();
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
    }

    private void rm(String fileName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.RM, fileName);
        Response response = this.send(request);
        if (! response.isValid()) {
            this.reportErrorAndExit("File could not be deleted: " + response.getErrorMessage());
        }
    }

    private void mkdir(String dirName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.MKDIR, dirName);
        Response response = this.send(request);
        if (! response.isValid()) {
            this.reportErrorAndExit("Directory could not be created: " + response.getErrorMessage());
        }
    }

    private void dir(String fileName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.DIR, fileName);
        Response response = this.send(request);
        if (! response.isValid()) {
            this.reportErrorAndExit("Directory could not be listed: " + response.getErrorMessage());
        }
    }


    private void rmdir(String dirName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.RMDIR, dirName);
        Response response = this.send(request);
        if (! response.isValid()) {
            this.reportErrorAndExit("Directory could not be removed: " + response.getErrorMessage());
        }
    }

    /**
     *
     * @param localFilePath
     * @param remoteDestination
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void upload(String localFilePath, String remoteDestination) throws IOException, ClassNotFoundException {
        File file = new File(localFilePath);
        if (! file.exists()) {
            this.reportErrorAndExit("File could not be found: " + localFilePath);
        }

        Request request = new Request(Action.UPLOAD, file, remoteDestination, file.getName(), file.length() + "");
        Response response = this.send(request);
        if (! response.isValid()) {
            this.reportErrorAndExit("File could not be uploaded: " + response.getErrorMessage());
        }
    }

    /**
     *
     * @param remoteFile
     * @param destination
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void download(String remoteFile, String destination) throws IOException, ClassNotFoundException {
        Path destinationPath = FileSystems.getDefault().getPath(destination);
        File destinationDir = new File(destination);
        if (! Files.exists(destinationPath)) {
            this.reportErrorAndExit("Destination directory could not be found.");
        }

        Request request = new Request(Action.DOWNLOAD, remoteFile);
        this.objectOut.writeObject(request);
        this.objectOut.flush();

        Response response = (Response) this.objectIn.readObject();
        if (! response.isValid()) {
            this.reportErrorAndExit("File could not be downloaded: " + response.getErrorMessage());
        } else {
            File downloadedFile = response.getFile();
            System.out.println("file length = " + downloadedFile.length());
            Path filePath = FileSystems.getDefault().getPath(destinationPath.toString(), downloadedFile.getName());
            BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(filePath.toFile()));
            Utility.write(new BufferedInputStream(new FileInputStream(downloadedFile)), fileOut);
            fileOut.close();
        }
    }

    /**
     *
     * @param request
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Response send(Request request) throws IOException, ClassNotFoundException {
        this.objectOut.writeObject(request);
        this.objectOut.flush();
        return (Response) this.objectIn.readObject();
    }

    private void reportErrorAndExit(String message) {
        System.out.println(message);
        System.exit(1);
    }
}


