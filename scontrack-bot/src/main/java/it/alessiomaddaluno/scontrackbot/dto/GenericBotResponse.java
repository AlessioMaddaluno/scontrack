package it.alessiomaddaluno.scontrackbot.dto;


import it.alessiomaddaluno.scontrackbot.enums.ResponseType;
import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Data @Builder
public class GenericBotResponse {
    private ResponseType type;
    private String text;
    private InlineKeyboardMarkup inlineKeyboard;
    private ReplyKeyboardMarkup replyKeyboard;
    private InputFile photo;
}
