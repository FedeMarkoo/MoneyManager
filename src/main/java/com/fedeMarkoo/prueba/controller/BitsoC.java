package com.fedeMarkoo.prueba.controller;

import com.bitso.Bitso;
import com.bitso.BitsoOperation;
import com.bitso.exceptions.BitsoAPIException;
import com.fedeMarkoo.prueba.model.CryptoMov;
import com.fedeMarkoo.prueba.model.Cryptos;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bitso")
public class BitsoC {

    private final Bitso bitso = new Bitso("key", "secret");
    private final BigDecimal invertido = BigDecimal.ZERO;
    private Map<String, BigDecimal> map = new HashMap<>();
    private List<Cryptos> cryptosList;
    private List<CryptoMov> cryptosMovs;

    @SneakyThrows
    @GetMapping
    public Collection<Cryptos> get() {
        Map<String, BigDecimal> currencies = getCurrencies();
        Map<String, Cryptos> list = new HashMap<>();
        for (Map.Entry<String, BigDecimal> e : currencies.entrySet()) {
            getCryptosAmounts(list, e);
        }
        fillAmount(list);

        calculateGanancias(list);
/*
        double withdrawals = Arrays.stream(bitso.getLedger("withdrawals", "method=SP")).mapToDouble(bo -> bo.getAfterOperationBalances()[0].getAmount().doubleValue()).sum();
        double fundings = Arrays.stream(bitso.getLedger("fundings","method=bind")).mapToDouble(bo -> bo.getAfterOperationBalances()[0].getAmount().doubleValue()).sum();
        double v = withdrawals + fundings;
*/
        Cryptos total = new Cryptos();
        total.setName("Total");
        total.setAmountBTC(BigDecimal.ZERO);
        total.setAmountARS(BigDecimal.ZERO);
        total.setGananciaARS(BigDecimal.ZERO);
        total.setGananciaBTC(BigDecimal.ZERO);
        list.values().stream().forEach(cryptos -> {
            total.setAmountBTC(total.getAmountBTC().add(cryptos.getAmountBTC()));
            total.setAmountARS(total.getAmountARS().add(cryptos.getAmountARS()));
            if (cryptos.getGananciaARS() != null)
                total.setGananciaARS(total.getGananciaARS().add(cryptos.getGananciaARS()));
            if (cryptos.getGananciaBTC() != null)
                total.setGananciaBTC(total.getGananciaBTC().add(cryptos.getGananciaBTC()));
        });
        list.put("tot", total);
        return list.values();
    }

    private void calculateGanancias(Map<String, Cryptos> list) {
        list.values().stream().forEach(cryptos -> {
            cryptos.setGananciaBTC(cryptos.getAmountBTC());
            cryptos.setGananciaARS(cryptos.getAmountARS());
        });
        cryptosMovs.stream().forEach(cryptoMov -> {
            Cryptos c = list.get(cryptoMov.getC1());
            if (cryptoMov.getC1().equals("btc")) {
                c.setGananciaBTC(null);
                c.setGananciaARS(null);
                return;
            }
            if (c != null && cryptoMov.getC2() != null) {
                switch (cryptoMov.getC2()) {
                    case "btc":
                        c.setGananciaBTC(c.getGananciaBTC().add(cryptoMov.getA2()));
                        c.setGananciaARS(c.getGananciaBTC().multiply(list.get("btc").getValueARS()));
                        break;
                    case "ars":
                        c.setGananciaARS(c.getGananciaARS().add(cryptoMov.getA2()));
                        break;
                }
            }
        });
    }

    public void fillAmount(Map<String, Cryptos> list) {
        list.forEach((e, c) -> {
            if (c.getAmountARS() == null && c.getAmountBTC() != null) {
                c.setAmountARS(list.get("btc").getValueARS().multiply(c.getAmountBTC()));
            } else if (c.getAmountBTC() == null) {
                c.setAmountBTC(c.getAmount());
            }

            if (c.getName().equals("btc")) {
                c.setValueBTC(BigDecimal.ONE);
            } else {
                c.setValueARS(c.getValueBTC().multiply(list.get("btc").getValueARS()));
            }
        });
    }

    public void getCryptosAmounts(Map<String, Cryptos> list, Map.Entry<String, BigDecimal> e) throws BitsoAPIException {
        Cryptos c = new Cryptos();
        c.setName(e.getKey());
        c.setAmount(e.getValue());
        Arrays.stream(bitso.getTicker())
                .filter(
                        bookInfo ->
                                bookInfo.getBook().equals("btc_" + c.getName())
                                        || bookInfo.getBook().equals(c.getName() + "_btc")
                ).forEach(t -> {
            c.setValueBTC(t.getLast());
            c.setAmountBTC(c.getAmount().multiply(t.getLast()));
        });
        Arrays.stream(bitso.getTicker())
                .filter(
                        bookInfo ->
                                bookInfo.getBook().equals("ars_" + c.getName())
                                        || bookInfo.getBook().equals(c.getName() + "_ars")
                ).forEach(t -> {
            c.setValueARS(t.getLast());
            c.setAmountARS(c.getAmount().multiply(t.getLast()));
        });


        list.put(c.getName(), c);
    }

    public Map<String, BigDecimal> getCurrencies() throws BitsoAPIException {
        map = new HashMap<>();
        cryptosMovs = new ArrayList<>();
        BitsoOperation[] specificLedger = new BitsoOperation[0];
        specificLedger = bitso.getLedger(null);
        for (BitsoOperation bitsoOperation : specificLedger) {
            BitsoOperation.BalanceUpdate[] afterOperationBalances = bitsoOperation.getAfterOperationBalances();

            CryptoMov e = new CryptoMov();
            e.setC1(afterOperationBalances[0].getCurrency());
            e.setA1(afterOperationBalances[0].getAmount());
            if (afterOperationBalances.length == 2) {
                e.setC2(afterOperationBalances[1].getCurrency());
                e.setA2(afterOperationBalances[1].getAmount());
            }
            cryptosMovs.add(e);

            for (BitsoOperation.BalanceUpdate afterOperationBalance : afterOperationBalances) {
                BigDecimal amount = map.get(afterOperationBalance.getCurrency());
                amount = amount == null ? afterOperationBalance.getAmount() : amount.add(afterOperationBalance.getAmount());
                map.put(afterOperationBalance.getCurrency(), amount);
            }
        }
        return map = map.entrySet().
                stream().
                filter(e -> e.getValue().
                        compareTo(BigDecimal.ZERO) > 0).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
