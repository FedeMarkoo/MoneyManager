package com.fedeMarkoo.prueba.controller;

import com.fedeMarkoo.prueba.model.Cuota;
import com.fedeMarkoo.prueba.model.Periodo;
import com.fedeMarkoo.prueba.model.PeriodoHistorico;
import com.fedeMarkoo.prueba.model.ProyeccionHistorico;
import com.fedeMarkoo.prueba.model.Registro;
import com.fedeMarkoo.prueba.service.MongoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@Component
public class ProyeccionController {

	private static MongoDAO dao;

	public static void addGastosTotal(List<Periodo> periodo, ProyeccionHistorico historicos, Integer defase) {
		defase += getMonthsDifference(periodo.get(0).getPeriodo());
		PeriodoHistorico perTemp;
		int index;
		Double[] amounts;

		perTemp = new PeriodoHistorico();
		perTemp.setDecrypt("Gastos Total de Tarjetas");
		perTemp.setType(2);
		amounts = new Double[9];

		double promedioDeCompras = 0;
		index = 4 - defase;
		for (Periodo p : periodo) {
			if (index < 0)
				break;
			if (index < 9)
				amounts[index] = p.getMovimientos().stream().mapToDouble(Registro::getMonto)
						.reduce(0, Double::sum);
			index--;
		}
		int cant = 0;
		for (Periodo p : periodo) {
			double compraAmount = p.getMovimientos().stream().filter(mov -> mov.getTipo().equals("Compra")).mapToDouble(Registro::getMonto)
					.reduce(0, Double::sum);
			cant++;
			promedioDeCompras += compraAmount;
		}
		promedioDeCompras /= cant;
		Periodo p = periodo.get(0);
		List<Cuota> cuotas = p.getCuotas();
		for (int i = Math.max(5 - defase, 0); i < 9; i++) {
			int finalI = i;
			Integer finalDefase = defase;
			Stream<Cuota> filtered = cuotas.stream().filter(a -> a.getResto() > finalI - 5 + finalDefase);
			DoubleStream map = filtered.mapToDouble(a -> a.getMonto() / a.getResto());
			amounts[i] = promedioDeCompras + map.reduce(0, Double::sum);
		}

		perTemp.setAmount(amounts);
		historicos.add(perTemp);
	}

	public static void addAnteriores(ProyeccionHistorico historicos, Integer defase) {
		PeriodoHistorico perTemp = new PeriodoHistorico();
		perTemp.setDecrypt("Restos del mes anterior");
		perTemp.setType(0);

		Double[] amounts = new Double[9];
		Double ant = 0.0;
		int index = 0;
		for (Double amount :
				historicos.getAmounts()) {
			amounts[index++] = ant == 0 ? null : ant;
			ant += amount;
		}

		perTemp.setAmount(amounts);
		historicos.add(perTemp);
	}

	public static void addResto(ProyeccionHistorico historicos, Integer defase) {
		PeriodoHistorico perTemp = new PeriodoHistorico();
		perTemp.setDecrypt("Restos del mes en curso");
		perTemp.setType(2);

		Double[] amounts = new Double[9];
		int index = 0;
		for (Double amount :
				historicos.getAmounts()) {
			amounts[index++] = amount == 0 ? null : amount;
		}

		perTemp.setAmount(amounts);
		historicos.add(perTemp);
	}

	public static void addCompras(List<Periodo> periodo, ProyeccionHistorico historicos, Integer defase) {
		defase += getMonthsDifference(periodo.get(0).getPeriodo());
		PeriodoHistorico perTemp;
		int index;
		Double[] amounts;

		perTemp = new PeriodoHistorico();
		perTemp.setDecrypt("Gastos en compra");
		perTemp.setType(1);
		index = 4 - defase;
		amounts = new Double[9];

		double monto = 0;
		int cant = 0;
		for (Periodo p : periodo) {
			double compraAmount = p.getMovimientos().stream().filter(mov -> mov.getTipo().equals("Compra")).mapToDouble(Registro::getMonto)
					.reduce(0, Double::sum);
			cant++;
			monto += compraAmount;
			if (index < 0) {
				historicos.getAmounts()[0] -= compraAmount;
			} else if (index < 9) {
				amounts[index] = compraAmount;
			}
			index--;
		}
		monto /= cant;
		for (index = 5 - defase; index < 9; index++) {
			if (index < 0) {
				historicos.getAmounts()[0] -= monto;
			} else if (index < 9)
				amounts[index] = monto;
		}
		perTemp.setAmount(amounts);
		historicos.add(perTemp);
	}

