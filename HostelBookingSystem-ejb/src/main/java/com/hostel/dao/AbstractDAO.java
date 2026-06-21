package com.hostel.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDAO<T, ID extends Serializable> {

    @PersistenceContext(unitName = "hostelPU")
    protected EntityManager em;

    private final Class<T> entityClass;

    protected AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        em.persist(entity);
        em.flush();
        return entity;
    }

    public T update(T entity) {
        T merged = em.merge(entity);
        em.flush();
        return merged;
    }

    public void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
        em.flush();
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    public List<T> findAll() {
        TypedQuery<T> query = em.createQuery(
            "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
        return query.getResultList();
    }

    public long count() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class);
        return query.getSingleResult();
    }
}
