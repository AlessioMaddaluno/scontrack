package it.alessiomaddaluno.scontrackbot.command;

import it.alessiomaddaluno.scontrackbot.dto.GenericBotResponse;
import it.alessiomaddaluno.scontrackbot.enums.CustomBotCommand;
import it.alessiomaddaluno.scontrackbot.enums.ResponseType;
import it.alessiomaddaluno.scontrackbot.model.Receipt;
import it.alessiomaddaluno.scontrackbot.model.User;
import it.alessiomaddaluno.scontrackbot.repository.ReceiptRepository;
import it.alessiomaddaluno.scontrackbot.utils.BotMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.games.CallbackGame;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetMonthReportByMonthCommand {

    @Autowired
    private ReceiptRepository receiptRepository;

    GenericBotResponse execute(int month, int year, User user){
        List<Receipt> receiptList = this.receiptRepository.findByMonthAndYear(month,year,user.getChatId());
        String textResponse;
        GenericBotResponse response = null;
        if(receiptList.isEmpty()){
            textResponse = BotMessageParser.noReceiptFoundErrorMessage();
            response = GenericBotResponse.builder().type(ResponseType.TEXT).text(textResponse).build();
        }
        else {
            int numberOfReceipts = receiptList.size();
            double total = receiptList.stream().mapToDouble(Receipt::getTotal).sum();
            textResponse = BotMessageParser.receiptReportMessage(numberOfReceipts,total,month);
            response = GenericBotResponse.builder().type(ResponseType.MARKDOWN).text(textResponse).inlineKeyboard(this.generateKeyBoard()).build();
        }
        return response;
    }

    private InlineKeyboardMarkup generateKeyBoard(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        // Aggiungi pulsanti inline
        InlineKeyboardButton inlineButton = new InlineKeyboardButton();
        inlineButton.setText("Mostra lista scontrini");
        inlineButton.setCallbackData(CustomBotCommand.ALL_RECEIPTS_CURRENT_MOTNH.label);
        inlineButton.setCallbackGame(new CallbackGame(){});
        row.add(inlineButton);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }



}
