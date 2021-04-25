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

/**
 * So I guess in java the comments go before the class? weird.
 *
 * ArkPDFProcessor loads a config file containing urls for all the ark funds.
 *
 * it handles pulling ARK holding info for all funds it supports and returning it in 
 * a FundHoldingsData object format.
 * */
public class ArkPDFProcessor {
    /**
     * Sets up the processor, loading config file data and stting up HttpClient
     *
     * @throws IOException if config file doesn't fit expected format.
     *
     * @param cfgFile String representing a .json holding ARK fund name and URL info
     */
    public ArkPDFProcessor(String cfgFile) throws IOException {
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

    /**
     * Takes a fund name, pulls PDF of the daily holdings report and processes it into a useful format
     *
     * TODO: move the error catching higher up so tbot can know if stuff failed and decide retry/bail
     * 
     * @param fund fund name EG "ARKK", "ARKG"
     *
     * @return FundHoldingsData object filled up if success, null if fund doesn't exit.
     * */
    public FundHoldingsData getPDFFromURL(String fund) {
        ByteArrayOutputStream inMemPDF = new ByteArrayOutputStream();
        HttpGet req = new HttpGet(PDFUrls.get(fund));
        try {
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

    /**
     * Checks for fund existence
     * @param fund name of fund to check
     *
     * @return True/False based on it we have info for a fund with that name
     * */
    public boolean hasFund(String fund) {
        return PDFUrls.containsKey(fund);
    }

    private final Logger log;
    private final HttpClient http;
    private PDFTextStripper pdfStripper;
    private Map <String, String> PDFUrls;
}

