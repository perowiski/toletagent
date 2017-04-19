package models_enums;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by tolet on 3/15/17.
 */
public enum PaymentType {

    TELLER("Teller"),FREE("Free");

    private String paymentType;

    private PaymentType(String paymentType){
        this.paymentType = paymentType;
    }

    public String toString(){
        return paymentType;
    }

    public static Map<String, String> select() {
        Map<String, String> map = new LinkedHashMap<>();
        for(PaymentType value: PaymentType.values()) {
            map.put(value.toString(), value.paymentType);
        }
        return map;
    }


}
