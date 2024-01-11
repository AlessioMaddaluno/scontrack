package it.alessiomaddaluno.scontrackbot.command;

import it.alessiomaddaluno.scontrackbot.dto.GenericBotResponse;
import it.alessiomaddaluno.scontrackbot.dto.ReceiptAIAnalyzerResponse;
import it.alessiomaddaluno.scontrackbot.enums.ResponseType;
import it.alessiomaddaluno.scontrackbot.model.Receipt;
import it.alessiomaddaluno.scontrackbot.model.User;
import it.alessiomaddaluno.scontrackbot.service.AzureBlobService;
import it.alessiomaddaluno.scontrackbot.service.ReceiptService;
import it.alessiomaddaluno.scontrackbot.service.RecepitAIAnalyzerService;
import it.alessiomaddaluno.scontrackbot.utils.BotMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class UploadAndAnalyzeReceiptCommand {

    @Autowired
    private AzureBlobService azureBlobService;

    @Autowired
    private RecepitAIAnalyzerService recepitAIAnalyzerService;

    @Autowired
    private ReceiptService receiptService;


    public GenericBotResponse execute(String imageUrl, User user) throws IOException {

        GenericBotResponse response = null;

        UUID uuid = UUID.randomUUID();
        String blobName = uuid.toString();

        azureBlobService.saveBlob(blobName,imageUrl);

        ReceiptAIAnalyzerResponse receiptAIAnalyzerResponse = recepitAIAnalyzerService.analyzeRecepitByBlobName(blobName);
        if(receiptAIAnalyzerResponse.isValid()){

            Receipt receipt = new Receipt();
            receipt.setCategory(receiptAIAnalyzerResponse.getReceiptType());
            receipt.setUser(user);
            receipt.setMerchantName(receiptAIAnalyzerResponse.getMerchantName().getValue());
            receipt.setTotal(Float.valueOf(receiptAIAnalyzerResponse.getTotal().getValue()));
            receipt.setTransactionDate(LocalDate.parse(receiptAIAnalyzerResponse.getTransactionDate().getValue()));
            receipt.setBlobName(blobName);

            Receipt receiptSaved = receiptService.save(receipt);
            response = GenericBotResponse.builder().type(ResponseType.TEXT).text(BotMessageParser.recepitAnalysisMessage(receiptSaved)).build();

        }else {
            this.azureBlobService.deleteBlob(blobName);
            response = GenericBotResponse.builder().type(ResponseType.MARKDOWN).text(BotMessageParser.recepitAnalysisErrorMessage()).build();
        }

        return response;

    }

}
