package models;

import formatters.ModelFormat;

import javax.persistence.*;

@Entity
@Table(name="axises")
public class EAxis extends EModel implements Comparable<EAxis>{

    @ModelFormat(EState.class)
    @ManyToOne
    @JoinColumn
    public EState state;

    @Override
    public String toString() {
        return this.name;
    }
	
	@Override
	public int compareTo(EAxis o) {
		return this.name.compareToIgnoreCase(o.name);
	}
}