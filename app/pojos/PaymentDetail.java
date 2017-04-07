package pojos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by peter on 3/17/17.
 */
public abstract class PaymentDetail implements Serializable{

    @Getter @Setter
    public Calendar paymentDate;

    @Getter @Setter
    public double amountPaid;
}
