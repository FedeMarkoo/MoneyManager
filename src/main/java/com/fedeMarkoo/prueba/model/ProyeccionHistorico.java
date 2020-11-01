package com.fedeMarkoo.prueba.model;

import java.util.ArrayList;

public class ProyeccionHistorico {
    private ArrayList<PeriodoHistorico> historicos;
    private Double[] amounts;

    public ProyeccionHistorico() {
        this.setHistoricos(new ArrayList<PeriodoHistorico>());
    }

    public ArrayList<PeriodoHistorico> getHistoricos() {
        if (historicos == null)
            this.setHistoricos(new ArrayList<PeriodoHistorico>());
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
