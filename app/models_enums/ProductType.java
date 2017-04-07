package models_enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by toletdeveloper2 on 3/3/17.
 */
public enum ProductType {
    PROPERTY_LISTING("PROPERTY LISTING"),
    CUSTOM_WEBSITE("CUSTOM WEBSITE"),
    CLIENT_DETAIL("CLIENT DETAIL"),
    SCHEDULE_INSPECTION_REMINDER("SCHEDULE INSPECTION REMINDER"),
    RE_ADVERTISE("RE ADVERTISE"),
    PROPERTY_BLAST("PROPERTY BLAST"),
    CLIENT_REQUEST_BLAST("CLIENT REQUEST BLAST"),
    FREE_LEADS("FREE LEADS");

    public String name;

    ProductType(String name){
        this.name = name;
    }

    public static Map<String, ProductType> productTypes(){
        Map<String, ProductType> productTypes = new HashMap<>();
        for(ProductType productType : ProductType.values()){
            productTypes.put(productType.name, productType);
        }
        return productTypes;
    }
}
