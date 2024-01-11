package it.alessiomaddaluno.scontrackbot.command;

import it.alessiomaddaluno.scontrackbot.dto.GenericBotResponse;
import it.alessiomaddaluno.scontrackbot.enums.ResponseType;
import it.alessiomaddaluno.scontrackbot.model.Receipt;
import it.alessiomaddaluno.scontrackbot.model.User;
import it.alessiomaddaluno.scontrackbot.repository.ReceiptRepository;
import it.alessiomaddaluno.scontrackbot.service.AzureBlobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteReceiptByIdCommand {
    
    @Autowired
    private ReceiptRepository receiptRepository;
    
    @Autowired
    private AzureBlobService azureBlobService;

    private final Logger logger = LoggerFactory.getLogger(DeleteReceiptByIdCommand.class);
    
    public GenericBotResponse execute(Long receiptId, User user){
        logger.info("[START] receiptId: {} , user: {}", receiptId, user.getChatId());
        Receipt receipt = this.receiptRepository.findByIdAndUserChatId(receiptId,user.getChatId());
        String blobName = receipt.getBlobName();
        this.receiptRepository.deleteById(receiptId);
        azureBlobService.deleteBlob(blobName);
        logger.info("[END OK]");
        return GenericBotResponse.builder().type(ResponseType.TEXT).text("Eliminato!").build();
    }
    
}
