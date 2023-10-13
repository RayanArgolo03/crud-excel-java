package enums;

import interfaces.Identifiable;

public enum LogType implements Identifiable {
    ADICIONOU (1), CRIOU (2), PEGOU(3), ALTEROU (4), DELETOU (5);
    private final int id;
    LogType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
