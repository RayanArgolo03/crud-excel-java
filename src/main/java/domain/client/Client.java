package domain.client;

import enums.*;
import exceptions.ClientException;
import interfaces.Validatable;
import services.EnumProviderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Client implements Validatable {
    private String name;
    private int age;
    private ClientType clientType;
    private Document document;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;

    //Programação defensiva do cliente
    public Client(String name, int age, ClientType clientType, Document document, LocalDateTime creationDate, LocalDateTime lastUpdateDate) {

        if (!validName(name)) throw new ClientException("Nome de cliente inválido!");
        this.name = name;

        if (!validAge(age)) throw new ClientException("Idade de cliente inválida! Acima de 123 ou abaixo de 18 anos!");
        this.age = age;

        if (!validObject(clientType)) throw new ClientException("Tipo de cliente inválido!");
        this.clientType = clientType;

        if (!validObject(document) || !validDocument(document.getContent(), document.getDocumentType()))
            throw new ClientException("Documento de cliente inválido!");
        this.document = document;

        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    //Sobrecarga de construtor para comparação
    public Client(String name, Document document) {

        if (!validName(name)) throw new ClientException("Nome inválido!");
        this.name = name;

        if (!validObject(document)) throw new ClientException("Documento inválido!");
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(document, client.document);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document);
    }

    //Retorna atributos do cliente para escrita no DB
    public List<Object> getAtributtesValues() {
        return Arrays.asList
                (getName(),
                        getAge(),
                        getClientType(),
                        getDocument().getDocumentType(),
                        getDocument().getContent(),
                        getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        getLastUpdateDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder().append("\n");
        List<String> atributtes = getAtributtesValues().stream().map(Object::toString).toList();
        List<ExcelField> fields = EnumProviderService.getOptions(ExcelField.class);

        //Percorre todos os atributos em string, substituindo underscores
        for (int i = 0; i < atributtes.size(); i++) {

            String atributte = atributtes.get(i);
            if (atributte.contains("_")) atributte = atributte.replace("_", " ");

            String field = fields.get(i).toString();
            if (field.contains("_")) field = field.replace("_", " ");

            sb.append(field).append(": ").append(atributte).append("\n");

        }
        return sb.toString();
    }
}
