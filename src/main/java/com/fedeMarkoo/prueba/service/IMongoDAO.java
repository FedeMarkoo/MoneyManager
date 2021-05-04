package com.fedeMarkoo.prueba.service;

import com.fedeMarkoo.prueba.model.*;
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
        return this.BD.findAll(Movimiento.class);
    }

    @Override
    public List<Registro> getRegistros() {
        return this.BD.findAll(Registro.class);
    }

    @Override
    public List<Cuota> getCuotas() {
        return this.BD.findAll(Cuota.class);
    }

    @Override
    public void createRegistro(final Registro registro) {
        if (!this.existRegistro(registro)) {
            this.BD.save(registro);
        }
    }

    @Override
    public boolean existRegistro(final Registro registro) {
        Criteria criteria = Criteria.where("comprobante").is(registro.getComprobante());
        criteria = criteria.and("periodo").is(null);
        final Query query = new Query().addCriteria(criteria);
        return this.BD.exists(query, Registro.class);
    }

    @Override
    public Periodo getPeriodo(final String periodo) {
        final Criteria criteria = Criteria.where("periodo").is(periodo);
        final Query query = new Query().addCriteria(criteria);
        return this.BD.findOne(query, Periodo.class);
    }

    @Override
    public void savePeriodo(final Periodo periodo) {
        this.BD.remove(periodo);
        this.BD.save(periodo);
    }

    @Override
    public Periodo getPeriodoLast() {
        return this.getAllPeridosSorted().get(0);
    }

    @Override
    public List<Periodo> getAllPeridosSorted() {
        final List<Periodo> result = this.BD.findAll(Periodo.class);
        result.sort((o1, o2) -> {
            final String[] periodo = o2.getPeriodo().split("-");
            final String[] periodo1 = o1.getPeriodo().split("-");
            final int compare = periodo[1].compareTo(periodo1[1]);
            return compare == 0 ? periodo[0].compareTo(periodo1[1]) : compare;
        });
        return result;
    }

    @Override
    public List<PeriodoHistorico> getPeriodosHistoricos() {
        final List<PeriodoHistorico> list = this.BD.findAll(PeriodoHistorico.class);
        list.sort(Comparator.comparing(PeriodoHistorico::getDecrypt));
        return list;
    }

    @Override
    public void savePeriodoHistorico(final PeriodoHistorico perTemp) {
        this.BD.remove(perTemp);
        this.BD.save(perTemp);
    }

    @Override
    public PeriodoHistorico getPeriodosHistorico(final String decrypt) {
        final Criteria criteria = Criteria.where("decrypt").is(decrypt);
        final Query query = new Query().addCriteria(criteria);
        return this.BD.findOne(query, PeriodoHistorico.class);
    }

    @Override
    public void removePeriodoHistorico(final String periodo) {
        final Criteria criteria = Criteria.where("decrypt").is(periodo);
        final Query query = new Query().addCriteria(criteria);
        this.BD.findAndRemove(query, PeriodoHistorico.class);
    }

    @Override
    public void updateMovs(final Movimiento mov) {
        final Query query = new Query();
        final Update update = new Update();

        query.addCriteria(Criteria.where("movimientos.comprobante").is(mov.getComprobante()));
        update.set("movimientos.$.clasificacion", mov.getClasificacion());

        this.BD.updateMulti(query, update, Periodo.class);

    }

    @Override
    public String getClasificacionByComprobante(final String comprobante) {

        final MongoCollection<Document> collection = this.BD.getCollection("Periodos");

        final Bson elemMatch = elemMatch("movimientos", Filters.eq("comprobante", comprobante));
        final Bson match = Projections.elemMatch("movimientos.clasificacion");

        final FindIterable<Document> find = collection.find(elemMatch);
        final FindIterable<Document> projection = find.projection(
                Projections.fields(
                        match)
        );

        final Document first = projection.first();
        if (first == null) {
            return "Otros";
        }

        return first.getList("movimientos", Document.class).get(0).getString("clasificacion");
    }

    @Override
    public void saveBitso(final BitsoData bitsoData) {
        this.BD.save(bitsoData);
    }

    @Override
    public List<BitsoData> getAllBitso() {
        return this.BD.findAll(BitsoData.class);
    }
}