	public static double getPromedioDeCompras(Double[] amounts) {
		int c = 0;
		double monto = 0;
		for (Double d :
				Arrays.asList(amounts).subList(0, 4)) {
			if (d != null) {
				c++;
				monto += d;
			}
		}
		monto /= c;
		return monto;
	}

	public static void addCuotas(List<Periodo> periodo, ProyeccionHistorico historicos, Integer defase) {
		defase += getMonthsDifference(periodo.get(0).getPeriodo());
		PeriodoHistorico perTemp;
		int index;
		Double[] amounts;

		perTemp = new PeriodoHistorico();
		perTemp.setDecrypt("Gastos en cuotas");
		perTemp.setType(1);
		index = 4 - defase;
		amounts = new Double[9];
		for (Periodo p : periodo) {
			double cuotaAmount = p.getMovimientos().stream().filter(mov -> !mov.getTipo().equals("Compra")).mapToDouble(Registro::getMonto)
					.reduce(0, Double::sum);
			if (index < 0) {
				historicos.getAmounts()[0] -= cuotaAmount;
			} else if (index < 9)
				amounts[index] = cuotaAmount == 0 ? null : cuotaAmount;
			index--;
		}


		Periodo p = periodo.get(0);
		List<Cuota> cuotas = p.getCuotas();
		for (int i = 5 - defase; i < 9; i++) {
			int finalI = i;
			Integer finalDefase = defase;
			double cuotaAmount = cuotas.stream().filter(a -> a.getResto() > finalI - 5 + finalDefase).mapToDouble(a -> a.getMonto() / a.getResto()).reduce(0,
					Double::sum);
			if (i < 0)
				historicos.getAmounts()[0] -= cuotaAmount;
			else
				amounts[i] = cuotaAmount == 0 ? null : cuotaAmount;
		}

		perTemp.setAmount(amounts);
		historicos.add(perTemp);
	}

	public static void addCuotasLiquidar(List<Periodo> periodo, ProyeccionHistorico historicos, Integer defase) {
		defase += getMonthsDifference(periodo.get(0).getPeriodo());
		PeriodoHistorico perTemp;
		int index;
		Double[] amounts;

		perTemp = new PeriodoHistorico();
		perTemp.setDecrypt("Liquidacion Cuotas");
		perTemp.setType(2);
		index = 4 - defase;
		amounts = new Double[9];


		Periodo p = periodo.get(0);
		List<Cuota> cuotas = p.getCuotas();
		for (int i = 5 - defase; i < 9; i++) {
			if (i >= 0) {
				int finalI = i;
				int temp = finalI - 4 + defase;
				Stream<Cuota> filtrado = cuotas.stream().filter(a -> a.getResto() > temp);
				DoubleStream doubleStream = filtrado.mapToDouble(a -> a.getMonto() / a.getResto() * (a.getResto() - temp));
				double amount = doubleStream.reduce(0, Double::sum);
				amounts[i] = amount == 0 ? null : amount;
			}
		}
		perTemp.setAmount(amounts);
		historicos.add(perTemp);
	}

	public static void addSueldo(List<Periodo> periodo, ProyeccionHistorico historicos, Integer defase) {
		defase += getMonthsDifference(periodo.get(0).getPeriodo());
		int mesesHastaRevision = 0;
		int cantRevisiones = 0;
		Periodo ptemp = periodo.get(0);
		Double ultimoSueldo = ptemp.getSueldo();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
		try {
			Date date = dateFormat.parse(ptemp.getPeriodo());
			YearMonth m1 = YearMonth.from(date.toInstant().atZone(ZoneOffset.UTC));
			mesesHastaRevision = m1.getMonth().getValue();

		} catch (Exception e) {
			e.printStackTrace();
		}

		PeriodoHistorico perTemp = new PeriodoHistorico();
		perTemp.setDecrypt("Sueldo");
		perTemp.setType(0);
		int index = 4 - defase;
		Double[] amounts = new Double[9];
		for (Periodo p : periodo) {
			Double sueldo = p.getSueldo();
			if (index < 0) {
				historicos.getAmounts()[0] += sueldo;
			} else if (index < 9) {
				amounts[index] = sueldo;
			}
			index--;
		}

		for (int i = 5 - defase; i < 0; i++) {
			if (mesesHastaRevision == 3) cantRevisiones++;
			else if (mesesHastaRevision == 9) {
				cantRevisiones++;
			} else if (mesesHastaRevision == 12)
				mesesHastaRevision = 0;

			historicos.getAmounts()[0] += ultimoSueldo * (Math.pow(1.15, cantRevisiones));
			mesesHastaRevision++;
		}

		Periodo p = periodo.get(0);
		for (int i = Math.max(5 - defase, 0); i < 9; i++) {
			if (mesesHastaRevision == 3) cantRevisiones++;
			else if (mesesHastaRevision == 9) {
				cantRevisiones++;
			} else if (mesesHastaRevision == 12)
				mesesHastaRevision = 0;

			amounts[i] = ultimoSueldo * (Math.pow(1.15, cantRevisiones));
			mesesHastaRevision++;
		}

		perTemp.setAmount(amounts);
		historicos.add(perTemp);
	}

