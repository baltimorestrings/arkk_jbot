import java.util.*;
import java.io.*;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class MyTbot extends TelegramLongPollingBot {
    /** MyTbot
     *
     * Basic-ass telegram bot, right now only has the one function.
     * */

    public MyTbot(String cfgFile) throws IOException {
        pdfProcessor = new ArkPDFProcessor(cfgFile);
    }

    @Override
    public void onUpdateReceived(Update update) {
        /** Just responds to a message with fund top holdings if it's a fund
         * */
        SendMessage msg = new SendMessage();
        msg.setChatId(update.getMessage().getChatId().toString());
        if (update.hasMessage() && update.getMessage().hasText()) {
            String fund = update.getMessage().getText();
            if (pdfProcessor.hasFund(fund)) {
                msg.setText(pdfProcessor.getPDFFromURL(fund));
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

    @Override
    public String getBotUsername() {
        return System.getenv("TBOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("TBOT_TOKEN");
    }

    private ArkPDFProcessor pdfProcessor;
}
