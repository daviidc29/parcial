package edu.eci.cvds.proyect.parcial.persistency.repository;

import edu.eci.cvds.proyect.parcial.persistency.entity.Res;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ResRepository extends MongoRepository<Res, Integer> {
    Optional<List<Res>> findByResStatus(String resStatus);
    Optional<List<Res>> findByName(String name);

}