package pl.sparkbit.commons.restlogger;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * This class will not work for forms submitted from HTML pages.
 * Tomcat checks that getOutputStream has already been called and ignores all POST body.
 * Check: org.apache.catalina.connector.Request#parseParameters()
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private String log;
    private ServletInputStream sis;
    private BufferedReader bufferedReader;

    private boolean usingInputStream = false;
    private boolean usingReader = false;

    RequestWrapper(HttpServletRequest request, String prompt) throws IOException {
        super(request);

        StringBuilder logBuilder = new StringBuilder(prompt).append("Incoming request body:\n");
        Charset charset = StandardCharsets.UTF_8;
        BufferedReader reader = new BufferedReader(new InputStreamReader(getRequest().getInputStream(), charset));
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line).append('\n');
            line = hidePassword(line);
            logBuilder.append(prompt).append(line).append('\n');
        }
        sis = new SimpleServletInputStream(new ByteArrayInputStream(body.toString().getBytes(charset)));
        log = logBuilder.deleteCharAt(logBuilder.length() - 1).toString();

        logBuilder.append(prompt).append('\n');
    }

    private String hidePassword(String line) {
        return line.replaceAll("\"password\"(\\s*):(\\s*)\"[^\"]*\"", "\"password\"$1:$2\"**********\"");
    }

    @Override
    public ServletInputStream getInputStream() {
        if (usingReader) {
            throw new IllegalStateException("getReader() has already been called for this response");
        }
        usingInputStream = true;
        return sis;
    }

    @Override
    public BufferedReader getReader() {
        if (usingInputStream) {
            throw new IllegalStateException("getInputStream() has already been called for this response");
        }
        usingReader = true;

        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(sis, StandardCharsets.UTF_8));
        }
        return bufferedReader;
    }

    String asString() {
        return log;
    }
}
