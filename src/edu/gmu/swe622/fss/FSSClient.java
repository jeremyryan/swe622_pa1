package edu.gmu.swe622.fss;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;

/**
 * Created by jmr on 10/15/2017.
 */
public class FSSClient {

    private Socket sock;

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
    }

    public void doAction(Action action, String[] args) throws IOException, ClassNotFoundException {
        switch (action) {
            /*
            case RM:
                this.rm(args[0]);
                break;
            case DIR:
                this.dir(args[0]);
                break;
            case MKDIR:
                this.mkdir(args[0]);
                break;
            case RMDIR:
                this.rmdir(args[0]);
                break;
                */
            case UPLOAD:
                this.upload(args[0], args[1]);
                break;
            case DOWNLOAD:
                this.download(args[0], args[1]);
                break;
            default:
                break;
        }
        this.sock.close();
    }
/*
    private void rm(String fileName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.RM, fileName);
        Response response = this.send(request);
    }

    private void dir(String fileName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.DIR, fileName);
        Response response = this.send(request);
    }


    private void mkdir(String dirName) throws IOException, ClassNotFoundException {
        Request request = new Request(Action.MKDIR, dirName);
        this.writeRequest("mkdir " + dirName);
        Response response = this.send(request);
        if (! response.isValid()) {
            System.out.println("Directory could not be created: " + response.getMessage());
            System.exit(1);
        }

    }

    private void rmdir(String dirName) throws IOException {
        String response = this.writeRequest(Action.RMDIR, dirName);
    }
*/
    private void upload(String localFilePath, String remoteDestination) throws IOException, ClassNotFoundException {
        File file = new File(localFilePath);
        if (! file.exists()) {
            System.out.println("File could not be found: " + localFilePath);
            this.sock.close();
            System.exit(1);
        }

        Request request = new Request(Action.UPLOAD, remoteDestination, file.getName(), file.length() + "");
        Response response = this.send(request);
        if (! response.isValid()) {
            System.out.println("File could not be uploaded: " + response.getErrorMessage());
            System.exit(1);
        } else if ("ready".equals(response.getMessage())) {
            ObjectOutput out = new ObjectOutputStream(this.sock.getOutputStream());
            out.writeObject(file);
        } else {
            System.out.println("Unrecognized response: " + response.getMessage());
            System.exit(1);
        }
    }

    private void download(String remoteFile, String destination) throws IOException, ClassNotFoundException {
        Path destinationPath = FileSystems.getDefault().getPath(destination);
        File destinationDir = new File(destination);
        if (! Files.exists(destinationPath)) {
            System.out.println("Destination directory could not be found.");
            System.exit(1);
        }
        Request request = new Request(Action.DOWNLOAD, remoteFile);
        Response response = this.send(request);
        System.out.println("response 1 = " + response.getMessage());
        if ("error".equals(response.getMessage())) {
            System.out.println("File could not be downloaded: ");
            System.exit(1);
        }

        if ("ready".equals(response.getMessage())) {
            File file = (File) new ObjectInputStream(this.sock.getInputStream()).readObject();
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            Files.copy(destinationPath, out);
            out.flush();
            out.close();
        }
    }

    private String writeRequest(Action action, String... args) throws IOException {
        String request = null;
        switch (action.getNumArgs()) {
            case 1:
                request = String.format("%s %s", action.getName(), args[0]);
                break;
            case 2:
                request = String.format("%s %s %s", action.getName(), args[0], args[1]);
                break;
        }

        this.writeRequest(request);
        return this.readResponse();
    }

    private Response send(Request request) throws IOException, ClassNotFoundException {
        ObjectOutput out = new ObjectOutputStream(this.sock.getOutputStream());
        out.writeObject(request);
        out.flush();
        ObjectInput in = new ObjectInputStream(this.sock.getInputStream());
        return (Response) in.readObject();
    }

    private void writeRequest(String message) throws IOException {
        DataOutputStream outStream = new DataOutputStream(this.sock.getOutputStream());
        outStream.writeBytes(message);
        outStream.writeByte('\n');
        outStream.flush();
    }

    private String readResponse() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
        return reader.readLine();
    }
}


