package ca.senecacollege.malibuluminahotel.repositories;

import java.util.List;
import java.util.Optional;

// represents a template repository interface for implementing other repositories interfaces
public interface IRepository<T, ID> {

    T save(T entity);

    T update(T entity);

    void delete(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);
}
