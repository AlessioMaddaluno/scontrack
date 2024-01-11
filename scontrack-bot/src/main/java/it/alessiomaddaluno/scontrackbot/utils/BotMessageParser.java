package it.alessiomaddaluno.scontrackbot.utils;

import it.alessiomaddaluno.scontrackbot.enums.ReceiptType;
import it.alessiomaddaluno.scontrackbot.model.Receipt;

import java.time.format.DateTimeFormatter;

public class BotMessageParser {

    public static String welcomeMessage(){
        return """
                Ciao! Benvenuto su Scontrack! Tramite questo bot potrai tracciare gli scontrini delle piccole spese in modo tale da poter
                riportare le spese nel tuo sistema di budgeting! Per farlo scatta una foto ad uno scontrino e attendi la sua analisi!
                """;
    }

    public static String recepitAnalysisMessage(Receipt receipt){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String category = parseCategory(receipt.getCategory());

        return String.format("""
                
                <strong>🛍️ Commerciante</strong>: <code>%s</code>
                <strong>🟦 Categoria</strong>:  <code>%s</code>
                <strong>📅 Data</strong>:  <code>%s</code>
                <strong>💸 Totale</strong>:  <code>%.2f €</code>
                
                """, receipt.getMerchantName(), category, formatter.format(receipt.getTransactionDate()), receipt.getTotal());
    }

    public static String recepitAnalysisErrorMessage(){
        return "Quello a me non sembra uno scontrino! 👀 Potresti rifare la foto in condizioni migliori? 🤨📸";
    }

    public static String noReceiptFoundErrorMessage(){
        return "Non ci sono scontrini nel periodo richiesto! 😣";
    }

    private static String parseCategory(ReceiptType recepitType){
        String parsedCategory = null;
        switch (recepitType) {
            case GAS -> parsedCategory = "Carburante ⛽";
            case FOOD -> parsedCategory = "Cibo 🍅";
            case HOTEL -> parsedCategory = "Hotel 🏨";
            case PARKING -> parsedCategory = "Parcheggio 🅿️";
            case CREDIT_CARD -> parsedCategory = "Carta di Credito 💳";
            case OTHER -> parsedCategory = "Altro 🧾";
        }
        return parsedCategory;
    }

    public static String parseCategoryIcon(ReceiptType receiptType){
        String parsedCategoryIcon = null;
        switch (receiptType) {
            case GAS -> parsedCategoryIcon = "⛽";
            case FOOD -> parsedCategoryIcon = "🍅";
            case HOTEL -> parsedCategoryIcon = "🏨";
            case PARKING -> parsedCategoryIcon = "🅿️";
            case OTHER -> parsedCategoryIcon = "🧾";
        }
        return parsedCategoryIcon;
    }

    public static String receiptReportMessage(int numberOfRecepits, double total, int month){
        return String.format("""
                    Report scontrini mese di `%s`:
                    
                    🧾 Numero scontrini inseriti: `%d`
                    💸 Totale importo: `%.2f €`
                    
                    """, months[month-1],numberOfRecepits,total);
    }

    private static final String[] months = {"GENNAIO","FEBBRAIO","MARZO","APRILE","MAGGIO","GIUGNO","LUGLIO","AGOSTO","SETTEMBRE"
            ,"OTTOBRE","NOVEMBRE","DICEMBRE"};

}
