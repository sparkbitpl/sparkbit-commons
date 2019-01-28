package pl.sparkbit.commons.restlogger;

import java.io.PrintWriter;

public class TeePrintWriter extends PrintWriter {

    private final PrintWriter branch;

    TeePrintWriter(PrintWriter writer, PrintWriter branch) {
        super(writer);
        this.branch = branch;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public synchronized void write(char[] buf, int off, int len) {
        super.write(buf, off, len);
        branch.write(buf, off, len);
        branch.flush();
    }

    @Override
    public void close() {
        super.close();
        branch.close();
    }

    @Override
    public void flush() {
        super.flush();
        branch.flush();
    }
}
