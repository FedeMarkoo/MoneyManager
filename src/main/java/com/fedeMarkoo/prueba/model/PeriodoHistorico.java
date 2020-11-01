package com.fedeMarkoo.prueba.model;

public class PeriodoHistorico {
    private String descript;
    private Double[] amount;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double[] getAmount() {
        return amount;
    }

    public void setAmount(Double[] amount) {
        this.amount = amount;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }
}
