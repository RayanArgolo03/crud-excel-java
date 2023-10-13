package services;
import java.util.*;
public class EnumProviderService {

    //Retorna lista mutável de todas as enumerações da classe
    public static <T extends Enum<T>> List<T> getOptions(Class<T> enumClass){
        return new ArrayList<>(Arrays.asList(enumClass.getEnumConstants()));
    }

    //Pega enum por opção escolhida
    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, int id){
        return getOptions(enumClass).get(id - 1);
    }

    //Pega enum na lista que sofreu mutações (remoções)
     public static <T extends Enum<T>> T getEnum(List<T> enums, int id){
        return enums.get(id - 1);
    }

    //Pega enum por string value
    public static <T extends Enum<T>> T getEnumEquals(Class<T> enumClass, String content) {

        for (T enumm : getOptions(enumClass)) {
            String enumContent = enumm.toString();

            //Remove underscore para comparar com atributo de cliente do DB
            if (enumContent.contains("_")){
                enumContent = enumContent.replace("_", " ");
            }

            if (enumContent.hashCode() == content.hashCode()) return enumm;
        }

        //Caso retorne nulo será lançada exception ao criar cliente (programação defensiva)
        return null;
    }

}