	public static void addAguinaldo(List<Periodo> periodo, ProyeccionHistorico historicos, Integer defase) {
		int mesesHastaRevision = 0;
		int cantRevisiones = 0;
		Periodo ptemp = periodo.get(0);
		Double ultimoSueldo = ptemp.getSueldo();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
		try {
			Date date = dateFormat.parse(ptemp.getPeriodo());
			YearMonth m1 = YearMonth.from(date.toInstant().atZone(ZoneOffset.UTC));
			mesesHastaRevision = m1.getMonth().getValue();

		} catch (Exception e) {
			e.printStackTrace();
		}
		PeriodoHistorico perTemp = new PeriodoHistorico();
		perTemp.setDecrypt("Sueldo");
		perTemp.setType(0);
		int index = 4 - defase;
		Double[] amounts = new Double[9];
		for (Periodo p : periodo) {
			Double sueldo = p.getSueldo();
			if (index < 0) {
				historicos.getAmounts()[0] += sueldo;
			} else if (index < 9) {
				amounts[index] = sueldo;
			}
			index--;
		}

		for (int i = 5 - defase; i < 0; i++) {
			if (mesesHastaRevision == 4) cantRevisiones++;
			else if (mesesHastaRevision == 12) {
				mesesHastaRevision = 0;
				cantRevisiones++;
			}
			historicos.getAmounts()[0] += ultimoSueldo * (Math.pow(1.15, cantRevisiones));
			mesesHastaRevision++;
		}

		for (int i = Math.max(5 - defase, 0); i < 9; i++) {
			if (mesesHastaRevision == 4) cantRevisiones++;
			else if (mesesHastaRevision == 12) {
				mesesHastaRevision = 0;
				cantRevisiones++;
			}
			amounts[i] = ultimoSueldo * (Math.pow(1.15, cantRevisiones));
			mesesHastaRevision++;
		}

		perTemp.setAmount(amounts);
		historicos.add(perTemp);
	}

	public static int getMonthsDifference(String periodo) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
			YearMonth m1 = YearMonth.from(dateFormat.parse(periodo).toInstant().atZone(ZoneOffset.UTC));

			YearMonth m2 = YearMonth.from(Instant.now().atZone(ZoneOffset.UTC));

			return (int) (m1.until(m2, ChronoUnit.MONTHS) - m1.until(m2, ChronoUnit.YEARS));
		} catch (ParseException e) {
			return 0;
		}
	}

	public static int getMonthsDifference(String periodo1, String periodo2) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
			YearMonth m1 = YearMonth.from(dateFormat.parse(periodo1).toInstant().atZone(ZoneOffset.UTC));

			YearMonth m2 = YearMonth.from(dateFormat.parse(periodo2).toInstant().atZone(ZoneOffset.UTC));

			return (int) (m1.until(m2, ChronoUnit.MONTHS) - m1.until(m2, ChronoUnit.YEARS));
		} catch (ParseException e) {
			return 0;
		}
	}

	public static void addPeriodosHistoricos(ProyeccionHistorico historicos, Integer defase) {
		List<PeriodoHistorico> periodoHistoricos = dao.getPeriodosHistoricos();
		periodoHistoricos.forEach(perTemp -> {
			Integer editableType = perTemp.getEditableType();
			editableType = editableType == null ? 1 : editableType;
			perTemp.setEditableType(editableType);
			int dif = getMonthsDifference(perTemp.getPeriodo()) + defase;
			Double[] temp = perTemp.getAmount();
			Double[] amounts = new Double[9];
			for (int i = Math.min(4 - dif, 0); i < temp.length + 4 - dif; i++) {
				if (i >= 4 - dif && i >= 0 && i - 4 + dif < temp.length && i < 9)
					amounts[i] = temp[i - 4 + dif];
				else if (i < 0) {
					if (temp[i - 4 + dif] != null)
						switch (perTemp.getType()) {
							case 0:
								historicos.getAmounts()[0] += temp[i - 4 + dif];
								break;
							case 1:
								historicos.getAmounts()[0] -= temp[i - 4 + dif];
								break;
						}
				}
			}
			perTemp.setAmount(amounts);
		});
		periodoHistoricos.forEach(historicos::add);
	}

	@Autowired
	public void setDao(MongoDAO dao) {
		ProyeccionController.dao = dao;
	}

}
