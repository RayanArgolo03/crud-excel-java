package enums;

import interfaces.Identifiable;

public enum DocumentType implements Identifiable {
    CNPJ(1), CPF (2);
    private final int id;
    DocumentType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {return id; }

}
