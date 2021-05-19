package com.fedeMarkoo.prueba.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Balance {
    private String name;
    private BigDecimal amount;
    private BigDecimal amountARS;
    private BigDecimal amountBTC;
    private BigDecimal amountUSD;
    private BigDecimal valueARS;
    private BigDecimal valueBTC;
    private BigDecimal valueUSD;
    private BigDecimal gananciaBTC;
    private BigDecimal gananciaARS;
    private String gananciaBTCText;
}
