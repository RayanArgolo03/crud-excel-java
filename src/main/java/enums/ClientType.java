
package enums;

import interfaces.Identifiable;
public enum ClientType  implements Identifiable {
    PESSOA_JURIDICA(1), PESSOA_FISICA (2);
    private final int id;
    ClientType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
