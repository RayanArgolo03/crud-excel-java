package services;

import exceptions.ControllerException;
import exceptions.ServiceException;
import interfaces.FileGerenciable;
import interfaces.Validatable;

import java.io.File;

public class FolderService implements FileGerenciable, Validatable {
    public FolderService() {
    }

    public String getPathFolder() {

        //Escolhe caminho para pasta dos retornos, seta todos os caminhos de arquivo
        System.out.println("Cole o caminho para criação ou utilização da pasta de retorno: ");
        String path = ReadService.readNextLine();
        if (!validPath(path)) throw new ControllerException("Caminho inválido!");

        System.out.println("Digite somente nome para sua pasta de retorno:");
        String folderName = ReadService.readNextLine();
        if (!validFolderName(folderName)) throw new ControllerException("Nome da pasta inválido!");

        return path + "\\" +folderName;
    }

    //Modifica o método da interface para criar diretório diretamente
    @Override
    public void createFile(String path) {
        boolean sucess = new File(path).mkdir();
        if (!sucess) throw new ServiceException("Erro ao criar pasta de arquivos!");
    }
}
