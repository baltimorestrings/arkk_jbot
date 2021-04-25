import java.io.*;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.*;

import java.net.http.HttpClient;
import java.util.*;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class ArkPDFProcessor {
    public ArkPDFProcessor() {
        log = LogManager.getLogger(ArkPDFProcessor.class);
        ObjectMapper o = new ObjectMapper();
        http = HttpClients.custom().setUserAgent("Mozilla/5.0 Firefox/26.0").build();
        try {
            pdfStripper = new PDFTextStripper(); // its really stupid that this initializer throws IOE
            Map<String, Object> config = o.readValue(Paths.get("/Users/afrankel02/IdeaProjects/tbot/src/main/resources/config.json").toFile(), Map.class);
            if (config.get("ARK_PDF_LOCATIONS") == null || !(config.get("ARK_PDF_LOCATIONS") instanceof HashMap)) {
                throw new IOException("Invalid config file");
            }
            PDFUrls = (HashMap<String, String>) config.get("ARK_PDF_LOCATIONS");
        } catch (IOException e) {
            log.error("Failed trying to initialize PDF processor.");
            e.printStackTrace();
            System.exit(1);
        }
    }
    public String getPDFFromURL(String fund) {
        try {
            ByteArrayOutputStream inMemPDF = new ByteArrayOutputStream();
            HttpGet req = new HttpGet(PDFUrls.get(fund));
            CloseableHttpResponse ret = http.execute(req);
            ret.getEntity().writeTo(inMemPDF);
            inMemPDF.close();
            PDDocument doc = PDDocument.load(inMemPDF.toByteArray());
            String pdfContents = pdfStripper.getText(doc);
            String s =  Arrays.stream(pdfContents.split("\n")).filter(
                    l -> (l.contains("As of") || l.matches("^1?[0-9] .*"))
            ).collect(Collectors.joining("\n"));
            log.info(s);
            doc.close();
            return s;

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public boolean hasFund(String fund) {
        return PDFUrls.containsKey(fund);
    }

    private final Logger log;
    private final CloseableHttpClient http;
    private PDFTextStripper pdfStripper;
    private Map <String, String> PDFUrls;
}

