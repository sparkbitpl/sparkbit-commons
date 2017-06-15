package pl.sparkbit.commons.restlogger;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter printWriter;
    private final Charset charset = StandardCharsets.UTF_8;

    private boolean usingOutputStream;
    private boolean usingWriter;

    ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (usingWriter) {
            throw new IllegalStateException("getWriter() has already been called for this response");
        }
        usingOutputStream = true;
        if (outputStream == null) {
            outputStream = new TeeServletOutputStream(getResponse().getOutputStream(), buffer);
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (usingOutputStream) {
            throw new IllegalStateException("getOutputStream() has already been called for this response");
        }
        usingWriter = true;
        if (printWriter == null) {
            printWriter = new TeePrintWriter(getResponse().getWriter(),
                    new PrintWriter(new OutputStreamWriter(buffer, charset)));
        }
        return printWriter;
    }

    String asString(String prompt) throws IOException {
        StringBuilder logBuilder = new StringBuilder(prompt).append("Outgoing response body:\n");
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        List<String> lines;
        lines = IOUtils.readLines(is, charset);
        for (String line : lines) {
            logBuilder.append(prompt).append(line).append('\n');
        }
        return logBuilder.deleteCharAt(logBuilder.length() - 1).toString();
    }
}
