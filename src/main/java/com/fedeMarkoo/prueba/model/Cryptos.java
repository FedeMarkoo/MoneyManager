package com.fedeMarkoo.prueba.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Cryptos {
    private String name;
    private BigDecimal amount;
    private BigDecimal amountBTC;
    private BigDecimal amountARS;
    private BigDecimal valueARS;
    private BigDecimal valueBTC;
    private BigDecimal gananciaBTC;
    private BigDecimal gananciaARS;

}
