package models;

import converters.PaymentDetailConverter;
import converters.SubscriptionProductConverter;
import lombok.Getter;
import lombok.Setter;
import models_enums.PaymentType;
import models_enums.ProductType;
import pojos.PaymentDetail;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Peter on 3/3/17.
 */

@Entity
@Table(name = "subscriptions")
public class Subscription extends Model{

    @Getter @Setter
    public String name;

    @Getter @Setter
    public Double price;

    @Getter @Setter
    @Temporal(TemporalType.TIMESTAMP)
    public Date activationDate;

    @Getter @Setter
    @Temporal(TemporalType.TIMESTAMP)
    public Date expiryDate;

    @Getter @Setter
    @ManyToOne
    public Agent agent;

    @Getter @Setter
    @Column(nullable = false)
    public Boolean isActive;

    @Getter @Setter
    public Integer duration;

    @Getter @Setter
    @Column(nullable = false)
    public Boolean isPending;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    public PaymentType paymentType;

    @Getter @Setter
    @Column(length = 10000000)
    @Convert(converter= SubscriptionProductConverter.class)
    public List<Subscription.Product> products = new ArrayList<>();

    @Getter @Setter
    @Column(length = 10000000)
    @Convert(converter= PaymentDetailConverter.class)
    public PaymentDetail paymentDetail;

    public List<ProductType> agentProducts() {
        List<ProductType> productTypes = new ArrayList<>();
        if (products != null){
            for (Subscription.Product p : products) {
                productTypes.add(p.productType);
            }
        }
        return productTypes;
    }

    @Getter @Setter
    public Date lastDateModified = new Date();

    public static class Product extends models.Product implements Serializable{
        @Getter @Setter
        public Integer remainder;
    }
}





