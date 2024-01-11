package it.alessiomaddaluno.scontrackbot.service;

import it.alessiomaddaluno.scontrackbot.dto.ReceiptAIAnalyzerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class RecepitAIAnalyzerService {

    @Value("${azure.receipt-ai-analyzer}")
    private String apiUrl;

    private RestTemplate restTemplate = new RestTemplate();

    public ReceiptAIAnalyzerResponse analyzeRecepitByBlobName(String blobName) throws IOException {
        ResponseEntity<ReceiptAIAnalyzerResponse> responseEntity = restTemplate.getForEntity(apiUrl + "&blobName={blobName}", ReceiptAIAnalyzerResponse.class,blobName);
        ReceiptAIAnalyzerResponse response = responseEntity.getBody();
        return response;
    }

}
