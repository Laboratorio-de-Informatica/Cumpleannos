package app.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Representa la llave principal de la persona perteneciente al programa de Ing. de Sistemas
 */
@Embeddable
@Data
@NoArgsConstructor
public class PersonId implements Serializable {

    private String nombre;
    private String apellido;
    private String perfil;
}
