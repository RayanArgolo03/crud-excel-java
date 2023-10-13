package app;

import controllers.*;
import services.*;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {

        //Instanciando controladores e composições
        ClientController clientController = new ClientController(new ClientService());
        ExcelController excelController = new ExcelController(new ExcelService());
        LogController logController = new LogController(new LogService());
        FolderController folderController = new FolderController(new FolderService());

        //Lógica de inicialização de arquivos
        try {
            System.out.println("Mantenha seu arquivo logs.txt fechado para receber registros..");
            FileInitiatorService.startFiles(clientController, folderController, logController, excelController);
        } catch (Exception e) {
            System.out.println();
            System.out.println("Ocorreu um erro ao iniciar o programa!");
            System.out.println(e.getMessage());

            //Breka o programa caso exception
            return;
        }

        //Lógica do CRUD - While no RunProgram
        CrudService.runProgram(excelController, logController, clientController, folderController);

    }
}




