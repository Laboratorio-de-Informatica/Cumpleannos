package app.services;

import app.models.ImportResult;
import app.models.Person;
import app.models.PersonId;
import app.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Clase que representa un servicio que permitirá construir y manejar la informacion de una persona
 * perteneciente al programa de Ing. de Sistemas
 */
@Service
public class PersonServices {

    @Autowired
    private PersonRepository personRepository;

    /**
     * Construye un objeto de tipo Persona apartir de una lista de valores
     * La lista estará compuesta por [carnet,nombre,apellido,dia,mes,correo,perfil,observacion,linkedin, ruta foto]
     */
    public ImportResult buildPersonfromListOfValues(List<String> values) throws Exception {
        Person person = null;
        ArrayList<String> logs = new ArrayList<String>();
        ImportResult result = new ImportResult();
        try {
            PersonId personId = new PersonId();
            buildName(values.get(1),personId);
            buildApellido(values.get(2),personId);
            buildPerfil(values.get(6),personId);
            person = new Person();
            person.setPersonId(personId);
            person.setId(values.get(0));
            buildDiaAndMes(values.get(3),values.get(4), person);
            buildCorreo(values.get(5),person);
            person.setObservacion(values.get(7));
            person.setLinkedin(values.get(8));
            try {
                String foto = values.get(9);
                buildFoto(foto, person, logs);
            } catch (Exception e) {}
        } catch (Exception e) {
            logs.add("ERROR: La persona NO FUE AGREGADA debido a que "+ e.getMessage());
            result.setLogs(logs);
            result.setPerson(null);
            return result;
        }
        logs.add("La persona "+person.getPersonId().getNombre()+" "+person.getPersonId().getApellido()+" fue agregada correctamente");
        result.setLogs(logs);
        result.setPerson(person);
        return result;
    }

    /**
     * Obtiene todas las personas
     */
    public List<Person> getAllPersons(){
        return personRepository.findAll();
    }

    /**
     * Actualiza la informacion de una persona.
     * Para eso recibe el nombre, apllellido y perfil de la persona a actualizar. Además, recibe la nueva informacion
     * contenida en un objeto de tipo Persona
     */
    public Person updatePerson(String oldNombre, String oldApellido, String oldPerfil, Person newPerson) throws Exception {
        Person oldPerson = getPersonById(oldNombre,oldApellido,oldPerfil);
        personRepository.delete(oldPerson);
        return personRepository.save(newPerson);
    }

    /**
     * Obtiene una persona de acuerdo a su PersonId
     */
    public Person getPersonById(String nombre, String apellido, String perfil) throws Exception {
        Person foundPerson = null;
        try {
            foundPerson = personRepository.getPersonById(nombre, apellido, perfil).get(0);
        } catch (Exception e) {
            throw new Exception("Persona no encontrada");
        }

        return foundPerson;
    }

    /**
     * Obtiene una persona de acuerdo a su PersonId pero no lanza excepcion si no la encuentra
     */
    public Person getPersonByIdWithoutException(String nombre, String apellido, String perfil) throws Exception {
        Person foundPerson = null;
        try {
            foundPerson = personRepository.getPersonById(nombre, apellido, perfil).get(0);
        } catch (Exception e) {
            return null;
        }

        return foundPerson;
    }

    /**
     * Elimina una persona recibida como parametro
     */
    public Person deletePerson(Person personToDelete) {
        personRepository.delete(personToDelete);
        return personToDelete;
    }

    /**
     * Guarda una serie de personas recibidas como parametro
     */
    public void saveListOfPersons(ArrayList<Person> personsToSave) throws Exception {
        try {
            personRepository.saveAll(personsToSave);
        } catch (Exception e) {
            throw new Exception("Error al guardar las personas "+e.getMessage());
        }
    }

    /**
     * Elimina una serie de personas recibida como parametro
     */
    public void deletePersons(List<Person> personsToDelete) {
        personRepository.deleteAll(personsToDelete);
    }

    /**
     * Elimina una persona de acuerdo a su email
     */
    public Person getPersonByEmail(String s) {
        Person person = null;
        try{
            person = personRepository.getPersonByEmail(s).get(0);
        } catch (Exception e) {
            return null;
        }
        return person;
    }

    /**
     * Añade una persona
     */
    public void addNewPerson(Person newPerson){
        personRepository.save(newPerson);
    }

    private void buildName(String nombre, PersonId personId) throws Exception {
        if(Objects.equals(nombre, "")){
            throw new Exception("El nombre no puede ser nulo");
        }
        personId.setNombre(nombre);
    }

    private void buildApellido(String apellido, PersonId personId) throws Exception {
        if(Objects.equals(apellido, "")){
            throw new Exception("El apellido no puede ser nulo");
        }
        personId.setApellido(apellido);
    }

    private void buildPerfil(String perfil, PersonId personId) throws Exception {
        if(Objects.equals(perfil, "")){
            throw new Exception("El perfil no puede ser nulo");
        }
        personId.setPerfil(perfil);
    }

    private void buildCorreo(String correo, Person person) throws Exception {
        if(Objects.equals(correo, "")){
            throw new Exception("El correo no puede ser nulo");
        }
        person.setCorreo(correo);
    }

    private void buildFoto(String fotoPath, Person person, List<String> logs) throws Exception {
        String root = Paths.get(System.getProperty("user.dir")).getParent().toString();
        String fotoDir = root + "\\" + (fotoPath.replace("/", "\\"));
        try {
            byte[] fotoBytes = Files.readAllBytes(Paths.get(fotoDir));
            person.setFoto(fotoBytes);
        } catch (IOException e) {
            logs.add("Foto no encontrada en el directorio: " + fotoDir);
        }
    }

    private void buildDiaAndMes(String dia, String mes, Person person) throws Exception {
        try {
            person.setDia(new BigDecimal(dia));
            person.setMes(new BigDecimal(mes));
        } catch (Exception e) {
            throw new Exception("El dia y el mes DEBEN SER NUMEROS ENTEROS");
        }

    }
}
