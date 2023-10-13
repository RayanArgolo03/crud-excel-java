package services;

import controllers.ClientController;
import controllers.ExcelController;
import controllers.FolderController;
import controllers.LogController;
import domain.client.Client;
import domain.log.Log;
import enums.CrudOption;
import enums.LogType;

import java.util.Map;

public class CrudService {
    public static void runProgram(ExcelController excelController, LogController logController, ClientController clientController, FolderController folderController) {

        //Labed loop para brekar o while
        choose:
        while (true) {

            try {

                //Printa opções e converte em enum a opção escolhida como int
                PrinterService.printEnums(EnumProviderService.getOptions(CrudOption.class));
                System.out.print("Sua escolha: ");

                CrudOption option = EnumProviderService.getEnum(CrudOption.class, ReadService.readInt());
                System.out.println();

                switch (option) {

                    case CREATE:
                        System.out.println("Mantenha seu arquivo logs.txt fechado para receber registros..");

                        //Cria cliente e gera log no arquivo logs
                        Client clientCreate = clientController.getClientService().createClient();
                        logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.CRIOU, " novo cliente no programa"), clientCreate);

                        //Adiciona cliente no DB e gera log de adição
                        boolean sucess = excelController.add(clientCreate);
                        if (sucess) {
                            logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.ADICIONOU, " " + clientCreate.getName() + " EXCEL database"));
                            System.out.println();
                            System.out.println("Feito, confira arquivo logs na pasta " + folderController.getFile().getName() + "!");
                        }

                        break;
                    case READ:

                        System.out.println("Mantenha seu arquivo logs.txt fechado para receber registros..");
                        System.out.println("Verificando se existem clientes no database..");
                        excelController.checkInvalidLine();

                        //Busca dados do cliente localmente
                        Client clientData = clientController.getClientService().getClientData();

                        // Retorna mapa com cliente e linha no DB (se existir)
                        Map<Integer, Client> clientAndLine = excelController.getExcelService().getClientAndLine(excelController.getFile(), clientData);

                        //Extrai cliente do mapa e gera log de cliente pego no DB
                        Client clientRead = excelController.getExcelService().getClient(clientAndLine);

                        logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.PEGOU, " cliente do EXCEL database"));

                        //Gera JSON do cliente na pasta de retorno
                        sucess = JsonService.getResponse(folderController.getFile().getPath(), clientRead);
                        if (sucess) {
                            logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.CRIOU, " nova consulta JSON"));
                            System.out.println();
                            System.out.println("Feito, confira arquivo logs e JSON na pasta " + folderController.getFile().getName() + "!");
                            System.out.println("Caso arquivo não apareça na pasta no programa, recarregue-a!");
                        } else {
                            //Caso ocorra erro no arquivo, sem exception
                            System.out.println("Ocorreu algum erro na serialização do cliente em JSON!");
                        }

                        break;
                    case UPDATE:

                        System.out.println("Mantenha seu arquivo logs.txt fechado para receber registros..");
                        System.out.println("Verificando se existem clientes no database..");
                        excelController.checkInvalidLine();

                        //Pega dados localmente passando linhas do DB
                        clientData = clientController.getClientService().getClientData();
                        clientAndLine = excelController.getExcelService().getClientAndLine(excelController.getFile(), clientData);

                        //Gera log de cliente buscado, caso não estoure exception ao pegar cliente no DB
                        logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.PEGOU, " cliente do EXCEL database"));

                        //Extrai cliente do mapa e atualiza-o
                        clientRead = excelController.getExcelService().getClient(clientAndLine);
                        Client clientUpdate = clientController.getClientService().updateClient(clientRead);

                        logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.ALTEROU, " dados de cliente"));

                        //Extrai linha do cliente no DB
                        int row = excelController.getExcelService().getRowClient(clientAndLine);

                        sucess = excelController.update(clientRead, clientUpdate, row);
                        if (sucess) {
                            logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.ALTEROU, " cliente no EXCEL database"), clientUpdate);
                            System.out.println();
                            System.out.println("Feito, confira arquivo logs na pasta " + folderController.getFile().getName() + "!");
                        }

                        break;
                    case DELETE:

                        System.out.println("Mantenha seu arquivo logs.txt fechado para receber registros..");
                        System.out.println("Verificando se existem clientes no database..");
                        excelController.checkInvalidLine();

                        clientData = clientController.getClientService().getClientData();
                        clientAndLine = excelController.getExcelService().getClientAndLine(excelController.getFile(), clientData);

                        logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.PEGOU, " cliente do EXCEL database"));

                        //Extrai cliente e linha do mapa
                        clientRead = excelController.getExcelService().getClient(clientAndLine);
                        row = excelController.getExcelService().getRowClient(clientAndLine);

                        sucess = excelController.delete(clientRead, row);
                        if (sucess) {
                            logController.getLogService().insertRegister(logController.getFile(), new Log(LogType.DELETOU, "  cliente do EXCEL database"));
                            System.out.println();
                            System.out.println("Feito, confira arquivo logs na pasta " + folderController.getFile().getName() + "!");
                        }

                        break;
                    case SAIR:

                        System.out.println("Thanks for the use :)");
                        System.out.println("Linkedin: https://www.linkedin.com/in/rayanargolo/ ✦ Rayan Argolo");
                        break choose;

                }

            }

            //Caso tenha conteúdo impróprio em célula no momento de busca
            catch (IndexOutOfBoundsException e) {
                System.out.println();
                System.out.println("Opção inválida!");
            }

            //Caso opção inválida
            catch (IllegalStateException e) {
                System.out.println();
                System.out.println("Há conteúdo inválido em célula no DB!");
            }

            //Célula ou linha nula
            catch (NullPointerException e) {
                System.out.println();
                System.out.println("Erro: Há célula e/ou linha nula no DB!");
            }

            //Exceptions lançadas pelos métodos
            catch (Exception e) {
                System.out.println();
                System.out.println("Erro: " + e.getMessage());
            }

            //Quebra linha para reiniciar programa
            System.out.println();
        }
    }

}
