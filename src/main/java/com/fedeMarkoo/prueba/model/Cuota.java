package com.fedeMarkoo.prueba.model;

import java.util.Date;

public class Cuota extends Registro<Cuota> {

	private Integer total;
	private Integer resto;

	public Cuota() {
		super();
	}

	public Cuota(String descripcion, String origen, Date fecha, String comprobante, Double monto, Double dolar,
				 Integer total, Integer resto) {
		super(descripcion, origen, fecha, comprobante, monto, dolar);
		this.total = total;
		this.resto = resto;
	}

	public Cuota(String descripcion, String origen, Date fecha, String comprobante, String monto, String dolar,
			String total, String resto) {
		super(descripcion, origen, fecha, comprobante, new Double(monto.replace(".", "").replace(",", ".")),
				new Double(dolar.replace(".", "").replace(",", ".")));
		this.total = new Integer(total);
		this.resto = new Integer(resto);
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getResto() {
		return resto;
	}

	public void setResto(Integer resto) {
		this.resto = resto;
	}

	@Override
	public String toString() {
		return "Cuota [total=" + total + ", resto=" + resto + ", " + super.toString() + "]";
	}

}
