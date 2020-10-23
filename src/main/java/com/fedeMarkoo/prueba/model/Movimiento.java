package com.fedeMarkoo.prueba.model;

import java.util.Date;

public class Movimiento extends Registro {

	public Movimiento(String descripcion, String origen, Date fecha, String comprobante, Double monto,
			Double dolar) {
		super(descripcion, origen, fecha, comprobante, monto, dolar);
		boolean isCuota = descripcion.matches(".*\\d{2}/\\d{2}.*");
		boolean isCuotaFinal = descripcion.matches(".*(\\d{2})/\\1.*");
		this.tipo = isCuota ? isCuotaFinal ? "Cuota Final" : "Cuota" : "Compra";
	}

	private String tipo;

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "Movimiento [tipo=" + tipo + ", " + super.toString() + "]";
	}

}
