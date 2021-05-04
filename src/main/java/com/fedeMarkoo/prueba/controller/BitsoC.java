package com.fedeMarkoo.prueba.controller;

import com.bitso.Bitso;
import com.bitso.BitsoBalance;
import com.bitso.BitsoOperation;
import com.bitso.BitsoTicker;
import com.bitso.exceptions.BitsoAPIException;
import com.fedeMarkoo.prueba.model.Balance;
import com.fedeMarkoo.prueba.model.BitsoData;
import com.fedeMarkoo.prueba.service.MongoDAO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bitso")
@RequiredArgsConstructor
public class BitsoC {

    public static final BigDecimal ZERO = BigDecimal.ZERO;
    private final Bitso bitso = new Bitso("key", "secret", 5);
    private final Map<String, BigDecimal> valor = new HashMap<>();
    private final MongoDAO dao;
    private BitsoData bitsoData;


    @GetMapping("/historial")
    public Object getH() {
        final List<BitsoData> allBitso = this.dao.getAllBitso();
        final Map<String, List<BigDecimal>> m = new HashMap();
        final AtomicInteger cant = new AtomicInteger();
        cant.set(0);
        allBitso.stream().forEach(bd -> {
            bd.getData().stream().forEach(b -> {
                final List<BigDecimal> list;
                if (!m.containsKey(b.getName())) {
                    list = new ArrayList<>();
                    list.addAll(Arrays.asList(new BigDecimal[cant.get()]));
                    if (cant.get() != 0) {
                        list.set(cant.get() - 1, BigDecimal.ZERO);
                    }
                } else {
                    list = m.get(b.getName());
                    while (list.size() != cant.get()) {
                        list.add(BigDecimal.ZERO);
                    }
                }
                list.add(b.getAmountARS().setScale(2, RoundingMode.FLOOR));
                m.put(b.getName(), list);
            });
            cant.getAndIncrement();
        });

        m.values().stream().forEach(ob -> {
            ob.addAll(Arrays.asList(new BigDecimal[cant.get() - ob.size()]));
            Collections.replaceAll(ob, null, BigDecimal.ZERO);
        });
        return m.values();
    }

    @GetMapping("/historialA")
    public Object getHA() {
        final List<BitsoData> allBitso = this.dao.getAllBitso();
        final Map<String, List<BigDecimal>> m = new HashMap();
        final AtomicInteger cant = new AtomicInteger();
        cant.set(0);
        allBitso.stream().forEach(bd -> {
            bd.getData().stream().forEach(b -> {
                final List<BigDecimal> list;
                if (!m.containsKey(b.getName())) {
                    list = new ArrayList<>();
                    list.addAll(Arrays.asList(new BigDecimal[cant.get()]));
                    if (cant.get() != 0) {
                        list.set(cant.get() - 1, BigDecimal.ZERO);
                    }
                } else {
                    list = m.get(b.getName());
                    while (list.size() != cant.get()) {
                        list.add(BigDecimal.ZERO);
                    }
                }
                list.add(b.getValueARS().setScale(2, RoundingMode.FLOOR));
                m.put(b.getName(), list);
            });
            cant.getAndIncrement();
        });
        m.values().stream().forEach(ob -> {
            ob.addAll(Arrays.asList(new BigDecimal[cant.get() - ob.size()]));
            Collections.replaceAll(ob, null, BigDecimal.ZERO);
        });
        return m.values();
    }

    @GetMapping("/historialVB")
    public Object getHB() {
        final List<BitsoData> allBitso = this.dao.getAllBitso();
        final Map<String, List<BigDecimal>> m = new HashMap();
        final AtomicInteger cant = new AtomicInteger();
        cant.set(0);
        allBitso.stream().forEach(bd -> {
            bd.getData().stream().forEach(b -> {
                final List<BigDecimal> list;
                if (!m.containsKey(b.getName())) {
                    list = new ArrayList<>();
                    list.addAll(Arrays.asList(new BigDecimal[cant.get()]));
                    if (cant.get() != 0) {
                        list.set(cant.get() - 1, BigDecimal.ZERO);
                    }
                } else {
                    list = m.get(b.getName());
                    while (list.size() != cant.get()) {
                        list.add(BigDecimal.ZERO);
                    }
                }
                list.add(b.getAmountBTC().setScale(8, RoundingMode.FLOOR));
                m.put(b.getName(), list);
            });
            cant.getAndIncrement();
        });

        m.values().stream().forEach(ob -> {
            ob.addAll(Arrays.asList(new BigDecimal[cant.get() - ob.size()]));
            Collections.replaceAll(ob, null, BigDecimal.ZERO);
        });
        return m.values();
    }


