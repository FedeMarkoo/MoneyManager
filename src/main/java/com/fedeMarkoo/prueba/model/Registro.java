package com.fedeMarkoo.prueba.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public abstract class Registro<T> implements Comparable<Registro> {

	private String descripcion;
	private String origen;
	private Date fecha;
	private String comprobante;
	private Double monto;
	private Double dolar;

	public Registro(String descripcion, String origen, Date fecha, String comprobante, Double monto, Double dolar) {
		super();
		this.descripcion = descripcion;
		this.origen = origen;
		this.fecha = fecha;
		this.comprobante = comprobante;
		this.monto = monto;
		this.dolar = dolar;
	}

	public Registro() {
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getComprobante() {
		return comprobante;
	}

	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public Double getDolar() {
		return dolar;
	}

	public void setDolar(Double dolar) {
		this.dolar = dolar;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof String) {
			return this.comprobante.equals(o);
		}
		if (o instanceof Registro) {
			return this.getComprobante().equals(((Registro) o).getComprobante());
		}
		return super.equals(o);
	}

	@Override
	public int compareTo(Registro o) {
		return this.comprobante.compareTo(o.comprobante);
	}

	@Override
	public String toString() {
		return "Registro [descripcion=" + descripcion + ", origen=" + origen + ", fecha=" + fecha + ", comprobante="
				+ comprobante + ", monto=" + monto + ", dolar=" + dolar + "]";
	}

}
