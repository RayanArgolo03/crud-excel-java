package services;

import exceptions.ServiceException;
import interfaces.FileGerenciable;

import java.io.*;

//Utiliza métodos diretamente da interface que implementa
public class LogService implements FileGerenciable {
    public LogService() {
    }

    //Exclui arquivo logs anterior antes de criar um novo
    @Override
    public void createFile(String path) throws IOException {

        File logFile = new File(path);
        boolean sucess;

        if (logFile.exists()) {
            sucess = logFile.delete();
            if (!sucess) throw new ServiceException("Não foi possível apagar arquivo logs.txt antigo!");
        }

        sucess = logFile.createNewFile();
        if (!sucess) throw new ServiceException("Não foi possível criar novo arquivo logs.txt!");

        formatFile(logFile);
    }

    //Formata arquivo log criado
    @Override
    public void formatFile(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("{ LOG DE AÇÕES DO SISTEMA }");
            writer.newLine();
            writer.flush();
        }
    }

}
