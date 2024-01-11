package it.alessiomaddaluno.scontrackbot.command;

import it.alessiomaddaluno.scontrackbot.dto.GenericBotResponse;
import it.alessiomaddaluno.scontrackbot.enums.ResponseType;
import it.alessiomaddaluno.scontrackbot.model.Receipt;
import it.alessiomaddaluno.scontrackbot.model.User;
import it.alessiomaddaluno.scontrackbot.repository.ReceiptRepository;
import it.alessiomaddaluno.scontrackbot.utils.BotMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class GetReceiptsByMonthAndYearCommand {

    @Autowired
    private ReceiptRepository receiptRepository;

    public GenericBotResponse execute(int month, int year, User user){
        GenericBotResponse response = null;
        List<Receipt> receiptList = this.receiptRepository.findByMonthAndYear(month,year,user.getChatId());
        System.out.println(receiptList + " " + month + " " + year);
        if(!receiptList.isEmpty()){
            response = GenericBotResponse.builder()
                    .type(ResponseType.MARKDOWN).text("Lista degli scontrini:")
                    .inlineKeyboard(generateKeyBoard(receiptList)).build();
        }

        return response;
    }

    private InlineKeyboardMarkup generateKeyBoard(List<Receipt> receiptList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row;
        InlineKeyboardButton inlineKeyboardBtn;
        InlineKeyboardButton delKeyboardBtn;
        String btnText;
        for (Receipt receipt : receiptList) {
            row = new ArrayList<>();
            inlineKeyboardBtn = new InlineKeyboardButton();
            btnText = String.format("%s %s - %s - %.2f€", BotMessageParser.parseCategoryIcon(receipt.getCategory()),
                    formatter.format(receipt.getTransactionDate()), receipt.getMerchantName(), receipt.getTotal());
            inlineKeyboardBtn.setText(btnText);
            inlineKeyboardBtn.setCallbackGame(null);
            inlineKeyboardBtn.setCallbackData("/receipt-" + receipt.getId());
            row.add(inlineKeyboardBtn);
            keyboard.add(row);

            row = new ArrayList<>();
            delKeyboardBtn = new InlineKeyboardButton();
            delKeyboardBtn.setText("🗑️");
            delKeyboardBtn.setCallbackData("/delete-" + receipt.getId());
            row.add(delKeyboardBtn);
            keyboard.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
