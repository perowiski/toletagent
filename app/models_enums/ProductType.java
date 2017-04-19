package models_enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Onuche Idoko on 3/3/17.
 */
public enum ProductType {
    PROPERTY_LISTING("Property Listing"),
    CUSTOM_WEBSITE("Custom Website"),
    CLIENT_DETAIL("Client Detail"),
    SCHEDULE_INSPECTION_REMINDER("Schedule Inspection and Reminder"),
    OTHER_CLIENT("Other Agent Client"),
    READVERTISE("Re-advertise per month"),
    PROPERTY_BLAST("Property Blast"),
    CLIENT_REQUEST_BLAST("Client Request Blast"),
    CLIENT_CONTACT("Direct Access to client contact"),
    TRACK_CLIENT("Keep Track of Clients from all Sources"),
    PROPERTY_PUSHUP("Property Push-up per Month"),
    RECOMMENDATION("Property Recommendation"),
    AGENT_FORUM("Access to Agent Forum"),
    PROMOTE_AD("Promote ad"),
    SEND_BROCHURE("Send Brochures contacts per Month"),
    FREE_LEADS("Access to Free Leads Through Direct Client Request");

    public String rep;

    ProductType(String rep) {
        this.rep = rep;
    }

    public String getRep() {
        return rep;
    }

    public static Map<String, ProductType> productTypes(){
        Map<String, ProductType> productTypes = new HashMap<>();
        for(ProductType productType : ProductType.values()){
            productTypes.put(productType.rep, productType);
        }
        return productTypes;
    }

}
