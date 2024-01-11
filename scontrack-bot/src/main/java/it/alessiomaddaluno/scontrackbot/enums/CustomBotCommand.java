package it.alessiomaddaluno.scontrackbot.enums;

public enum CustomBotCommand {

    UPLOAD_AND_ANALYZE_RECEPIT("/upload-and-analyze-recepit"),
    REPORT_CURRENT_MONTH("Report Mese Corrente"),
    REPORT_PREVIOUS_MONTH("Report Mese Precedente"),
    OTHER_COMMANDS("Altri comandi"),

    ALL_RECEIPTS_CURRENT_MOTNH("/list-current-month"),
    ALL_RECEIPTS_PREVIOUS_MONTH("Mostra tutti gli scontrini del mese precedente"),
    RECEIPT("Mostra scontrino"),
    DELETE_RECEIPT("Cancella scontrino");

    public final String label;

     CustomBotCommand(String value){
        this.label = value;
    }

    public static CustomBotCommand fromLabel(String label) {
        for (CustomBotCommand command : CustomBotCommand.values()) {
            if (command.label.equals(label)) {
                return command;
            }
        }
        return null;
    }



}
