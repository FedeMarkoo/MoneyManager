package com.fedeMarkoo.prueba.service;

import com.fedeMarkoo.prueba.model.*;

import java.util.List;

public interface MongoDAO {

    List<Registro> getRegistros();

    List<Movimiento> getMovimientos();

    List<Cuota> getCuotas();

    void createRegistro(Registro registro);

    boolean existRegistro(Registro registro);

    Periodo getPeriodo(String periodo);

    void savePeriodo(Periodo p);

    Periodo getPeriodoLast();

    List<Periodo> getAllPeridosSorted();

    List<PeriodoHistorico> getPeriodosHistoricos();

    void savePeriodoHistorico(PeriodoHistorico perTemp);

    PeriodoHistorico getPeriodosHistorico(String decrypt);

    void removePeriodoHistorico(String periodo);

    void updateMovs(Movimiento mov);

    String getClasificacionByComprobante(String comprobante);


    void saveBitso(BitsoData bitsoData);

    List<BitsoData> getAllBitso();
}
