package services;

import interfaces.Identifiable;

import java.util.List;

public class PrinterService {

    //Exibe elemento de lista de enumerações + seu ID para escolha
    public static <T extends Identifiable> void printEnums(List<T> enums) {
        for (T enumm : enums) {

            //Substitui underscore para exibição
            String s = (enumm.toString().contains("_"))
                    ? enumm.toString().replace("_", " ")
                    : enumm.toString();

            //Exibe index da enumeração + 1 para escolha
            System.out.println(enumm.getId() + " - " + s);
        }
    }
}
