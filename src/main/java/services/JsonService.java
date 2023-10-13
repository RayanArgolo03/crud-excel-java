package services;

import com.google.gson.*;
import domain.client.Client;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonService {

    public static boolean getResponse(String folderPath, Client clientRead) throws IOException {
        File jsonFile = new File(folderPath + "\\response.json");
        serializer(jsonFile, clientRead);

        //Somente retorna se não lançar exception na serialização
        return true;
    }

    private static void serializer(File jsonFile, Client clientRead) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))) {

            //Registra o adaptador para LocalDateTime
            GsonBuilder gsonBuilderDate = new GsonBuilder()
                    .registerTypeAdapter(
                            LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                                    (localDateTime, type, context) -> {
                                        String formattedDateTime =
                                                localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                                        return new JsonPrimitive(formattedDateTime);
                                    }
                    );

            //Cria um objeto GSON personalizado para formatação
            Gson gsonObject = gsonBuilderDate.serializeNulls()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .create();

            //Serializa o objeto para escrever no arquivo e escreve-o
            String jsonClient = gsonObject.toJson(clientRead);
            writer.write(jsonClient);
            writer.flush();
        }
    }

}


