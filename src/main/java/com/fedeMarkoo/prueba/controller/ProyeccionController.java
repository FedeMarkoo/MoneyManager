package com.fedeMarkoo.prueba.controller;

import com.fedeMarkoo.prueba.model.Cuota;
import com.fedeMarkoo.prueba.model.Periodo;
import com.fedeMarkoo.prueba.model.PeriodoHistorico;
import com.fedeMarkoo.prueba.model.ProyeccionHistorico;

import java.util.Arrays;
import java.util.List;

public class ProyeccionController {

    public static void addAnteriores(ProyeccionHistorico historicos) {
        PeriodoHistorico perTemp = new PeriodoHistorico();
        perTemp.setDescript("Restos del mes anterior");
        perTemp.setType(0);

        Double[] amounts = new Double[9];
        Double ant = 0.0;
        int index = 0;
        for (Double amount :
                historicos.getAmounts()) {
            amounts[index++] = ant;
            ant += amount;
        }

        perTemp.setAmount(amounts);
        historicos.add(perTemp);
    }

    public static void addResto(ProyeccionHistorico historicos) {
        PeriodoHistorico perTemp = new PeriodoHistorico();
        perTemp.setDescript("Restos del mes en curso");
        perTemp.setType(2);

        Double[] amounts = new Double[9];
        int index = 0;
        for (Double amount :
                historicos.getAmounts()) {
            amounts[index++] = amount;
        }

        perTemp.setAmount(amounts);
        historicos.add(perTemp);
    }

    public static void addCompras(List<Periodo> periodo, ProyeccionHistorico historicos) {
        PeriodoHistorico perTemp;
        int index;
        Double[] amounts;

        perTemp = new PeriodoHistorico();
        perTemp.setDescript("Gastos en compra");
        perTemp.setType(1);
        index = 4;
        amounts = new Double[9];
        for (Periodo p : periodo) {
            amounts[index--] =
                    p.getMovimientos().stream().filter(mov -> mov.getTipo().equals("Compra")).mapToDouble(ob -> (ob.getMonto()))
                            .reduce(0, (a, b) -> a + b);
            if (index < 0) {
                break;
            }
        }

        int c = 0;
        double monto = 0;
        for (Double d :
                Arrays.asList(amounts).subList(0,4)) {
            if (d != null) {
                c++;
                monto += d;
            }
        }
        monto /= c;
        for (index = 5; index < 9; index++) {
            amounts[index] = monto;
        }
        perTemp.setAmount(amounts);
        historicos.add(perTemp);
    }

    public static void addCuotas(List<Periodo> periodo, ProyeccionHistorico historicos) {
        PeriodoHistorico perTemp;
        int index;
        Double[] amounts;

        perTemp = new PeriodoHistorico();
        perTemp.setDescript("Gastos en cuotas");
        perTemp.setType(1);
        index = 4;
        amounts = new Double[9];
        for (Periodo p : periodo) {
            amounts[index--] =
                    p.getMovimientos().stream().filter(mov -> mov.getTipo().equals("Cuota")).mapToDouble(ob -> (ob.getMonto()))
                            .reduce(0, (a, b) -> a + b)
                            + p.getMovimientos().stream().filter(mov -> mov.getTipo().equals("Cuota Final")).mapToDouble(ob -> (ob.getMonto()))
                            .reduce(0, (a, b) -> a + b);
            if (index < 0)
                break;
        }

        Periodo p = periodo.get(0);
        List<Cuota> cuotas = p.getCuotas();
        for (int i = 5; i < 9; i++) {
            int finalI = i;
            amounts[i] = cuotas.stream().filter(a -> a.getResto() > finalI - 4).mapToDouble(a -> a.getMonto() / a.getResto()).reduce(0, (a, b) -> a + b);
        }

        perTemp.setAmount(amounts);
        historicos.add(perTemp);
    }

    public static void addSueldo(List<Periodo> periodo, ProyeccionHistorico historicos) {
        PeriodoHistorico perTemp = new PeriodoHistorico();
        perTemp.setDescript("Sueldo");
        perTemp.setType(0);
        int index = 4;
        Double[] amounts = new Double[9];
        for (Periodo p : periodo) {
            amounts[index--] = Double.valueOf(p.getSueldo());
            if (index < 0)
                break;
        }

        Periodo p = periodo.get(0);
        for (int i = 5; i < 9; i++) {
            int finalI = i;
            amounts[i] = 77000.0;
        }

        perTemp.setAmount(amounts);
        historicos.add(perTemp);
    }

}
