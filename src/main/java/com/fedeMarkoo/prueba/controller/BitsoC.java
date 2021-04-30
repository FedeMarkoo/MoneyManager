package com.fedeMarkoo.prueba.controller;

import com.bitso.Bitso;
import com.bitso.BitsoBalance;
import com.bitso.BitsoOperation;
import com.bitso.BitsoTicker;
import com.bitso.exceptions.BitsoAPIException;
import com.fedeMarkoo.prueba.model.Balance;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bitso")
public class BitsoC {

    public static final BigDecimal ZERO = BigDecimal.ZERO;
    private final Bitso bitso = new Bitso("FWbgYSgovf", "e059508ac3460a0c95f600d67431c8e6", 5);
    private final Map<String, BigDecimal> valor = new HashMap<>();

    public static void addTotal(final List<Balance> balanceList) {
        final Balance total = new Balance();
        total.setName("Total");
        total.setAmountBTC(BigDecimal.ZERO);
        total.setAmountARS(BigDecimal.ZERO);
        total.setGananciaARS(BigDecimal.ZERO);
        total.setGananciaBTC(BigDecimal.ZERO);
        balanceList.stream().forEach(cryptos -> {
            total.setAmountBTC(total.getAmountBTC().add(cryptos.getAmountBTC()));
            total.setAmountARS(total.getAmountARS().add(cryptos.getAmountARS()));
            if (cryptos.getGananciaARS() != null) {
                total.setGananciaARS(total.getGananciaARS().add(cryptos.getGananciaARS()));
            }
            if (cryptos.getGananciaBTC() != null) {
                total.setGananciaBTC(total.getGananciaBTC().add(cryptos.getGananciaBTC()));
            }
        });
        balanceList.add(total);
    }

    @SneakyThrows
    @GetMapping
    public Object get() {
        final Map<String, Balance> balanceList = this.getBalances();

        this.getValores();

        this.setGanancias(balanceList);

        this.fillSomething(balanceList);

        final List<Balance> list = balanceList.values().stream().filter(balance -> balance.getAmount().doubleValue() > 0).collect(Collectors.toList());

        BitsoC.addTotal(list);

        return list;
    }

    public void fillSomething(final Map<String, Balance> balanceList) {
        balanceList.forEach((s, balance) -> {
            final BigDecimal btc = this.getValor(balance.getName(), "btc");
            final BigDecimal ars = this.getValor(balance.getName(), "ars");

            balance.setValueARS(ars);
            balance.setValueBTC(btc);

            balance.setAmountBTC(balance.getAmount().multiply(btc));
            balance.setAmountARS(balance.getAmount().multiply(ars));
        });
    }

    public void setGanancias(final Map<String, Balance> balanceList) throws BitsoAPIException {
        balanceList.values().forEach(balance -> balance.setGananciaBTC(balance.getAmount().multiply(this.getValor(balance.getName(), "btc"))));
        balanceList.values().forEach(balance -> balance.setGananciaARS(balance.getAmount().multiply(this.getValor(balance.getName(), "ars"))));

        BitsoOperation[] ledger = this.bitso.getLedger(null, "limit=25", "sort=asc");
        String last = "";
        do {
            last = "marker=" + ledger[ledger.length - 1].getEntryId();
            for (final BitsoOperation bitsoOperation : ledger) {
                final BitsoOperation.BalanceUpdate[] after = bitsoOperation.getAfterOperationBalances();
                final Balance balance = balanceList.get(after[0].getCurrency());
                if (after.length == 2) {
                    if (after[1].getCurrency().equals("btc")) {
                        balance.setGananciaBTC(balance.getGananciaBTC().add(after[1].getAmount()));
                    } else if (after[1].getCurrency().equals("ars")) {
                        balance.setGananciaARS(balance.getGananciaARS().add(after[1].getAmount()));
                    }
                }
            }
            ledger = this.bitso.getLedger(null, "limit=25", "sort=asc", last);
        } while (ledger.length > 0);

        balanceList.forEach((s, balance) -> {
            switch (s) {
                case "ars":
                case "btc":
                case "dai":
                    balance.setGananciaBTC(BitsoC.ZERO);
                case "eth":
                    balance.setGananciaARS(BitsoC.ZERO);
                    break;
                default:
                    balance.setGananciaARS(balance.getGananciaBTC().multiply(this.getValor("btc", "ars")));
            }
        });
    }

    private BigDecimal getValor(final String c1, final String c2) {
        BigDecimal value = this.valor.getOrDefault(c1 + "_" + c2, this.valor.get(c2 + "_" + c1));
        if (value == null) {
            value = this.tryConvert(c1, c2);
        }
        return value;
    }

    private BigDecimal tryConvert(final String c1, final String c2) {
        if (c1.equals(c2)) {
            final BigDecimal v = BigDecimal.ONE;
            this.valor.put(c1 + "_" + c2, v);
            return v;
        }
        if (c2 == "ars") {
            final BigDecimal v = this.getValor(c1, "btc").multiply(this.getValor("btc"));
            this.valor.put(c1 + "_" + c2, v);
            return v;
        }
        return BitsoC.ZERO;
    }

    public BigDecimal getValor(final String c) {
        BigDecimal valor = this.getValor(c, "ars");
        if (valor == BitsoC.ZERO) {
            valor = this.getValor(c, "btc");
        }
        return valor;
    }

    private void getValores() throws BitsoAPIException {
        final BitsoTicker[] ticker = this.bitso.getTicker();
        for (final BitsoTicker bitsoTicker : ticker) {
            this.valor.put(bitsoTicker.getBook(), bitsoTicker.getLast());
        }
    }

    public Map<String, Balance> getBalances() throws BitsoAPIException {
        final BitsoBalance accountBalance = this.bitso.getAccountBalance();
        final List<BitsoBalance.Balance> balanceList = accountBalance.getBalances().values().stream().collect(Collectors.toList());
        final List<Balance> list = new ArrayList<>();
        balanceList.forEach(balance -> {
            if (balance.getCurrency().matches("usd|ars|dai")) {
                balance.setTotal(balance.getTotal().setScale(2, RoundingMode.FLOOR));
            }
            final Balance b = new Balance();
            b.setName(balance.getCurrency());
            b.setAmount(balance.getTotal());
            list.add(b);
        });
        final Map<String, Balance> collect = list.stream().collect(Collectors.toMap(Balance::getName, Function.identity()));
        return collect;
    }
}
