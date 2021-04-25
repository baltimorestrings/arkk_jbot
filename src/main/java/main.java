import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileNotFoundException;
import java.io.IOException;

public class main {
    public static void main(String[] args) {
        /** Init block
         */
        Logger log = LogManager.getLogger("main");
        MyTbot bot = null;
        try {
            bot = new MyTbot("target/classes/config.json");
        } catch (FileNotFoundException e) {
            log.error("Couldn't find file \"" + e.getMessage().split(" ")[0] + "\", exiting...");
            System.exit(1);
        } catch (IOException e) {
            log.error("Unknown error encountered processing config file / initializing bot:");
            e.printStackTrace();
            System.exit(1);
        }
        /** Making two separate try blocks so I can catch IOExcept from HTTP shit later, butttt might be
         *  unnecessary as I think telegram thing only raises telegram exceptions.
         *
         *  Only thing to catch would be http errors from PDF dl, which we can make a custom runtime exc for
         *  or just return null or some java shit like that.
         */
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
