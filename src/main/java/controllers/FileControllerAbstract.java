
package controllers;

import java.io.File;

//Classe abstrata que disponibiliza métodos padrão para arquivos
public abstract class FileControllerAbstract {

    //Inicializa todos os atributos arquivos como nulo
    private File file;
    public FileControllerAbstract() {
        this.file = null;
    }
    public File getFile() {
        return file;
    }

    //Seta caminho do arquivo na folderPath
    public void setFile(String path) {
        this.file = new File(path);
    }

    //Método que define se o data base é antigo
    public boolean isOldFile (){
        return file.exists();
    }
}
