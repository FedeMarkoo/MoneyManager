package com.fedeMarkoo.prueba.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ProyeccionHistorico {
	private ArrayList<PeriodoHistorico> historicos;
	private Double[] amounts;

	public ProyeccionHistorico() {
		this.setHistoricos(new ArrayList<>());
	}

	public ArrayList<PeriodoHistorico> getHistoricos() {
		if (historicos == null)
			this.setHistoricos(new ArrayList<>());
		return historicos;
	}

	public void setHistoricos(ArrayList<PeriodoHistorico> historicos) {
		this.historicos = historicos;
	}

	public Double[] getAmounts() {
		if (amounts == null)
			this.setAmounts(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
		return amounts;
	}

	public void setAmounts(Double[] amounts) {
		this.amounts = amounts;
	}

	public void add(PeriodoHistorico perTemp) {
		if (Arrays.stream(perTemp.getAmount()).noneMatch(Objects::nonNull))
			return;
		Double totalAmount = Arrays.stream(perTemp.getAmount()).filter(Objects::nonNull).reduce(Double::sum).orElse(0.0);
		String decrypt = String.format("%s ($%,.2f)", perTemp.getDecrypt(), totalAmount);
		perTemp.setDecrypt(decrypt);
		this.getHistoricos().add(perTemp);
		int index = 0;
		for (Double amount :
				perTemp.getAmount()) {
			if (amount != null)
				switch (perTemp.getType()) {
					case 0:
						this.getAmounts()[index++] += amount;
						break;
					case 1:
						this.getAmounts()[index++] -= amount;
						break;
				}
			else
				index++;
		}
	}

}
