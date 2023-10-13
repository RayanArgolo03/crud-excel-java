package enums;

import interfaces.Identifiable;

public enum ExcelField implements Identifiable {

    NOME(1), IDADE(2), TIPO(3), TIPO_DOCUMENTO(4), DOCUMENTO(5), CRIADO_EM(6), ALTERADO_EM(7);
    private final int id;
    ExcelField(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
