import java.io.IOException;
import org.apache.logging.log4j.*;
import java.util.*;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArkPDFProcessor {
    public ArkPDFProcessor() {
        log = LogManager.getLogger(ArkPDFProcessor.class);
        ObjectMapper o = new ObjectMapper();
        try {
            Map<String, Object> config = o.readValue(Paths.get("/Users/afrankel02/IdeaProjects/tbot/src/main/resources/config.json").toFile(), Map.class);
            if (config.get("ARK_PDF_LOCATIONS") == null || !(config.get("ARK_PDF_LOCATIONS") instanceof HashMap)) {
                throw new IOException("Invalid config file");
            }
            PDFUrls = (HashMap<String, String>) config.get("ARK_PDF_LOCATIONS");
        } catch (IOException e) {
            log.error("Failed trying to open/process config file");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private final Logger log;
    private Map <String, String> PDFUrls;
}
