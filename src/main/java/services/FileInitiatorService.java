package services;

import controllers.*;
import domain.client.Client;
import domain.log.Log;
import enums.LogType;

import java.io.*;
import java.util.Map;

public class FileInitiatorService {
    public static void startFiles(ClientController clientController, FolderController folderController, LogController logController, ExcelController excelController) throws IOException {

        //Pega pasta de retorno dos arquivos passada pelo usuário
        folderController.setFile(folderController.getFolderService().getPathFolder());

        excelController.setFile(folderController.getFile().getPath());
        logController.setFile(folderController.getFile().getPath());

        //Caso já exista pasta: gera log de pasta antiga e verifica se quer DB antigo
        if (folderController.isOldFile()) {
            excelController.getExcelService().getDataBase(excelController.getFile(), excelController.isOldFile());

            //Escolhe excel -> usa log antigo ou cria novo conforme a escolha
            if (excelController.getExcelService().isOldDatabase()) {

                //Caso arquivo log não tenha sido apagado = log antigo
                if (logController.isOldFile()) {
                    logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.PEGOU, " LOG do programa anterior"));
                    logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.PEGOU, " EXCEL database do programa anterior"));
                }

                //Arquivo log foi apagado / corrompido
                else {
                    System.out.println("Trazendo dados de clientes do DB para arquivo logs...");

                    logController.getLogService().createFile(folderController.getFile().getPath());
                    logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.CRIOU, " novo arquivo LOG"));

                    Map<Integer, Client> oldClientes = excelController.getExcelService().getClientsMap(excelController.getFile());

                    //Caso encontre clientes no DB antigo
                    if (!oldClientes.isEmpty()) {
                        logController.getLogService()
                                .insertRegister(logController.getFile(),
                                        new Log(LogType.PEGOU, " clientes do EXCEL database antigo"), oldClientes.values());
                    }

                }
            }

            //Não é um database antigo, não tem como utilizar log antigo
            else {
                System.out.println("Criando novo arquivo logs..");

                logController.getLogService().createFile(logController.getFile().getPath());
                logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.CRIOU, " novo EXCEL database"));
                logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.CRIOU, " novo arquivo LOG"));
            }
        }

        //É uma nova pasta, então, criará todos os arquivos novamente
        else {

            System.out.println("Criando novos arquivos...");

            //Cria pasta -> Cria arquivo de Log e formata -> Insere novos logs
            folderController.getFolderService().createFile(folderController.getFile().getPath());

            logController.getLogService().createFile(logController.getFile().getPath());
            logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.CRIOU, " nova pasta"));
            logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.CRIOU, " novo arquivo LOG"));

            excelController.getExcelService().createFile(excelController.getFile().getPath());
            logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.CRIOU, " novo EXCEL database"));

        }


        System.out.println("Foram geradas modificações no sistema, confira arquivo logs na pasta "
                + folderController.getFile().getName() + "!");
    }
}
