package models_enums;

/**
 * Created by seyi on 12/16/16.
 */
public enum AgentConnectType {
    CLIR("Lead"),
    BRIR("Brief"),
    PROP("Property Brief");

    String rep;

    AgentConnectType(String rep) {
        this.rep = rep;
    }

    public String getRep() {
        return rep;
    }
}
