package edu.eci.cvds.proyect.parcial.persistency.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;


@Data               // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor  // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los campos
@Builder
@Document(collection="res")// Builder pattern

public class Res {
    @Id
    private Integer id;

    @NotBlank(message="El nombre de usuario no puede estar vació")
    @NonNull
    private String name;

    @NotBlank(message="La cedula del usuario no puede estar vacia")
    private String cedula;

    @NotBlank(message="El email del usuario no puede estar vacio")
    private String email;

    @CreatedDate
    private LocalDateTime creationDate;

    @NotBlank(message="La especialidad no puede estar vacia")
    @Pattern(regexp = "Medicina General|Psicología|Ortopedia|Odontologia", message = "El rol del usuario no es valido")
    private String especialidad;

    @NotBlank(message="El doctor que atiende al usuario no puede estar vacio")
    private String nameDoctor;

    @NotBlank(message="ubicacion de la cita no puede estar vacio")
    private String location;

    @Pattern(regexp = "Confirmada|Rechazadaa", message = "estado no valido")
    private String resStatus;



}