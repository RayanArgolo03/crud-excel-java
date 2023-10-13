package services;

import domain.client.*;
import enums.*;
import exceptions.ServiceException;
import interfaces.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.*;
import java.util.*;

public class ExcelService implements FileGerenciable, Validatable {

    private boolean choseOld;

    public ExcelService() {

        //Inicia escolha de antigo como falso, pois ainda não houve escolha
        this.choseOld = false;
    }

    public boolean isOldDatabase() {
        return choseOld;
    }

    public void getDataBase(File excelDataBase, boolean isOldDatabase) throws IOException {
        if (isOldDatabase) {
            databaseChoose(excelDataBase);
        } else {
            createFile(excelDataBase.getPath());
        }
    }

    public void databaseChoose(File excelDataBase) throws IOException {

        System.out.println("Já existe um DB na pasta, deseja utiliza-lo?");
        PrinterService.printEnums(EnumProviderService.getOptions(ConfirmOption.class));
        System.out.print("Sua escolha: ");

        int opc = readChoose(ReadService.readInt());
        ConfirmOption option = EnumProviderService.getEnum(ConfirmOption.class, opc);

        switch (option) {
            case SIM -> choseOld = true;
            case NAO -> {
                String path = excelDataBase.getPath();
                boolean sucess = excelDataBase.delete();
                if (!sucess) throw new ServiceException("Não foi possível apagar o DB atual!");
                createFile(path);
            }
        }
    }

    //Lê opção de DB a ser utilizado
    private int readChoose(int opc) {

        while (!validOption(opc)) {
            System.out.println();
            System.out.println("Opção Inválida!");
            System.out.println("Já existe um DB na pasta returns, deseja utiliza-lo?");
            PrinterService.printEnums(EnumProviderService.getOptions(ConfirmOption.class));
            opc = ReadService.readInt();
        }

        return opc;
    }

    @Override
    public void createFile(String path) throws IOException {
        File excelDataBase = new File(path);
        boolean sucess = excelDataBase.createNewFile();
        if (!sucess) throw new ServiceException("Não foi possível criar novo arquivo logs.txt!");
        formatFile(excelDataBase);
    }

