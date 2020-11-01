package com.fedeMarkoo.prueba.controller;

import com.fedeMarkoo.prueba.model.*;
import com.fedeMarkoo.prueba.service.IMongoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/movs")
public class MovimientosController {

    private static final String MASTER_COMPRA = "(\\d+/\\d+/\\d+)\\s+([^\\t]+)\\s+(\\d+)\\t([\\d,]+)\\s*([\\d,]+)";
    private static final String VISA_COMPRA = "(\\d+\\/\\d+\\/\\d+)\\s+([\\w \\(\\)\\*\\/\\d,-\\.]+)\\s+([\\d\\w]+)\\s+([\\.\\d,]+)\\s+([\\d,]+)";

    private static final String MASTER_CUOTA = "(\\d+\\/\\d+\\/\\d+)(?:\\t)+ ([\\w\\s\\*]+)(?:\\t)+(\\d+)(?:\\t)+(\\d+)(?:\\t)+(\\d+)(?:\\t)+([\\d,]+)\\s*(?:\\t)+([\\d,]+)";
    private static final String VISA_CUOTA = "(\\d+\\/\\d+\\/\\d+)\\s+([\\w \\(\\)\\*\\d,-\\.]+)\\s+([\\d\\w]+)\\s+([\\.\\d]{1,2})\\s+([\\d]{1,2})\\s*([\\d,\\.]+)\\s*([\\d,]+)";

    private static Periodo periodo;

    @Autowired
    private IMongoDAO mongo;
    private int counter = 0;

    @GetMapping("/get/{periodoS}")
    public @ResponseBody
    ResponseEntity<Periodo> get(@PathVariable String periodoS) {
        Periodo periodo = mongo.getPeriodo(periodoS);
        ResponseEntity<Periodo> responseEntity = new ResponseEntity<Periodo>(periodo, HttpStatus.OK);
        return responseEntity;
    }

    @GetMapping("/get")
    public @ResponseBody
    ResponseEntity<Periodo> get() {
        Periodo periodo = mongo.getPeriodoLast();
        ResponseEntity<Periodo> responseEntity = new ResponseEntity<Periodo>(periodo, HttpStatus.OK);
        return responseEntity;
    }

    @GetMapping("/getProyeccionHistorico")
    public @ResponseBody
    ResponseEntity<ProyeccionHistorico> getProyeccionHistorico() {
        List<Periodo> periodo = mongo.getAllPeridosSorted();
        ProyeccionHistorico historicos = new ProyeccionHistorico();

        ProyeccionController.addSueldo(periodo, historicos);
        ProyeccionController.addCuotas(periodo, historicos);
        ProyeccionController.addCompras(periodo, historicos);
        ProyeccionController.addAnteriores(historicos);
        ProyeccionController.addResto(historicos);

        ResponseEntity<ProyeccionHistorico> responseEntity = new ResponseEntity<ProyeccionHistorico>(historicos, HttpStatus.OK);
        return responseEntity;
    }


    @PostMapping("/save")
    public void save(@RequestBody String data, HttpServletRequest request) {
        masterCompra(data);
        masterCuota(data);

        visaCompra(data);
        visaCuota(data);

        System.out.println(data);
    }

    @PostMapping("/master2")
    private void masterCuota(@RequestBody String data) {
        if (MovimientosController.periodo == null)
            return;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Pattern pattern = Pattern.compile(MASTER_CUOTA);
        Matcher m = pattern.matcher(data);
        while (m.find()) {
            try {
                String comprobante = m.group(3);
                Cuota cuota = new Cuota(m.group(2), "MasterCard", simpleDateFormat.parse(m.group(1)), comprobante,
                        m.group(6), m.group(7), m.group(4), m.group(5));
                if (!periodo.getCuotas().contains(cuota)) {
                    periodo.getCuotas().add(cuota);
                    System.out.println(cuota);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        savePeriodo();
        System.out.println("masterCuota");
    }

    @PostMapping("/visa2")
    private void visaCuota(@RequestBody String data) {
        if (MovimientosController.periodo == null)
            return;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Pattern pattern = Pattern.compile(VISA_CUOTA);
        Matcher m = pattern.matcher(data);
        while (m.find()) {
            try {
                String comprobante = m.group(3);
                Cuota cuota = new Cuota(m.group(2), "Visa", simpleDateFormat.parse(m.group(1)), comprobante, m.group(6),
                        m.group(7), m.group(4), m.group(5));
                if (!periodo.getCuotas().contains(cuota)) {
                    periodo.getCuotas().add(cuota);
                    System.out.println(cuota);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        savePeriodo();
        System.out.println("visaCuota");
    }

    @PostMapping("/master1")
    private void masterCompra(@RequestBody String data) {
        if (MovimientosController.periodo == null)
            return;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Pattern pattern = Pattern.compile(MASTER_COMPRA);
        Matcher m = pattern.matcher(data);
        while (m.find()) {
            try {
                String comprobante = m.group(3);
                Movimiento movimiento = new Movimiento(m.group(2), "MasterCard", simpleDateFormat.parse(m.group(1)),
                        comprobante, new Double(m.group(4).replace(".", "").replace(",", ".")),
                        new Double(m.group(5).replace(".", "").replace(",", ".")));
                if (!periodo.getMovimientos().contains(movimiento)) {
                    periodo.getMovimientos().add(movimiento);
                    System.out.println(movimiento);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        savePeriodo();
        System.out.println("masterCompra");
    }

    @PostMapping("/visa1")
    private void visaCompra(@RequestBody String data) {
        if (MovimientosController.periodo == null)
            return;
        Pattern pattern = Pattern.compile(VISA_COMPRA);
        Matcher m = pattern.matcher(data);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        while (m.find()) {
            try {
                String comprobante = m.group(3);
                Movimiento movimiento = new Movimiento(m.group(2), "Visa", simpleDateFormat.parse(m.group(1)),
                        comprobante, new Double(m.group(4).replace(".", "").replace(",", ".")),
                        new Double(m.group(5).replace(".", "").replace(",", ".")));
                if (!periodo.getMovimientos().contains(movimiento)) {
                    periodo.getMovimientos().add(movimiento);
                    System.out.println(movimiento);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("visaCompra");
        savePeriodo();
    }

    private void savePeriodo() {
        if (++this.counter == 4) {
            mongo.savePeriodo(MovimientosController.periodo);
            this.counter = 0;
        }
    }

    @PostMapping("/setPeriodo")
    public void setPeriodo(@RequestBody String periodo) {
        if (MovimientosController.periodo != null && periodo.equals(MovimientosController.periodo.getPeriodo()))
            return;
        Periodo p = mongo.getPeriodo(periodo);
        if (p == null) {
            p = new Periodo();
            p.setPeriodo(periodo);
            p.setCuotas(new ArrayList<>());
            p.setMovimientos(new ArrayList<>());
            mongo.savePeriodo(p);
        }
        MovimientosController.periodo = p;
    }
}
