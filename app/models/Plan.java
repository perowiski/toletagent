package models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import models_enums.ProductType;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by toletdeveloper2 on 3/3/17.
 */
@Data
@Entity
@Table(name = "plans")
public class Plan extends Model{
    
    public String name;
    public Double price;
    public Integer duration;
    @OneToMany(mappedBy = "plan")
    public List<Product> products = new ArrayList<>();
}
