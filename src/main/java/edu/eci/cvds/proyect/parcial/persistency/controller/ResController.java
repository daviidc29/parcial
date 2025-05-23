package edu.eci.cvds.proyect.parcial.persistency.controller;

import edu.eci.cvds.proyect.parcial.persistency.exception.ResException;
import edu.eci.cvds.proyect.parcial.persistency.dto.ResDto;
import edu.eci.cvds.proyect.parcial.persistency.entity.Res;
import edu.eci.cvds.proyect.parcial.persistency.service.ResService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/Res")
@Slf4j
@Tag(name = "Reservas", description = "Gestión de reservas de citas medicas")
public class ResController {
     
    private ResService resService;
    private static final String ERROR_KEY = "Error";
    private static final String MESSAGE_KEY = "Message";
    private static final Logger logger = LoggerFactory.getLogger(ResController.class);

    @Autowired
    public ResController(ResService resService) {
        this.resService = resService;
    }

    @GetMapping
    @Operation(
        summary = "Buscar u obtener citas", 
        description = """
            Permite buscar citas por diferentes criterios:
            - Sin parámetros: Retorna todos los artículos
            - Número: Búsqueda por ID
            - Estado válido: Filtra por estado (Confirmada, Rechazada)
            - Nombre: Búsqueda por nombre
            """,
        parameters = {
            @Parameter(
                name = "q",
                description = "Criterio de búsqueda (ID, estado, nombre)",
                examples = {
                    @ExampleObject(name = "Todos", value = ""),
                    @ExampleObject(name = "Por ID", value = "1"),
                    @ExampleObject(name = "Por estado", value = "Confirmada"),
                    @ExampleObject(name = "Por nombre", value = "Juan")
                }
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Reservas encontradas",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                            "cantidad": 2,
                            "reservas": [
                                {
                                    "id": 1,
                                    "name": "Juan",
                                    "status": "Confimada",
                                    "especialidad": "Odontologia",
                                    "creationDate": "05/26/2025"
                                }
                                
                            ]
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Resrva no encontrado",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                            "error": "Reserva no encontrado",
                            "details": "No se encontró reserva con ID: 99"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                            "error": "Error al obtener reserva",
                            "details": "Error de conexión a la base de datos"
                        }
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<?> getRes(@RequestParam(value = "q", required = false) String q) {
        try {
            List<Res> result;

            if (q == null || q.isBlank()) {
                result = resService.getAll();
            } else if (q.matches("\\d+")) {
                Res res = resService.getOne(Integer.parseInt(q));
                result = (res != null) ? List.of(res) : List.of();
            } else {
                Set<String> estadosValidos = Set.of("Confirmada", "Rechazada");
                result = estadosValidos.contains(q)
                        ? resService.getResStatus(q)
                        : null;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("cantidad", result.size());
            response.put("reservas", result);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener reservas: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener reservas");
            errorResponse.put("details", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PostMapping
    @Operation(
        summary = "Crear reserva",
        description = "Registra una nueva  reserva en el sistema",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de reserva a crear",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResDto.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "id": 1,
                        "name": "Juan",
                        "status": "Confimada",
                        "especialidad": "Odontologia",
                        "creationDate": "05/26/2025"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Reserva creada",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                            
                            "id": 1,
                            "name": "Juan",
                            "status": "Confimada",
                            "especialidad": "Odontologia",
                            "creationDate": "05/26/2025"
                                
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Datos inválidos",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                            "Error": "Error al guardar la reserva",
                            "Message": "Nombre  usuario de la reserva no puede tener mas de 500 caracteres"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                            "Error": "Error al guardar reserva",
                            "Message": "Error inesperado: NullPointerException"
                        }
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<Object> save(
        @Parameter(description = "Detalles de reserva a crear", required = true) @Valid @RequestBody ResDto resDto) {
        try {
            Res savedArticle = resService.save(resDto);
            return new ResponseEntity<>(savedArticle, HttpStatus.CREATED);
        } catch (Exception e) {
            String errorMessage = (e.getCause() != null) ? e.getCause().toString() : e.getMessage();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put(ERROR_KEY, "Error al guardar reserva");
            errorResponse.put(MESSAGE_KEY, errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar reserva",
        description = "Actualiza estado de una reserva existente",
        parameters = {
            @Parameter(
                name = "id",
                description = "ID de la reserva a actualizar",
                required = true,
                example = "1"
            )
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nuevos datos de reserva",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResDto.class),
                examples = @ExampleObject(
                    value = """
                    {
            
                        "id": 1,
                        "name": "Juan",
                        "status": "Confimada",
                        "especialidad": "Odontologia",
                        "creationDate": "05/26/2025"
                    
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Reserva actualizado",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                            "id": 1,
                            "name": "Juan",
                            "status": "Confimada",
                            "especialidad": "Odontologia",
                            "creationDate": "05/26/2025"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Reserva no encontrado",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                            "Error": "Error al actualizar reserva",
                            "Message": "Reserva no encontrado con ID: 99"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                            "Error": "Error al actualizar reserva",
                            "Message": "Error de conexión a la base de datos"
                        }
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<Object> update(
        @Parameter(description = "ID del reserva a actualizar", required = true) @PathVariable("id") Integer id,
        @Parameter(description = "Nuevos detalles de reserva", required = true) @RequestBody ResDto resDto) {
        try {
            return new ResponseEntity<>(resService.update(id, resDto), HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = (e.getCause() != null) ? e.getCause().toString() : e.getMessage();
            logger.error("Error al actualizar reserva con ID {}: {}", id, errorMessage, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put(ERROR_KEY, "Error al actualizar reserva");
            errorResponse.put(MESSAGE_KEY, errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    
}