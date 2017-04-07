package converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Subscription;
import pojos.PaymentDetail;
import pojos.TellerPaymentDetail;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by peter on 3/17/17.
 */
@Converter
public class PaymentDetailConverter implements AttributeConverter<PaymentDetail, String>{

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(PaymentDetail paymentDetails) {
        try {
            if(paymentDetails == null) return "";
            return objectMapper.writeValueAsString(paymentDetails);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Override
    public PaymentDetail convertToEntityAttribute(String s) {
        try {
            if(s == null) return new PaymentDetail(){};
            PaymentDetail paymentDetails = objectMapper.readValue(s, TellerPaymentDetail.class);
            return paymentDetails;

        } catch (Exception ex) {
            return null;
        }
    }
}
