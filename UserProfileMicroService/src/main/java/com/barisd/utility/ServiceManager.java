package com.barisd.utility;

import com.barisd.repository.entity.BaseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public class ServiceManager<T extends BaseEntity,ID> implements IService<T,ID> {

    private final MongoRepository <T,ID> mongoRepository;

    public ServiceManager(MongoRepository<T, ID> mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public T save(T t) {
        Long time=System.currentTimeMillis();
        t.setCreateAt(time);
        t.setUpdateAt(time);
        t.setState(true);
        return mongoRepository.save(t);
    }

    @Override
    public Iterable<T> saveAll(Iterable<T> t) {
        Long time=System.currentTimeMillis();
        t.forEach(x->{
            x.setCreateAt(time);
            x.setUpdateAt(time);
            x.setState(true);
        });

        return mongoRepository.saveAll(t);
    }

    @Override
    public T update(T t) {
        t.setUpdateAt(System.currentTimeMillis());
        return mongoRepository.save(t); //jpaRepository içinde update metodu yoktur.
        // save metodu içine gönderilen entity içinde id varsa update işlemi yapar,
        // eğer entity içinde id değeri yoksa save işlemi yapar.
    }

    @Override
    public void delete(T t) {
        mongoRepository.delete(t);
    }

    @Override
    public void deleteById(ID id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public Optional<T> findById(ID id) {
        return mongoRepository.findById(id);
    }

    @Override
    public List<T> findAll() {
        return mongoRepository.findAll();
    }
}
