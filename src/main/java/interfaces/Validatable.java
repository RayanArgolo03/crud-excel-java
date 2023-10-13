
package interfaces;

import enums.*;

public interface Validatable {
    default boolean validOption(int opc, int total) {
        return opc > 0 && opc <= total;
    }

    //Utilizado pela classe ExcelService - Validação de inserções, atualizações do DB
    default boolean validOption(int opc) {
        return opc == ConfirmOption.SIM.getId() || opc == ConfirmOption.NAO.getId();
    }

    //Utilizado pela classe FolderController - Validação de caminho, nome de pasta
    default boolean validPath(String path) {
        return path.matches("([a-zA-Z]:)?(\\\\[a-zA-Z0-9-]+)+\\\\?");
    }

    default boolean validFolderName(String folderName) {
        return folderName.matches(
                "^[^\\\\/?*&quot;'gl:|]*$") && !folderName.isEmpty();
    }

    //Utilizados pela classe ClientService e Client - Criação e validação de cliente
    default boolean validObject(Object o) {
        return o != null;
    }

    default boolean validName(String name) {
        return name.matches(
                "^[A-ZÀ-Ÿ][A-zÀ-ÿ']+\\s([A-zÀ-ÿ'])*[A-ZÀ-Ÿ][A-zÀ-ÿ']+$");
    }

    default boolean validAge(int age) {
        return age >= 18 && age <= 123;
    }

    default boolean validDocument(String content, DocumentType documentType) {
        //TRUE regex CPF, FALSE regex CNPJ
        String regex = (documentType.equals(DocumentType.CPF))
                ? "(^\\d{3}\\x2E\\d{3}\\x2E\\d{3}\\x2D\\d{2}$)"
                : "(^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$)";

        return content.matches(regex);
    }
}
