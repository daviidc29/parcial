package edu.eci.cvds.proyect.parcial.persistency.dto;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.mongodb.lang.NonNull;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResDto {

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