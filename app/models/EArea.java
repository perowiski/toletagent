package models;

import formatters.ModelFormat;

import javax.persistence.*;

@Entity
@Table(name="areas")
public class EArea extends EModel implements Comparable<EArea>{

    @ModelFormat(EAxis.class)
    @ManyToOne
    @JoinColumn
    public EAxis axis;

    @Override
    public String toString() {
        return this.name;
    }

    @Override
	public int compareTo(EArea o) {
		return this.name.compareToIgnoreCase(o.name);
	}
}