package it.alessiomaddaluno.scontrackbot.command;

import it.alessiomaddaluno.scontrackbot.dto.GenericBotResponse;
import it.alessiomaddaluno.scontrackbot.enums.CustomBotCommand;
import it.alessiomaddaluno.scontrackbot.enums.ResponseType;
import it.alessiomaddaluno.scontrackbot.model.User;
import it.alessiomaddaluno.scontrackbot.service.AzureBlobService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.LocalDate;


@Component
public class CommandInvoker {

    @Autowired
    private AzureBlobService azureBlobService;

    @Autowired
    private BeanFactory beanFactory;

    public GenericBotResponse invokeCommand(CustomBotCommand command, Message message, AbsSender absSender, String token, User user) throws TelegramApiException, IOException {
        GenericBotResponse response = null;
        int month,year;
        LocalDate now = LocalDate.now();
        String[] parts;
        System.out.println(command.label);
        switch (command) {
            case UPLOAD_AND_ANALYZE_RECEPIT -> {
                String imageUrl = this.extractUrlImageFromMessage(message, absSender, token);
                UploadAndAnalyzeReceiptCommand uploadAndAnalyzeReceiptCommand = beanFactory.getBean(UploadAndAnalyzeReceiptCommand.class);
                response = uploadAndAnalyzeReceiptCommand.execute(imageUrl, user);
                System.out.println(response);
            }
            case REPORT_CURRENT_MONTH, REPORT_PREVIOUS_MONTH -> {
                year = now.getYear();
                month = now.getMonthValue();
                GetMonthReportByMonthCommand getReportCurrentMonthCommand = beanFactory.getBean(GetMonthReportByMonthCommand.class);
                if (command == CustomBotCommand.REPORT_PREVIOUS_MONTH) {
                    if (month - 1 == 0) {
                        month = 12;
                        year -= 1;
                    } else {
                        month -= 1;
                    }
                }
                response = getReportCurrentMonthCommand.execute(month, year, user);
                if (command == CustomBotCommand.REPORT_PREVIOUS_MONTH && response.getType().equals(ResponseType.MARKDOWN)) {
                    response.getInlineKeyboard().getKeyboard().get(0).get(0).setCallbackData(CustomBotCommand.ALL_RECEIPTS_PREVIOUS_MONTH.label);
                }
            }
            case ALL_RECEIPTS_CURRENT_MOTNH, ALL_RECEIPTS_PREVIOUS_MONTH -> {
                year = now.getYear();
                month = now.getMonthValue();
                if (command == CustomBotCommand.ALL_RECEIPTS_PREVIOUS_MONTH) {
                    if (month - 1 == 0) {
                        month = 12;
                        year -= 1;
                    } else {
                        month -= 1;
                    }
                }
                GetReceiptsByMonthAndYearCommand getReceiptsByMonthAndYearCommand = beanFactory.getBean(GetReceiptsByMonthAndYearCommand.class);
                response = getReceiptsByMonthAndYearCommand.execute(month, year, user);
            }
            case RECEIPT -> {
                parts = message.getText().split("-");
                GetReceiptByIdCommand getReceiptByIdCommand = beanFactory.getBean(GetReceiptByIdCommand.class);
                response = getReceiptByIdCommand.execute(Long.parseLong(parts[1]), user);
            }
            case DELETE_RECEIPT -> {
                parts = message.getText().split("-");
                Long receiptId = Long.parseLong(parts[1]);
                DeleteReceiptByIdCommand deleteReceiptByIdCommand = beanFactory.getBean(DeleteReceiptByIdCommand.class);
                response = deleteReceiptByIdCommand.execute(receiptId, user);
            }
            default ->
                // Comando sconosciuto
                response = GenericBotResponse.builder().type(ResponseType.TEXT).text("Comando sconosciuto").build();
        }
        return response;
    }

    private String extractUrlImageFromMessage(Message message,AbsSender absSender, String token) throws TelegramApiException {
        // La foto più recente (la più grande) si trova all'ultimo indice della lista
        PhotoSize photo = message.getPhoto().get(message.getPhoto().size()-1);
        // Ottieni il file ID della foto
        String fileId = photo.getFileId();
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(fileId);
        File file = absSender.execute(getFileMethod);
        String filePath = file.getFilePath();
        String telegramFileUrl = "https://api.telegram.org/file/bot" + token + "/" + filePath;
        return telegramFileUrl;
    }



}
