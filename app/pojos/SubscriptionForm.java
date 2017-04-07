package pojos;

import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

/**
 * Created by tolet on 3/10/17.
 */
public class SubscriptionForm {

    @Getter @Setter
    @Constraints.Required
    private Long planId;
    @Getter @Setter
    @Constraints.Required
    private String paymentType;
    @Getter @Setter
    private String tellerNumber;
    @Getter @Setter
    private String amountPaid;
    @Getter @Setter
    private String paymentDate;
}
