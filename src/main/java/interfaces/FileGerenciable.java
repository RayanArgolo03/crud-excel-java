package interfaces;

import domain.client.Client;
import domain.log.Log;

import java.io.*;
import java.util.Collection;

public interface FileGerenciable {

    //Método para criação de arquivos: txt, excel e diretório
    //É modificado por todos que implementam
    void createFile(String path) throws IOException;

    //Formata arquivo txt ou excel
    //É modificado por todas as classes que implementam
    default void formatFile(File file) throws IOException {}

    //Insere novo log ou cliente no DB
    //Excel service altera a lógica
    default <T> void insertRegister(File file, T logOrClient) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.newLine();
            writer.write(logOrClient.toString());
            writer.newLine();
            writer.flush();
        }
    }


    //Insere log + cliente ou cliente na linha no DB
    //Sobrescrito e alterado pelo ExcelController
    default <T, V> void insertRegister(File file, T type, V value) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.newLine();
            writer.newLine();
            writer.write(type.toString());
            writer.newLine();
            writer.write(value.toString());
            writer.flush();
        }
    }

    //Insere log + coleção de clientes lista de clientes
    default void insertRegister(File file, Log log, Collection<Client> clients) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.newLine();
            writer.newLine();
            writer.write(log.toString());
            writer.newLine();

            //Escrevendo clientes da lista
            for (Client c : clients) {
                writer.write(c.toString());
            }

            writer.flush();
        }
    }

    //Remove cliente do DB - Lógica no excel service
    default void removeRegister(File file, Client clientRead, int line) throws IOException {}

}
