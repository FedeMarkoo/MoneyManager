package com.fedeMarkoo.prueba.service;

import com.fedeMarkoo.prueba.model.Cuota;
import com.fedeMarkoo.prueba.model.Movimiento;
import com.fedeMarkoo.prueba.model.Periodo;
import com.fedeMarkoo.prueba.model.PeriodoHistorico;
import com.fedeMarkoo.prueba.model.Registro;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static com.mongodb.client.model.Filters.elemMatch;

@Repository
@Transactional
public class IMongoDAO implements MongoDAO {

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
			String[] periodo = o2.getPeriodo().split("-");
			String[] periodo1 = o1.getPeriodo().split("-");
			int compare = periodo[1].compareTo(periodo1[1]);
			return compare == 0 ? periodo[0].compareTo(periodo1[1]) : compare;
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

	@Override
	public void updateMovs(Movimiento mov) {
		Query query = new Query();
		Update update = new Update();

		query.addCriteria(Criteria.where("movimientos.comprobante").is(mov.getComprobante()));
		update.set("movimientos.$.clasificacion", mov.getClasificacion());

		BD.updateMulti(query, update, Periodo.class);

	}

	@Override
	public String getClasificacionByComprobante(String comprobante) {

		MongoCollection<Document> collection = BD.getCollection("Periodos");

		Bson elemMatch = elemMatch("movimientos", Filters.eq("comprobante", comprobante));
		Bson match = Projections.elemMatch("movimientos.clasificacion");

		FindIterable<Document> find = collection.find(elemMatch);
		FindIterable<Document> projection = find.projection(
				Projections.fields(
						match)
		);

		Document first = projection.first();
		if (first == null) return "Otros";

		return first.getList("movimientos", Document.class).get(0).getString("clasificacion");
	}
}
