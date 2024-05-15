package app.models;


import com.mysql.cj.jdbc.Blob;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Representa una persona miembro del programa de Ingenieria de Sistemas
 * Su llave principal es una llave compuesta entre el nombre, el perfil y el apellido contenida dentro de la
 * clase PersonId
 */
@Entity
@Table(name = "informacion")
@Data
@NoArgsConstructor
public class Person implements Serializable {

    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @Column(name = "correo", length = 255, unique = true)
    private String correo;

    @EmbeddedId
    private PersonId personId;

    @Column(name = "dia")
    private BigDecimal dia;

    @Column(name = "mes")
    private BigDecimal mes;

    @Column(name = "linkedin")
    private String linkedin;

    @Lob
    @Column(name="foto", length=100000)
    private byte[] foto;

}