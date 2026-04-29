package com.jbase.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@NoRepositoryBean
@Transactional
public class BaseRepositoryImpl<T, ID> implements JpaRepository<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> domainClass;

    protected BaseRepositoryImpl(Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    // ---------- CUSTOM QUERIES ---------- //

    // 🟦 NamedQuery
    public Page<T> findByNamedQuery(String namedQuery, Map<String, Object> params) {
        return findByNamedQuery(namedQuery, params, null, null);
    }

    // 🟦 NamedQuery - com paginação
    public Page<T> findByNamedQuery(String namedQuery, Map<String, Object> params, Pageable pageable) {
        return findByNamedQuery(namedQuery, params, pageable, null);
    }

    // 🟦 NamedQuery - com paginação e ordenação
    public Page<T> findByNamedQuery(String namedQuery, Map<String, Object> params, Pageable pageable, Sort sort) {
        StringBuilder jpql = new StringBuilder("SELECT e FROM " + getEntityName().toLowerCase() + " e");

        // Aplica ordenação
        if (sort != null && sort.isSorted()) {
            jpql.append(" ORDER BY ");
            List<String> orders = sort.stream()
                    .map(order -> "e." + order.getProperty() + " " + order.getDirection().name())
                    .toList();
            jpql.append(String.join(", ", orders));
        }

        TypedQuery<T> query = entityManager.createNamedQuery(namedQuery, domainClass);

        // Aplica parâmetros
        if (params != null && !params.isEmpty()) {
            params.forEach(query::setParameter);
        }

        // Aplica paginação
        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<T> content = query.getResultList();

        // Conta total — executa novamente sem paginação (forma simples)
        TypedQuery<Long> countQuery = entityManager.createQuery(
                "SELECT COUNT(e) FROM " + getEntityName().toLowerCase() + " e", Long.class);
        long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    // 🟥 NativeQuery
    public Page<T> findByNativeQuery(String sqlQuery, Map<String, Object> params) {
        return findByNativeQuery(sqlQuery, params, null, null);
    }

    // 🟥 NativeQuery - com paginação
    public Page<T> findByNativeQuery(String sqlQuery, Map<String, Object> params, Pageable pageable) {
        return findByNativeQuery(sqlQuery, params, pageable, null);
    }

    // 🟥 NativeQuery - com paginação e ordenação
    public Page<T> findByNativeQuery(String sqlQuery, Map<String, Object> params, Pageable pageable, Sort sort) {
        StringBuilder sql = new StringBuilder(sqlQuery);

        // Aplica ordenação
        if (sort != null && sort.isSorted()) {
            sql.append(" ORDER BY ");
            List<String> orders = sort.stream()
                    .map(order -> order.getProperty() + " " + order.getDirection().name())
                    .toList();
            sql.append(String.join(", ", orders));
        }

        Query query = entityManager.createNativeQuery(sql.toString(), domainClass);

        // Aplica parâmetros
        if (params != null && !params.isEmpty()) {
            params.forEach(query::setParameter);
        }

        // Aplica paginação
        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<T> content = query.getResultList();

        // Conta total — simples: executa uma COUNT(*) sobre a query base
        long total = content.size();
        if (pageable != null) {
            String countSql = "SELECT COUNT(*) FROM (" + sqlQuery + ") AS total_count";
            Query countQuery = entityManager.createNativeQuery(countSql);
            if (params != null && !params.isEmpty()) {
                params.forEach(countQuery::setParameter);
            }
            total = ((Number) countQuery.getSingleResult()).longValue();
        }

        return new PageImpl<>(content, pageable, total);
    }

    // ---------- CRUD PRINCIPAL ---------- //

    @Override
    public <S extends T> S save(S entity) {
        if (entityManager.contains(entity) || getId(entity) != null) {
            return entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
            return entity;
        }
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager.find(domainClass, id));
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public List<T> findAll() {
        String jpql = "SELECT e FROM " + getEntityName().toLowerCase() + " e";
        return entityManager.createQuery(jpql, domainClass).getResultList();
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        String jpql = "SELECT e FROM " + getEntityName().toLowerCase() + " e WHERE e.id IN :ids";
        return entityManager.createQuery(jpql, domainClass)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public long count() {
        String jpql = "SELECT COUNT(e) FROM " + getEntityName().toLowerCase() + " e";
        return entityManager.createQuery(jpql, Long.class).getSingleResult();
    }

    @Override
    public void deleteById(ID id) {
        findById(id).ifPresent(entityManager::remove);
    }

    @Override
    public void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        findAllById((Iterable<ID>) ids).forEach(this::delete);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        findAll().forEach(this::delete);
    }

    // ---------- PAGINAÇÃO E ORDENAÇÃO ---------- //

    public Page<T> findAll(Pageable pageable, Sort sort) {
        // Monta o JPQL base
        StringBuilder jpql = new StringBuilder("SELECT e FROM " + getEntityName().toLowerCase() + " e");

        // Aplica ordenação, se existir
        if (sort != null && sort.isSorted()) {
            jpql.append(" ORDER BY ");
            List<String> orders = sort.stream()
                    .map(order -> "e." + order.getProperty() + " " + order.getDirection().name())
                    .toList();
            jpql.append(String.join(", ", orders));
        }

        // Cria a query tipada
        TypedQuery<T> query = entityManager.createQuery(jpql.toString(), domainClass);

        // Configura a paginação
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Executa a consulta e obtém resultados
        List<T> content = query.getResultList();

        // Conta total (sem paginação)
        long total = count();

        // Retorna um PageImpl com os resultados e metadados
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<T> findAll(Sort sort) {
        return findAll(Pageable.unpaged(), sort).getContent();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return findAll(pageable, Sort.unsorted());
    }

    // ---------- OUTROS MÉTODOS AUXILIARES ---------- //

    @Override
    public void flush() {
        entityManager.flush();
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        S saved = save(entity);
        flush();
        return saved;
    }

    @Override
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        List<S> saved = saveAll(entities);
        flush();
        return saved;
    }

    @Override
    public void deleteAllInBatch(Iterable<T> entities) {
        deleteAll(entities);
        flush();
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<ID> ids) {
        deleteAllById(ids);
        flush();
    }

    @Override
    public void deleteAllInBatch() {
        deleteAll();
        flush();
    }

    @Override
    public T getOne(ID id) {
        return getReferenceById(id);
    }

    @Override
    public T getById(ID id) {
        return entityManager.find(domainClass, id);
    }

    @Override
    public T getReferenceById(ID id) {
        return entityManager.getReference(domainClass, id);
    }

    // ---------- MÉTODOS DE EXAMPLE (simplificados) ---------- //

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        List<S> results = findAll(example);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        // Implementação mínima - ignora Example e retorna tudo
        return (List<S>) findAll();
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return (List<S>) findAll(sort);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return (Page<S>) findAll(pageable);
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return count();
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return count() > 0;
    }

    @Override
    public <S extends T, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        // Apenas uma implementação placeholder
        return null;
    }

    // ---------- MÉTODO UTILITÁRIO PARA PEGAR ID VIA REFLEXÃO ---------- //

    @SuppressWarnings("unchecked")
    private ID getId(T entity) {
        try {
            var idField = Arrays.stream(entity.getClass().getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(jakarta.persistence.Id.class))
                    .findFirst()
                    .orElse(null);

            if (idField == null)
                return null;

            idField.setAccessible(true);
            return (ID) idField.get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    private String getEntityName() {
        Entity entityAnnotation = domainClass.getAnnotation(Entity.class);
        if (entityAnnotation != null && !entityAnnotation.name().isEmpty()) {
            return entityAnnotation.name();
        }
        return domainClass.getSimpleName();
    }

}
