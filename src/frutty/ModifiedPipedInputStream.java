package frutty;

import java.io.*;

public final class ModifiedPipedInputStream extends PipedInputStream {

    public ModifiedPipedInputStream(PipedOutputStream src) throws IOException {
        super(src);
    }

    //Need to modify this method, because PipedInputStream keeps throwing exceptions because the main thread terminates after initialization
    @Override
    public synchronized int read() throws IOException {
        while (in < 0) {
            notifyAll();
            try {
                wait(1000);
            } catch (InterruptedException ex) {
                throw new java.io.InterruptedIOException();
            }
        }
        var ret = buffer[out++] & 0xFF;
        if (out >= buffer.length) {
            out = 0;
        }
        if (in == out) {
            in = -1;
        }

        return ret;
    }
}