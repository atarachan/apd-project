package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.config.EntityManagerFactoryProvider;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

// represents an abstract repository for implementing other repositories
public class AbstractRepository<T, ID> implements IRepository<T, ID> {

    private final Class<T> entityClass;

    protected AbstractRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(entity);
            tx.commit();

            return entity;
        }
        catch (RuntimeException ex) {
            rollbackIfActive(tx);
            throw ex;
        }
        finally {
            em.close();
        }
    }

    @Override
    public T update(T entity) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            T updatedEntity = em.merge(entity);

            tx.commit();

            return updatedEntity;
        }
        catch (RuntimeException ex) {
            rollbackIfActive(tx);
            throw ex;
        }
        finally{
            em.close();
        }
    }

    @Override
    public void delete(T entity) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();

            T managedEntity = em.contains(entity) ? entity : em.merge(entity);

            em.remove(managedEntity);

            tx.commit();
        }
        catch (RuntimeException ex) {
            rollbackIfActive(tx);
            throw ex;
        }
        finally{
            em.close();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();

        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        }
        finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll(){
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();

        try {
            String query = "SELECT e FROM " + entityClass.getSimpleName() + " e";

            return  em.createQuery(query, entityClass).getResultList();
        }
        finally {
            em.close();
        }
    }

    @Override
    public void deleteById(ID id) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            T entity = em.find(entityClass, id);

            if(entity != null){
                em.remove(entity);
            }

            tx.commit();
        }
        catch (RuntimeException ex) {
            rollbackIfActive(tx);
            throw ex;
        }
        finally{
            em.close();
        }
    }

    protected void rollbackIfActive(EntityTransaction tx) {
        if (tx.isActive()) {
            tx.rollback();
        }
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    protected EntityManager createEntityManager() {
        return EntityManagerFactoryProvider.createEntityManager();
    }

}
