package edu.gmu.swe622.fss;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jmr on 10/18/2017.
 */
public final class Utility {
    public static int write(InputStream in, OutputStream out) throws IOException {
        int b, bytesWritten = 0;
        while (-1 != (b = in.read())) {
            out.write(b);
            bytesWritten++;
        }
        out.flush();
        return bytesWritten;
    }
}
