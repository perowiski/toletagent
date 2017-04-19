package models;

import converters.PaymentDetailConverter;
import converters.SubscriptionProductConverter;
import lombok.Getter;
import lombok.Setter;
import models_enums.PaymentType;
import pojos.PaymentDetail;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 3/17/17.
 */

@Table(name = "SubscriptionPaymentTopUps")
@Entity
public class SubscriptionPaymentTopUp extends Model implements Comparable<SubscriptionPaymentTopUp>{

    @Getter @Setter
    @ManyToOne
    public Subscription subscription;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    public PaymentType paymentType;

    @Getter @Setter
    @Column(length = 10000000)
    @Convert(converter= PaymentDetailConverter.class)
    public PaymentDetail paymentDetail;

    @Getter @Setter
    @Column(nullable = false)
    public Boolean isPending;

    @Getter @Setter
    @Column(length = 10000000)
    @Convert(converter= SubscriptionProductConverter.class)
    public List<Subscription.Product> products = new ArrayList<>();

    @Getter @Setter
    public Date lastDateModified = new Date();

    @Override
    public int compareTo(SubscriptionPaymentTopUp o) {
        return this.created.compareTo(o.created);
    }
}
