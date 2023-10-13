package controllers;

import domain.client.Client;
import exceptions.ControllerException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import services.ExcelService;

import java.io.*;

public class ExcelController extends FileControllerAbstract {
    private ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        super();
        this.excelService = excelService;
    }

    public ExcelService getExcelService() {
        return excelService;
    }

    @Override
    public void setFile(String path) {
        super.setFile(path + "\\dbClient.xlsx");
    }

    //Verifica se há alguma linha nula e/ou faltando campos no DB
    public void checkInvalidLine() throws IOException {

        try (FileInputStream inputStream = new FileInputStream(super.getFile())) {

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);

            //Caso não tenham linhas suficientes
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            if (numberOfRows < 2) throw new ControllerException("Não há clientes no DB!");

            int cell = 0;
            for (int i = 1; i <= numberOfRows; i++) {

                Row row = sheet.getRow(i);
                if (row != null) {

                    //Verifica se linha falta atributos
                    if (row.getPhysicalNumberOfCells() != 7) {
                        throw new ControllerException("Linha " + (i + 1) + " faltando atributos!");
                    }

                    //Verifica se tem alguma célula vazia na linha
                    for (Cell c : row) {
                        if (c.getCellType() != CellType.NUMERIC) {
                            if (c.getStringCellValue().isBlank()) {
                                throw new ControllerException("Linha " + (i + 1) +
                                        " com célula nulo e/ou célula com espaço no DB!");
                            }
                        }
                    }
                }
            }

            workbook.close();
        }
    }

    //Valida adição de cliente
    public boolean add(Client client) throws IOException {
        boolean newClient = getExcelService().isNewClient(super.getFile(), client);
        if (!newClient) throw new ControllerException("Cliente já existente no DB!");
        getExcelService().insertRegister(getFile(), client);

        // Caso não lance exception acima, adição concluída
        return true;
    }

    //Valida update
    public boolean update(Client clientRead, Client clientUpdate, int row) throws IOException {
        boolean isValidUpdate = getExcelService().isValidUpdate(super.getFile(), clientRead, clientUpdate);
        if (!isValidUpdate) throw new ControllerException("Cliente já existente no DB!");
        getExcelService().insertRegister(super.getFile(), clientUpdate, row);
        return true;
    }

    //Valida exclusão
    public boolean delete(Client clientRead, int row) throws IOException {
        getExcelService().removeRegister(super.getFile(), clientRead, row);
        return true;
    }
}
