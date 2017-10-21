package edu.gmu.swe622.fss;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by jmr on 10/18/2017.
 */
public class Test {

    public static void main(String[] args) throws IOException {
        String fileName = "/home/jmr/fss/swe622.sh";
        Path filePath = FileSystems.getDefault().getPath(fileName);
        File file = filePath.toFile();
        System.out.println("length = "+ file.length());
    }

}
