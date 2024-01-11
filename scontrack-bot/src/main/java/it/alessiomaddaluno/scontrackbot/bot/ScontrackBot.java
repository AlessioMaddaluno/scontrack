package it.alessiomaddaluno.scontrackbot.bot;

import it.alessiomaddaluno.scontrackbot.command.CommandInvoker;
import it.alessiomaddaluno.scontrackbot.dto.GenericBotResponse;
import it.alessiomaddaluno.scontrackbot.enums.CustomBotCommand;
import it.alessiomaddaluno.scontrackbot.model.User;
import it.alessiomaddaluno.scontrackbot.service.UserService;
import it.alessiomaddaluno.scontrackbot.utils.BotMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
public class ScontrackBot extends TelegramLongPollingBot {

    @Value("${telegram.token}")
    private String TELEGRAM_BOT_TOKEN;

    @Value("${telegram.bot-name}")
    private String TELEGRAM_BOT_NAME;

    @Autowired
    private UserService userService;

    @Autowired
    private CommandInvoker commandInvoker;

    @Override
    public String getBotUsername() {
        return TELEGRAM_BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return this.TELEGRAM_BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message;
        if(update.hasCallbackQuery()){
            this.handleCallBackFeedback(update.getCallbackQuery().getId());
            message = update.getCallbackQuery().getMessage();

            message.setText(update.getCallbackQuery().getData());

        }else {
            message = update.getMessage();
        }

        final Long chatId = message.getChatId();
        final String username = message.getFrom().getUserName();

        User user = userService.findUserById(chatId);

        if(user == null){
            SendMessage sendMessage = SendMessage.builder()
                    .text(BotMessageParser.welcomeMessage())
                    .chatId(chatId)
                    .build();
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            this.userService.saveOrUpdate(chatId,username);
            this.setupKeyboard(chatId);
            return;
        }

        CustomBotCommand command = null;

        // Check se ha immagini
        List<PhotoSize> images = message.getPhoto();
        if(images != null && !images.isEmpty()){
           command = CustomBotCommand.UPLOAD_AND_ANALYZE_RECEPIT;
        }
        else {
            if(message.getText().startsWith("/receipt-")){
                command = CustomBotCommand.RECEIPT;
            }
            else if (message.getText().startsWith("/delete-")){
                command = CustomBotCommand.DELETE_RECEIPT;
            }else {
                command = CustomBotCommand.fromLabel(message.getText());
            }

        }

        if(command != null){
            try {
                GenericBotResponse response = commandInvoker.invokeCommand(command,message,this,getBotToken(),user);
                this.sendResponse(response,chatId);
            } catch (TelegramApiException | IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            this.setupKeyboard(chatId);
        }



    }

    private void handleCallBackFeedback(String callBackId){
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callBackId)
                .showAlert(false)
                .build();
        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupKeyboard(Long chatId){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardButton btnReportCurrentMonth = new KeyboardButton(CustomBotCommand.REPORT_CURRENT_MONTH.label);
        KeyboardButton btnReportNextMonth = new KeyboardButton(CustomBotCommand.REPORT_PREVIOUS_MONTH.label);
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(btnReportCurrentMonth);
        keyboardRow1.add(btnReportNextMonth);
        keyboard.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboard);
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Seleziona un comando o scatta una foto ad uno scontrino per tracciare la spesa.")
                .replyMarkup(replyKeyboardMarkup)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendResponse(GenericBotResponse response, Long chatId) throws TelegramApiException {
        SendMessage message;
        switch (response.getType()) {
            case TEXT ->
                    message = SendMessage.builder().chatId(chatId).parseMode("html").text(response.getText()).build();
            case MARKDOWN ->
                    message = SendMessage.builder().chatId(chatId).parseMode("Markdown").text(response.getText()).replyMarkup(response.getInlineKeyboard()).build();
            case IMAGE -> {
                System.out.println(response.getInlineKeyboard());
                SendPhoto photoMessage = SendPhoto.builder()
                        .photo(response.getPhoto())
                        .replyMarkup(response.getReplyKeyboard())
                        .chatId(chatId)
                        .build();
                execute(photoMessage);
                return;
            }
            default -> message = null;
        }
        execute(message);
    }


}
