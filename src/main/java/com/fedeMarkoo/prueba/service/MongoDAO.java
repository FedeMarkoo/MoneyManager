package com.fedeMarkoo.prueba.service;

import com.fedeMarkoo.prueba.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

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
		return BD.findOne(query, Periodo.class);
	}

	@Override
	public void savePeriodo(Periodo periodo) {
		BD.remove(periodo);
		BD.save(periodo);
	}

	@Override
	public Periodo getPeriodoLast() {
		return getAllPeridosSorted().get(0);
	}

	@Override
	public List<Periodo> getAllPeridosSorted() {
		List<Periodo> result = BD.findAll(Periodo.class);
		result.sort((o1, o2) -> {
			return o2.getPeriodo().compareTo(o1.getPeriodo());
		});
		return result;
	}

	@Override
	public List<PeriodoHistorico> getPeriodosHistoricos() {
		List<PeriodoHistorico> list = BD.findAll(PeriodoHistorico.class);
		list.sort(Comparator.comparing(PeriodoHistorico::getDecrypt));
		return list;
	}

	@Override
	public void savePeriodoHistorico(PeriodoHistorico perTemp) {
		BD.remove(perTemp);
		BD.save(perTemp);
	}

	@Override
	public PeriodoHistorico getPeriodosHistorico(String decrypt) {
		Criteria criteria = Criteria.where("decrypt").is(decrypt);
		Query query = new Query().addCriteria(criteria);
		return BD.findOne(query, PeriodoHistorico.class);
	}

	@Override
	public void removePeriodoHistorico(String periodo) {
		Criteria criteria = Criteria.where("decrypt").is(periodo);
		Query query = new Query().addCriteria(criteria);
		BD.findAndRemove(query, PeriodoHistorico.class);
	}
}
