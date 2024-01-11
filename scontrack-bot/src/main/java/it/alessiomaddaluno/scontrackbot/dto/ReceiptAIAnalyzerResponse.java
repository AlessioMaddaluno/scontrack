package it.alessiomaddaluno.scontrackbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.alessiomaddaluno.scontrackbot.enums.ReceiptType;
import lombok.Data;

@Data
public class ReceiptAIAnalyzerResponse {
    @JsonProperty("receipt_type")
    private ReceiptType receiptType;
    @JsonProperty("is_valid")
    private boolean isValid;
    @JsonProperty("merchant_name")
    private ConfidenceValue merchantName;
    @JsonProperty("transaction_date")
    private ConfidenceValue transactionDate;
    @JsonProperty("total")
    private ConfidenceValue total;
    @Data
    public static class ConfidenceValue {
        private String value;
        private double confidence;
    }

}
