package com.fedeMarkoo.prueba.service;

import java.util.List;

import com.fedeMarkoo.prueba.model.Cuota;
import com.fedeMarkoo.prueba.model.Movimiento;
import com.fedeMarkoo.prueba.model.Periodo;
import com.fedeMarkoo.prueba.model.Registro;

public interface IMongoDAO {

	public List<Registro> getRegistros();

	public List<Movimiento> getMovimientos();

	public List<Cuota> getCuotas();

	public void createRegistro(Registro registro);
	
	public boolean existRegistro(Registro registro);

	public Periodo getPeriodo(String periodo);

	public void savePeriodo(Periodo p);
}
