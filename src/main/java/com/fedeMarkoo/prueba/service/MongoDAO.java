package com.fedeMarkoo.prueba.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fedeMarkoo.prueba.model.Cuota;
import com.fedeMarkoo.prueba.model.Movimiento;
import com.fedeMarkoo.prueba.model.Periodo;
import com.fedeMarkoo.prueba.model.Registro;

@Repository
@Transactional
public class MongoDAO implements IMongoDAO {

	@Autowired
	private MongoTemplate BD;

	@Override
	public List<Movimiento> getMovimientos() {
		return BD.findAll(Movimiento.class);
	}

	@Override
	public List<Registro> getRegistros() {
		return BD.findAll(Registro.class);
	}

	@Override
	public List<Cuota> getCuotas() {
		return BD.findAll(Cuota.class);
	}

	@Override
	public void createRegistro(Registro registro) {
		if (!this.existRegistro(registro)) {
			BD.save(registro);
		}
	}

	@Override
	public boolean existRegistro(Registro registro) {
		Criteria criteria = Criteria.where("comprobante").is(registro.getComprobante());
		criteria = criteria.and("periodo").is(null);
		Query query = new Query().addCriteria(criteria);
		return BD.exists(query, Registro.class);
	}

	@Override
	public Periodo getPeriodo(String periodo) {
		Criteria criteria = Criteria.where("periodo").is(periodo);
		Query query = new Query().addCriteria(criteria);
		Periodo result = BD.findOne(query, Periodo.class);
		return result;
	}

	@Override
	public void savePeriodo(Periodo periodo) {
		BD.remove(periodo);
		
		BD.save(periodo);
	}
}
