package models;

import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import formatters.AmtFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/*import models_enums.Currency;
import models_enums.PriceDuration;*/
import play.data.validation.Constraints;

@Embeddable
public class Price {

    @Constraints.Required
    @Getter @Setter
    @AmtFormat
    @Column(name="PRICE", nullable=false)
    public Double price;

    @Getter @Setter
    @Column(name="DISCOUNT")
    public Double discount;

    /*@Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name="PRICE_CURRENCY")
    public Currency currency = Currency.NAIRA;*/

    /*@Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name="PRICE_DURATION")
    public PriceDuration duration;*/

    @Getter @Setter
    @Column(name="PRICE_DESC")
    public String priceDesc;

    @Getter @Setter
    @Column(name="PER_PRICE")
    public String perPrice;

    /*@Override
    public String toString() {
        //DecimalFormat df = new DecimalFormat("#,###.00");
        DecimalFormat df = new DecimalFormat("#,###");
        String per = "";
        if(StringUtils.hasText(this.perPrice)) per = " / " + this.perPrice;
        if(currency==Currency.DOLLAR) {
            return "$ " + df.format(this.price) + per;
        }
        return "N " + df.format(this.price) + per;
    }*/

    public String toPrice() {
        //DecimalFormat df = new DecimalFormat("#,###.00");
        DecimalFormat df = new DecimalFormat("#,###");
        String per = "";
        if(StringUtils.hasText(this.perPrice)) per = " / " + this.perPrice;
        return df.format(this.price) + per;
    }

    /*public String toDouble() {
        DecimalFormat df = new DecimalFormat("#,###");
        if(currency==Currency.DOLLAR) {
            return "$ " + df.format(this.price);
        }
        return "N " + df.format(this.price);
    }*/

    public String discount() {
        String dis = "";
        if(this.discount != null) {
            dis = "( " + this.discount.longValue() + "% off )";
        }
        return dis;
    }
}