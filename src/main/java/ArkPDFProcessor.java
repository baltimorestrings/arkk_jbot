import java.io.*;
import java.nio.file.Paths;

import org.apache.http.client.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.*;

import java.util.*;
import java.util.regex. *;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ArkPDFProcessor {
    /** Takes in fund name, spits out data
     *
     * I should pick and learn a java commenting style
     */
    public ArkPDFProcessor(String cfgFile) throws IOException {
        // init stuff, specifically the mapping of fund name to PDF location - pulled from cfg file
        log = LogManager.getLogger(ArkPDFProcessor.class);
        ObjectMapper o = new ObjectMapper();
        http = HttpClients.custom().setUserAgent("Mozilla/5.0 Firefox/26.0").build();
        pdfStripper = new PDFTextStripper(); // its really stupid that this initializer throws IOE
        Map<String, Object> config = o.readValue(Paths.get(cfgFile).toFile(), Map.class);
        if (config.get("ARK_PDF_LOCATIONS") == null || !(config.get("ARK_PDF_LOCATIONS") instanceof HashMap)) {
            throw new IOException("Invalid config file");
        }
        PDFUrls = (HashMap<String, String>) config.get("ARK_PDF_LOCATIONS");
    }
    public FundHoldingsData getPDFFromURL(String fund) {
        /** curls the PDF into a ByteArray, uses PDFbox to strip it to text and for now just returns top 20 holdings
         */
        try {
            ByteArrayOutputStream inMemPDF = new ByteArrayOutputStream();
            HttpGet req = new HttpGet(PDFUrls.get(fund));
            HttpResponse ret = http.execute(req);
            ret.getEntity().writeTo(inMemPDF);
            inMemPDF.close();
            PDDocument doc = PDDocument.load(inMemPDF.toByteArray());
            String pdfContents = pdfStripper.getText(doc);
            Pattern dateMatcher = Pattern.compile("As of ([0-9/]+)");
            Matcher match = dateMatcher.matcher(pdfContents);
            FundHoldingsData etfHoldings;
            if (match.find())
                etfHoldings = new FundHoldingsData(match.group(1));
            else
                throw new IOException("Bullshit PDF data found trying to process date");
            Arrays.stream(pdfContents.split("\n"))
                    .filter( l -> l.matches("^[0-9]{1,3} .*"))
                    .forEach(etfHoldings::addSecurityFromLogline);
            doc.close();
            return etfHoldings;

        } catch (IOException e) {
            // This should be handled higher up, or it should wait and retry again, then bubble up an Exc idk
            log.error("Some IO shit went down trying to process PDF");
            log.error(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public boolean hasFund(String fund) {
        return PDFUrls.containsKey(fund);
    }

    private final Logger log;
    private final HttpClient http;
    private PDFTextStripper pdfStripper;
    private Map <String, String> PDFUrls;
}