    @SneakyThrows
    @GetMapping
    public Object get() {
        final Map<String, Balance> balanceList = this.getBalances();

        this.getValores();

        this.setGanancias(balanceList);

        this.fillSomething(balanceList);

        final List<Balance> list = balanceList.values().stream().filter(balance -> balance.getAmount().doubleValue() > 0).collect(Collectors.toList());

        list.sort((o1, o2) -> o2.getAmountARS().compareTo(o1.getAmountARS()));

        BitsoC.addTotal(list);

        this.saveBitsoData(list);

        return list;
    }

    public static void addTotal(final List<Balance> balanceList) {
        final Balance total = new Balance();
        total.setName("Total");
        total.setAmountBTC(BigDecimal.ZERO);
        total.setAmountARS(BigDecimal.ZERO);
        total.setAmountUSD(BigDecimal.ZERO);
        total.setGananciaARS(BigDecimal.ZERO);
        total.setGananciaBTC(BigDecimal.ZERO);
        balanceList.stream().forEach(cryptos -> {
            total.setAmountBTC(total.getAmountBTC().add(cryptos.getAmountBTC()));
            total.setAmountARS(total.getAmountARS().add(cryptos.getAmountARS()));
            total.setAmountUSD(total.getAmountUSD().add(cryptos.getAmountUSD()));
            if (cryptos.getGananciaARS() != null) {
                total.setGananciaARS(total.getGananciaARS().add(cryptos.getGananciaARS()));
            }
            if (cryptos.getGananciaBTC() != null) {
                total.setGananciaBTC(total.getGananciaBTC().add(cryptos.getGananciaBTC()));
            }
        });
        balanceList.add(total);
    }

    private void saveBitsoData(final List<Balance> list) {
        final BitsoData bitsoData = new BitsoData();
        bitsoData.setInstant(Instant.now());
        bitsoData.setData(list);
        this.dao.saveBitso(bitsoData);
    }

    public void fillSomething(final Map<String, Balance> balanceList) {
        balanceList.forEach((s, balance) -> {
            final BigDecimal ars = this.getValor(balance.getName(), "ars");
            final BigDecimal usd = this.getValor(balance.getName(), "usd");
            BigDecimal btc = this.getValor(balance.getName(), "btc");

            balance.setValueARS(ars);
            balance.setValueUSD(usd);
            if (balance.getName().equals("ars")) {
                btc = BigDecimal.ONE.divide(btc, 12, RoundingMode.FLOOR);
            }

            balance.setValueBTC(btc);

            balance.setAmountBTC(balance.getAmount().multiply(btc));
            balance.setAmountARS(balance.getAmount().multiply(ars));
            balance.setAmountUSD(balance.getAmount().multiply(usd));
        });
    }

    public void setGanancias(final Map<String, Balance> balanceList) throws BitsoAPIException {
        balanceList.values().forEach(balance -> balance.setGananciaBTC(balance.getAmount().multiply(this.getValor(balance.getName(), "btc"))));
        balanceList.values().forEach(balance -> balance.setGananciaARS(balance.getAmount().multiply(this.getValor(balance.getName(), "ars"))));

        BitsoOperation[] ledger;
        String last = "";
        while ((ledger = this.bitso.getLedger(null, "limit=100", "sort=asc", last)).length > 0) {
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
            last = "marker=" + ledger[ledger.length - 1].getEntryId();
        }

        final List<BitsoOperation> bitsoOperations = Arrays.asList(this.bitso.getLedger("", "sort=asc"));

        balanceList.forEach((s, balance) -> {
            switch (s) {
                case "ars":
                case "dai":
                case "eth":
                case "btc":
                    balance.setGananciaBTC(null);
                    balance.setGananciaARS(null);
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
            return v;
        }
        final BigDecimal v;
        if (c1.equals("ars") && c2.equals("usd")) {
            v = this.getValor(c2, "btc").divide(this.getValor("btc", c1), 4, RoundingMode.FLOOR);
        } else if (c1.equals("usd") && c2.equals("ars")) {
            v = this.getValor(c1, "btc").divide(this.getValor("btc", c2), 4, RoundingMode.FLOOR);
        } else {
            v = this.getValor(c1, "btc").multiply(this.getValor("btc", c2));
        }
        return v;
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
