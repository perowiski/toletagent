package pojos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Price implements Serializable {
	@Getter
    @Setter
	public Double price;

	@Getter
    @Setter
	public Double discount;

	@Getter
    @Setter
	public String currency;

	@Getter
    @Setter
	public String duration;

	@Getter
    @Setter
	public String priceDesc;

	@Getter
    @Setter
	public String perPrice;

	@Override
	public String toString() {
		//DecimalFormat df = new DecimalFormat("#,###.00");
		DecimalFormat df = new DecimalFormat("#,###");
		String per = "";
		if(StringUtils.hasText(this.perPrice)) per = " / " + this.perPrice;
		if("DOLLAR".equals(currency)) {
			return "$ " + df.format(this.price) + per;
		}
		return "N " + df.format(this.price) + per;
	}
}