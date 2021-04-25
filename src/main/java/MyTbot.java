import java.util.*;
import java.io.*;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/** MyTbot
 *
 * Basic-ass telegram bot, right now only has the one function.
 * */
public class MyTbot extends TelegramLongPollingBot {
    /**
     * Just sets up our PDF processor
     * @param cfgFile config file with fund url data
     * @throws IOException if config file fails, bubble up the exception so the main app can handle comms
     */
    public MyTbot(String cfgFile) throws IOException {
        pdfProcessor = new ArkPDFProcessor(cfgFile);
    }

    /**
     * This function is called whenever bot receives a message.
     *
     * If it gets a valid fund name, it'll try to grab the latest PDF and process it
     * @param update update info from bot API
     */
    @Override public void onUpdateReceived(Update update) {
        /** Just responds to a message with fund top holdings if it's a fund
         * */
        SendMessage msg = new SendMessage();
        msg.setChatId(update.getMessage().getChatId().toString());
        if (update.hasMessage() && update.getMessage().hasText()) {
            String fund = update.getMessage().getText();
            if (pdfProcessor.hasFund(fund)) {
                msg.setText(pdfProcessor.getPDFFromURL(fund).toString());
            } else {
                msg.setText("No fund with that name found.");
            }
            try {
                execute(msg);
            } catch (TelegramApiException e) {
                System.out.println("Issue sending message.");
                e.printStackTrace();    
            }
        }

    }

    /**
     * Needed for bot aPI stuff
     * @return username
     */
    @Override public String getBotUsername() {
        return System.getenv("TBOT_USERNAME");
    }

    /**
     * Needed for bot aPI stuff
     * @return @botfather token
     */
    @Override public String getBotToken() {
        return System.getenv("TBOT_TOKEN");
    }

    private ArkPDFProcessor pdfProcessor;
}
