package it.alessiomaddaluno.scontrackbot.service;

import it.alessiomaddaluno.scontrackbot.model.Receipt;
import it.alessiomaddaluno.scontrackbot.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    public Receipt save(Receipt receipt){
        Receipt savedReceipt = this.receiptRepository.save(receipt);
        return savedReceipt;
    }


}
