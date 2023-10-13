package enums;

import interfaces.Identifiable;

public enum ConfirmOption implements Identifiable {
    SIM (1), NAO (2);
    private final int id;

    ConfirmOption(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
