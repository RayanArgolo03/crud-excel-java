package domain.log;

import enums.LogType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private LogType logType;
    private String additionals;
    private LocalDateTime instant;

    public Log(LogType logType, String additionals) {
        this.logType = logType;
        this.additionals = additionals;

        //Inicializa log com instante atual
        this.instant = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "[ " +instant.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                " - " + logType +additionals+ " ]";
    }
}
