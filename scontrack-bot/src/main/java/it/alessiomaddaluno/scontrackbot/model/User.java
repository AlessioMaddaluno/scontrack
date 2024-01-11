package it.alessiomaddaluno.scontrackbot.model;




import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "BOT_USER")
@Data
public class User {

    @Id
    private Long chatId;

    @NotNull
    private String username;

}
