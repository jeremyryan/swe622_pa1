package edu.gmu.swe622.fss;

import java.io.*;
import java.net.Socket;

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

    public void doAction(Action action, String[] args) throws IOException {
        switch (action) {
            case RM:
                rm(args[0]);
                break;
            case DIR:
                dir(args[0]);
                break;
            case DOWNLOAD:
                download(args[0], args[1]);
                break;
            case MKDIR:
                mkdir(args[0]);
                break;
            case RMDIR:
                rmdir(args[0]);
                break;
            case UPLOAD:
                upload(args[0], args[1]);
                break;
            default:
                break;
        }
        this.sock.close();
    }

    private void rm(String fileName) throws IOException {
        String response = this.writeRequest(Action.RM, fileName);
    }

    private void dir(String fileName) throws IOException {
        String response = this.writeRequest(Action.DIR, fileName);
    }

    private void download(String remoteFile, String localDestination) throws IOException {
        String response = this.writeRequest(Action.DOWNLOAD, remoteFile);
    }

    private void mkdir(String dirName) throws IOException {
        this.writeRequest("mkdir " + dirName);
        String response = this.writeRequest(Action.MKDIR, dirName);

    }

    private void rmdir(String dirName) throws IOException {
        String response = this.writeRequest(Action.RMDIR, dirName);
    }

    private void upload(String localFilePath, String remoteDestination) throws IOException {
        File file = new File(localFilePath);
        if (! file.exists()) {
            System.out.println("File could not be found: " + localFilePath);
            this.sock.close();
            System.exit(1);
        }

        String response = this.writeRequest(Action.UPLOAD, localFilePath, remoteDestination);

        if ("error".startsWith(response)) {
            System.out.println("File could not be uploaded: ");
            System.exit(1);
        } else if ("ready-for-file".equals(response)) {
            BufferedOutputStream fileOutStream = new BufferedOutputStream(this.sock.getOutputStream());
            BufferedInputStream fileInStream = new BufferedInputStream(new FileInputStream(file));
            Utility.write(fileInStream, fileOutStream);
            fileInStream.close();
        } else {
            System.out.println("Unrecognized response: " + response);
            System.exit(1);
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


