package enums;

import interfaces.Identifiable;

public enum CrudOption implements Identifiable {

    CREATE(1), READ(2), UPDATE(3), DELETE(4), SAIR(5);

    private final int id;

    CrudOption(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

}
