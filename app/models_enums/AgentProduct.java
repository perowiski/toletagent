package models_enums;

/**
 * Created by toletdeveloper2 on 2/8/17.
 */
public enum AgentProduct {

    SRM("Send/Receive Brief and Property Request"),

    CW("Custom Website"),

    SI("My Inspections"),

    BP("My Adverts"),

    BCR("My Messages"),

    PR("Property Request");

    String rep;

    AgentProduct(String rep) {
        this.rep = rep;
    }

    public String getRep() {
        return rep;
    }
}
