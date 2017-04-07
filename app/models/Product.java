package models;

import lombok.Getter;
import lombok.Setter;
import models_enums.ProductType;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by onuche on 3/3/17.
 */
@Entity
@Table(name = "products")
public class Product extends Model {

    @Getter @Setter
    public Integer maximum;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    public ProductType productType;

    @Getter @Setter
    public Double unitPrice;

    @Getter @Setter
    @ManyToOne
    public Plan plan;
}
