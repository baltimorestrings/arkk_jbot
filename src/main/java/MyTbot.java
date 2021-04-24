import java.util.*;
import java.io.*;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class MyTbot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage msg = new SendMessage();
            msg.setChatId(update.getMessage().getChatId().toString());
            msg.setText("Test thing");
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
}
