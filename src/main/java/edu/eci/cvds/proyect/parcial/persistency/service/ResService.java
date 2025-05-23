package edu.eci.cvds.proyect.parcial.persistency.service;


import edu.eci.cvds.proyect.parcial.persistency.dto.ResDto;
import edu.eci.cvds.proyect.parcial.persistency.entity.Res;
import edu.eci.cvds.proyect.parcial.persistency.exception.ResException;
import edu.eci.cvds.proyect.parcial.persistency.repository.ResRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ResService {

    private final ResRepository resRepository;
    private static final String RES_ID_NULL = "El ID de la reserva no puede ser null";
    private static final String RES_ID_NOT_FOUND = "Reserva no encontrada con ID: ";
    private static final String RES_NOT_FOUND_MESSAGE = "Nombre del usuario no encontrado: ";

    public ResService(ResRepository resRepository) {
        this.resRepository = resRepository;
    }


    public List<Res> getAll(){
        return resRepository.findAll();
    }

    public Res getOne(Integer id){
        if (id == null) {
            throw new IllegalArgumentException(RES_ID_NULL);
        }
        return resRepository.findById(id).orElseThrow(() -> new RuntimeException(RES_ID_NOT_FOUND + id));
    }

    

    public List<Res> getResStatus(String resStatus) {
        if (resStatus == null) {
            throw new IllegalArgumentException("Estado de reserva no puede ser nulo");
        }
        return resRepository.findByResStatus(resStatus)
                .orElseThrow(() -> new RuntimeException("Estado de la reserva no encontrado: " + resStatus));
    }
    

    public Res save(ResDto resDto) { 
        if (resDto == null) {
            throw new ResException("El artículo no puede ser nulo");
        }
        Integer id = autoIncrement();
        
        Res res = new Res(id, resDto.getName(), resDto.getCedula(),resDto.getEmail(),resDto.getCreationDate(),resDto.getEspecialidad(),resDto.getNameDoctor(),resDto.getLocation(),resDto.getResStatus());
        Res saved = resRepository.save(res);
        
        return saved;
    }

    
    public Res update(Integer id, ResDto resDto){
        if (id == null) {
            throw new IllegalArgumentException(RES_ID_NULL);
        }
        Res res = resRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(RES_ID_NOT_FOUND + id));
    
        res.setResStatus(resDto.getResStatus());
        
    
        Res updated = resRepository.save(res);
        return updated;
    }
    
    private Integer autoIncrement(){
        List<Res> articles= resRepository.findAll();
        return articles.isEmpty() ? 1 : articles.stream()
                .max(Comparator.comparing(Res::getId))
                .orElseThrow(() -> new RuntimeException("No se pudo determinar el siguiente ID"))
                .getId() + 1;    
    }
    

    public Optional<List<Res>> findByResStatus(String status) {
        return resRepository.findByName(status);
    }
    public Optional<List<Res>> findByName(String name) {
        return resRepository.findByName(name);
    }

    
}