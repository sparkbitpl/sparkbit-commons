package pl.sparkbit.commons.restlogger;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import org.apache.commons.io.output.TeeOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class TeeServletOutputStream extends ServletOutputStream {

    private final OutputStream teeOutputStream;

    TeeServletOutputStream(OutputStream os1, OutputStream os2) {
        this.teeOutputStream = new TeeOutputStream(os1, os2);
    }

    @Override
    public void write(int b) throws IOException {
        teeOutputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        teeOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        teeOutputStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        teeOutputStream.flush();
    }

    @Override
    public void close() throws IOException {
        teeOutputStream.close();
    }

    @Override
    public boolean isReady() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        throw new UnsupportedOperationException();
    }
}
