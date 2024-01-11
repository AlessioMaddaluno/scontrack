package it.alessiomaddaluno.scontrackbot.command;

import it.alessiomaddaluno.scontrackbot.dto.GenericBotResponse;
import it.alessiomaddaluno.scontrackbot.enums.ResponseType;
import it.alessiomaddaluno.scontrackbot.model.Receipt;
import it.alessiomaddaluno.scontrackbot.model.User;
import it.alessiomaddaluno.scontrackbot.repository.ReceiptRepository;
import it.alessiomaddaluno.scontrackbot.service.AzureBlobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.games.CallbackGame;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class GetReceiptByIdCommand {


    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private AzureBlobService azureBlobService;


    public GenericBotResponse execute(Long receipitId, User user) throws IOException {

        Receipt receipt = receiptRepository.findByIdAndUserChatId(receipitId,user.getChatId());
        InputStream inputStream = azureBlobService.downloadFile(receipt.getBlobName());

        InputFile inputFile =  new InputFile();
        inputFile.setMedia(inputStream,receipt.getBlobName());

        GenericBotResponse response = GenericBotResponse.builder()
                .photo(inputFile)
                .type(ResponseType.IMAGE)
                .inlineKeyboard(generateKeyBoard(receipitId))
                .build();

        return response;
    }

    private InlineKeyboardMarkup generateKeyBoard(long receipitId){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        // Aggiungi pulsanti inline
        InlineKeyboardButton inlineButton = new InlineKeyboardButton();
        inlineButton.setText("Elimina scontrino");
        inlineButton.setCallbackData("/delete-"+receipitId);
        inlineButton.setCallbackGame(new CallbackGame(){});
        row.add(inlineButton);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
