package com.jbase.generic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

/**
 * @author michel.pech
 */

@Service
public abstract class BaseService<T, ID> {

    protected BaseRepositoryImpl<T, ID> repository;

    @Transactional
    public List<T> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

	@Transactional
    public List<T> findAll(Pageable pageable, Sort sort) {
        return (List<T>) repository.findAll(pageable, sort);
    }

	@Transactional
    public List<T> findAll(Pageable pageable) {
        return (List<T>) repository.findAll(pageable);
    }

	@Transactional
    public List<T> findAll(Sort sort) {
        return (List<T>) repository.findAll(sort);
    }

    @Transactional
    public T save(T entity) {
        repository.saveAndFlush(entity);
        return entity;
    }

    @Transactional
    public List<T> saveAll(List<T> entityList) {
        repository.saveAllAndFlush(entityList);
        return entityList;
    }

    @Transactional
    public void deleteByEntity(T entity) {
        repository.delete(entity);
    }

    @Transactional
    public void deleteById(ID id) {
        repository.deleteById(id);
    }
}
