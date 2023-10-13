package services;

import domain.client.*;
import enums.*;
import exceptions.ServiceException;
import interfaces.*;

import java.time.LocalDateTime;
import java.util.*;

public class ClientService implements Validatable {
    public ClientService() {
    }

    public Client createClient() {

        System.out.println();
        System.out.println("Nome do cliente: (primeiro nome e sobrenome começando com maiúsculo!)");

        //Serviço estático para leitura de dados
        ReadService.readNextLine();
        String name = ReadService.readNextLine();
        if (!validName(name)) {
            throw new ServiceException("Nome com formato inválido!");
        }

        System.out.println();
        System.out.println("Idade do cliente: (somente números)");
        int age = ReadService.readInt();
        if (!validAge(age)) {
            throw new ServiceException("Idade inválida! Menor de idade ou acima do permitido (123 anos)!");
        }

        System.out.println();
        System.out.println("Tipo do cliente: ");
        PrinterService.printEnums(EnumProviderService.getOptions(ClientType.class));

        System.out.print("Sua escolha: ");
        int opcClientType = ReadService.readInt();

        //Gera: tipo de cliente passando lista e opção escolhida + documento pelo tipo gerado
        ClientType clientType = generateClientType(EnumProviderService.getOptions(ClientType.class), opcClientType);
        Document document = generateDocument(clientType);

        return new Client(name, age, clientType, document, LocalDateTime.now(), LocalDateTime.now());
    }


    public Client getClientData() {

        System.out.println();
        System.out.println("Nome do cliente: (primeiro nome e sobrenome começando com maiúsculo!)");
        ReadService.readNextLine();
        String name = ReadService.readNextLine();

        if (!validName(name)) {
            throw new ServiceException("Nome com formato inválido!");
        }

        System.out.println();
        System.out.println("Tipo do cliente: ");
        PrinterService.printEnums(EnumProviderService.getOptions(ClientType.class));
        System.out.print("Sua escolha: ");
        int opcClientType = ReadService.readInt();

        ClientType clientType = generateClientType(EnumProviderService.getOptions(ClientType.class), opcClientType);
        Document document = generateDocument(clientType);

        //Retorna dados comparativos do cliente (equals e hash code)
        return new Client(name, document);
    }

    //Retorna tipo de cliente pelo id escolhido, se inválido lança exception
    private ClientType generateClientType(List<ClientType> clientTypes, int opcClientType) {
        return clientTypes.stream()
                .filter(type -> type.getId() == opcClientType || type.getId() - 1 == opcClientType)
                .findFirst()
                .orElseThrow(() -> new ServiceException("Opção inválida!"));
    }

    //Retorna documento do cliente
    private Document generateDocument(ClientType clientType) {

        List<DocumentType> documentTypes = EnumProviderService.getOptions(DocumentType.class);

        //Gera tipo do documento pelo ID do ClientType (Cliente PJ e CNPJ tem mesmo id)
        DocumentType documentType = documentTypes
                .stream()
                .filter(type -> type.getId() == clientType.getId())
                .findFirst()
                .orElseThrow(() -> new ServiceException("Tipo de cliente inválido!"));

        System.out.println();
        System.out.println(documentType + " do cliente: (com pontuação correta!)");
        ReadService.readNextLine();
        String content = ReadService.readNextLine();

        //Caso não passe no regex lança exception
        if (!validDocument(content, documentType)) throw new ServiceException("Documento inválido!");

        return new Document(documentType, content);
    }

    public Client updateClient(Client clientRead) {

        //Removendo campos indesejados para alteração
        List<ExcelField> excelFields = EnumProviderService.getOptions(ExcelField.class);
        excelFields.remove(ExcelField.CRIADO_EM);
        excelFields.remove(ExcelField.ALTERADO_EM);
        excelFields.remove(ExcelField.TIPO_DOCUMENTO);

        List<ConfirmOption> confirmOptions = EnumProviderService.getOptions(ConfirmOption.class);

        //Cria cliente apontando para novo endereço com dados do buscado
        Client clientUpdate = new Client(clientRead.getName(),
                clientRead.getAge(),
                clientRead.getClientType(),
                clientRead.getDocument(),
                clientRead.getCreationDate(),
                clientRead.getLastUpdateDate());

        //Enquanto desejar alterar
        ConfirmOption option = ConfirmOption.SIM;
        while (option.equals(ConfirmOption.SIM)) {

            System.out.println();
            System.out.println("Opções para alteração:");
            PrinterService.printEnums(excelFields);
            System.out.print("Sua escolha: ");

            int opcAtributte = ReadService.readInt();
            if (!validOption(opcAtributte, excelFields.size())) throw new ServiceException("Opção inválida!");

            //Atualiza atributo pela enum e recebe opção de continuar
            updateAtributte(EnumProviderService.getEnum(excelFields, opcAtributte), clientUpdate);
            clientUpdate.setLastUpdateDate(LocalDateTime.now());

            System.out.println();
            System.out.println("Deseja atualizar mais algum atributo? ");
            PrinterService.printEnums(confirmOptions);
            System.out.print("Sua escolha: ");

            //Recebe opção SIM ou NÃO em int e converte
            option = EnumProviderService.getEnum(ConfirmOption.class, ReadService.readInt());
        }

        return clientUpdate;
    }


    //Atualiza atributo de cliente por Switch
    private void updateAtributte(ExcelField atributte, Client clientUpdate) {

        switch (atributte) {
            case NOME -> {

                System.out.println();
                System.out.println("Digite o novo nome do cliente " + clientUpdate.getName() + ": (primeiro nome e sobrenome começando com maiúsculo!)");
                ReadService.readNextLine();
                String name = ReadService.readNextLine();

                if (!validName(name) || name.equals(clientUpdate.getName())) {
                    throw new ServiceException("Nome com formato inválido ou igual ao nome anterior!");
                }

                clientUpdate.setName(name);
            }
            case IDADE -> {

                System.out.println();
                System.out.println("Digite a nova idade do cliente " + clientUpdate.getName() + ": (somente números!)");
                int age = ReadService.readInt();

                if (!validAge(age) || age == clientUpdate.getAge()) {
                    throw new ServiceException("Idade inválida!");
                }

                clientUpdate.setAge(age);
            }
            case TIPO -> {

                //Retira tipo atual e disponibiliza apenas os outros
                List<ClientType> clientTypes = EnumProviderService.getOptions(ClientType.class);
                clientTypes.remove(clientUpdate.getClientType());

                System.out.println();
                PrinterService.printEnums(clientTypes);
                System.out.print("Sua escolha: ");

                int opcClientType = ReadService.readInt();

                //-1 no método permite que a lógica funcione com usuário escolhendo enum de id 0
                ClientType clientType = generateClientType(clientTypes, opcClientType);
                Document document = generateDocument(clientType);

                if (!validDocument(document.getContent(), document.getDocumentType())) {
                    throw new ServiceException("Documento inválido!");
                }

                clientUpdate.setClientType(clientType);
                clientUpdate.setDocument(document);

            }

            case DOCUMENTO -> {

                Document document = generateDocument(clientUpdate.getClientType());

                if (!validDocument(document.getContent(), document.getDocumentType())
                        || document.equals(clientUpdate.getDocument())) {
                    throw new ServiceException("Documento inválido!");
                }

                clientUpdate.setDocument(document);
            }
        }
    }

}