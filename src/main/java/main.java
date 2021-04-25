import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class main {
    public static void main(String[] args) {
        /**
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new MyTbot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
         **/
        ArkPDFProcessor a = new ArkPDFProcessor();

    }
}
