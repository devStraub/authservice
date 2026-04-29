package com.jbase.generic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author michel.pech
 */


@Tag(name = "Endpoints", description = "Utilize para alimentar os dados")
public abstract class BaseController<T, ID> {

    @Autowired
    protected BaseService<T, ID> service;

    @Operation(hidden = false, summary = "Listar todos os registros")
    @GetMapping("/findAll")
    public ResponseEntity<List<T>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(hidden = true, summary = "Listar registros com paginação e ordenação")
    @GetMapping("/findAllPaged")
    public ResponseEntity<?> findAllPaged(
            @Parameter(description = "Configuração de paginação") Pageable pageable,
            @Parameter(description = "Campo de ordenação (opcional)") @RequestParam(required = false) String sortField,
            @Parameter(description = "Direção da ordenação (ASC/DESC)") @RequestParam(required = false) String sortDir) {

        Sort sort = Sort.unsorted();
        if (sortField != null && sortDir != null) {
            sort = Sort.by(Sort.Direction.fromString(sortDir), sortField);
        }
        return ResponseEntity.ok(service.findAll(pageable, sort));
    }

    @Operation(hidden = true, summary = "Buscar por ID")
    @GetMapping("/findById/{id}")
    public ResponseEntity<T> findById(@PathVariable ID id) {
        Optional<T> result = service.findById(id);
        return result.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @Operation(hidden = false, summary = "Salvar ou atualizar um registro")
    @PostMapping("/save")
    public ResponseEntity<T> save(@RequestBody T entity) {
        return ResponseEntity.ok(service.save(entity));
    }

    @Operation(hidden = true, summary = "Salvar ou atualizar múltiplos registros")
    @PostMapping("/saveAll")
    public ResponseEntity<List<T>> saveAll(@RequestBody List<T> entityList) {
        return ResponseEntity.ok(service.saveAll(entityList));
    }

    @Operation(hidden = true, summary = "Excluir por entidade")
    @DeleteMapping("/deleteByEntity")
    public ResponseEntity<Void> deleteByEntity(@RequestBody T entity) {
        service.deleteByEntity(entity);
        return ResponseEntity.ok().build();
    }

    @Operation(hidden = true, summary = "Excluir por ID")
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable ID id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