    //Formata DB criado, colocando campos de atributos
    @Override
    public void formatFile(File excelDataBase) throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(excelDataBase)) {
            Workbook workbook = new XSSFWorkbook();

            //Cria linha 0 para escrever campos
            Sheet sheet = workbook.createSheet("Clientes");
            Row row = sheet.createRow(0);

            //Estila das células
            CellStyle backgroundColor = workbook.createCellStyle();
            backgroundColor.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            backgroundColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            //Countador de células e ID inicial
            int cells = 0;
            int id = 1;

            //Pega tamanho da lista de atributos (cliente) para escrita no arquivo excel
            int countAtributtes = EnumProviderService.getOptions(ExcelField.class).size();
            while (countAtributtes > 0) {

                //Pega to string da enum
                String defaultField = EnumProviderService.getEnum(ExcelField.class, id).toString();

                //Substítui o underscore para escrita no DB
                if (defaultField.contains("_")) {
                    defaultField = defaultField.replace("_", " ");
                }

                //Escreve conteúdo, reorganiza tamanho e pinta background da célula
                Cell cell = row.createCell(cells);
                cell.setCellValue(defaultField);
                cell.setCellStyle(backgroundColor);
                sheet.autoSizeColumn(cells);

                id++;
                cells++;
                countAtributtes--;
            }

            workbook.write(outputStream);
            outputStream.flush();
            workbook.close();
        }
    }

    //Busca clientes no DB antigo, retornando mapa com linhas e cliente associados
    public Map<Integer, Client> getClientsMap(File excelDataBase) throws IOException {

        Map<Integer, Client> clientMap = new HashMap<>();

        try (FileInputStream inputStream = new FileInputStream(excelDataBase)) {

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);

            //Se só tiver somente uma linha (campos dos atributos) retorna vazio
            int rows = sheet.getPhysicalNumberOfRows();
            if (rows == 1) return new HashMap<>();

            //Percorre todas as linhas do DB que podem ter clientes
            int cell = 0;
            for (int i = 1; i < rows; i++) {

                Row row = sheet.getRow(i);

                String name = row.getCell(cell).getStringCellValue().trim();
                cell++;

                int age = (int) row.getCell(cell).getNumericCellValue();
                cell++;

                ClientType clientType = EnumProviderService.getEnumEquals(ClientType.class, row.getCell(cell).getStringCellValue().trim());
                cell++;

                DocumentType documentType = EnumProviderService.getEnumEquals(DocumentType.class, row.getCell(cell).getStringCellValue().trim());
                cell++;

                String content = row.getCell(cell).getStringCellValue();
                cell++;

                LocalDateTime creationDate = convertDate(row.getCell(cell).getStringCellValue().trim());
                cell++;

                LocalDateTime lastUpdateDate = convertDate(row.getCell(cell).getStringCellValue().trim());

                Client client = new Client(name, age, clientType, new Document(documentType, content), lastUpdateDate, creationDate);

                //Se tiver cliente repetido em outra linha do DB
                if (clientMap.containsValue(client)) {
                    throw new ServiceException("Clientes com mesmo documento no DB, corrija!");
                }

                clientMap.put(row.getRowNum(), client);
                cell = 0;

            }

            workbook.close();
            return clientMap;
        }
    }

    //Converte data local date time recebida do DB
    private LocalDateTime convertDate(String dateString) {
        String[] date = dateString.split("[!/@#$%^&*()_+{}\\[\\]:;<>,.?~\\s]");
        int day = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[2]);
        int hour = Integer.parseInt(date[3]);
        int minute = Integer.parseInt(date[4]);
        return LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.of(hour, minute));
    }

    //Busca se tem alguma chave (linha) associada ao cliente com mesmo nome e documento
    public Map<Integer, Client> getClientAndLine(File excelDataBase, Client clientData) throws IOException {
        Map<Integer, Client> clientsMap = getClientsMap(excelDataBase);

        int key = clientsMap.keySet().stream()
                .filter(row -> clientsMap.get(row).getName().equals(clientData.getName())
                        && clientsMap.get(row).getDocument().equals(clientData.getDocument()))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Cliente não encontrado na consulta ao DB!"));

        return Map.of(key, clientsMap.get(key));
    }

    //Extrai cliente do mapa
    public Client getClient(Map<Integer, Client> clientMap) {
        return clientMap.values().stream()
                .findFirst()
                .orElseThrow(() -> new ServiceException("Ocorreu um erro ao pegar o cliente!"));
    }

    //Extrai linha do cliente no DB do mapa
    public Integer getRowClient(Map<Integer, Client> clientMap) {
        return clientMap.keySet().stream()
                .findFirst()
                .orElseThrow(() -> new ServiceException("Ocorreu um erro ao pegar a linha do cliente!"));
    }

    //Verifica se o cliente criado já existe no DB
    public boolean isNewClient(File excelDataBase, Client clientCreate) throws IOException {
        return !getClientsMap(excelDataBase).containsValue(clientCreate);
    }

    //Verifica se o update é válido
    public boolean isValidUpdate(File excelDataBase, Client clientRead, Client clientUpdate) throws IOException {

        //Remove cliente antes de ser atualizado localmente
        Collection<Client> clientsWithoutUpdate = getClientsMap(excelDataBase).values();
        clientsWithoutUpdate.remove(clientRead);

        //True se não há outro cliente com dados do atualizado no DB
        return !clientsWithoutUpdate.contains(clientUpdate);
    }


    //Insere novo cliente no database, modificando implementação da interface
    //Exceção pode ser lançada, caso seja, é capturada na classe App
    @Override
    public <T> void insertRegister(File excelDataBase, T logOrClient) throws IOException {

        //Converte T em client para acessar atributos
        Client clientCreate = (Client) logOrClient;

        try (FileInputStream inputStream = new FileInputStream(excelDataBase)) {

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);

            //Procura linha para escrever cliente, partindo da linha após os campos default
            int line = 1;
            Row row = null;
            while (row == null) {

                Row aux = sheet.getRow(line);

                //Se encontrar linha nula (zerada) cria linha para cliente
                if (aux == null) {
                    row = sheet.createRow(line);
                }

                //Se encontrar linha faltando atributos sobrescreve
                else if (aux.getPhysicalNumberOfCells() != 7) {
                    sheet.removeRow(aux);
                    row = sheet.createRow(line);
                    System.out.println("Cliente sendo sobrescrito na linha "
                            + (line + 1) + " pois no anterior faltavam atributos!!");
                }

                line++;
            }


            //Escreve os atributos do cliente nos campos
            int countCell = 0;
            for (Object atributte : clientCreate.getAtributtesValues()) {
                Cell cell = row.createCell(countCell);
                writeWithType(cell, atributte);
                sheet.autoSizeColumn(cell.getColumnIndex());

                countCell++;
            }

            workbook.write(new FileOutputStream(excelDataBase));
            workbook.close();
        }
    }

    //Atualiza cliente em linha no DB, modificando método da interface
    @Override
    public <T, V> void insertRegister(File excelDataBase, T clientUpdate, V row) throws IOException {

        try (FileInputStream inputStream = new FileInputStream(excelDataBase)) {

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);

            //Pega linha do cliente no DB, convertendo o genérico
            Row clientRow = sheet.getRow((int) row);

            //Casting para cliente
            int countCell = 0;
            Client client = (Client) clientUpdate;

            for (Object atributte : client.getAtributtesValues()) {
                Cell cell = clientRow.getCell(countCell);
                writeWithType(cell, atributte);
                sheet.autoSizeColumn(cell.getColumnIndex());

                countCell++;
            }

            workbook.write(new FileOutputStream(excelDataBase));
            workbook.close();
        }
    }

    //Verifica se é string (para retirar underscores) ou age integer
    private void writeWithType(Cell cell, Object attributte) {
        if (attributte instanceof Integer num) {
            cell.setCellValue(num);
        } else {
            String atributteString = (attributte.toString().contains("_"))
                    ? attributte.toString().replace("_", " ")
                    : attributte.toString();

            cell.setCellValue(atributteString);
        }
    }

    @Override
    public void removeRegister(File excelDataBase, Client clientRead, int row) throws IOException {

        try (FileInputStream inputStream = new FileInputStream(excelDataBase)) {

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);

            //Remove a linha da planilha
            sheet.removeRow(sheet.getRow(row));

            //Redefine o comprimento da coluna
            for (int i = 0; i <= 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(new FileOutputStream(excelDataBase));
            workbook.close();
        }
    }


}
