package com.fedeMarkoo.prueba.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PeriodosHistoricos")
public class PeriodoHistorico {
    private String periodo;
    @Id
    private String decrypt;
    private Double[] amount;
    private int type;
    private Integer editableType;

    public PeriodoHistorico() {
    }

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

    public String getDecrypt() {
        return decrypt;
    }

    public void setDecrypt(String decrypt) {
        this.decrypt = decrypt;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Integer getEditableType() {
        return editableType;
    }

    public void setEditableType(Integer editableType) {
        this.editableType = editableType;
    }
}