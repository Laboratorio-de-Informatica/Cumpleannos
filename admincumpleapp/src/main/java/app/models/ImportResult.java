package app.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.List;

/**
 * Representa el resultado de haber intentado importar la informacion de una persona
 */
@Embeddable
@Data
@NoArgsConstructor
public class ImportResult implements Serializable {

    private Person person;
    private List<String> logs;
}
