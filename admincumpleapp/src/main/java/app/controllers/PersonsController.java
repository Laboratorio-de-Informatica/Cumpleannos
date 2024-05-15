package app.controllers;

import app.models.Person;
import app.models.PersonId;
import app.services.FileServices;
import app.services.PersonServices;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * REST API Controller for Books.
 */
@RestController
@RequestMapping(value = "api/v1/persons")
@CrossOrigin(origins = "*", methods = {RequestMethod.PUT, RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE})
public class PersonsController {

    @Autowired
    private PersonServices services;

    @Autowired
    private FileServices fileservices;

    /**
     * Añade una nueva persona a la aplicación
     */
    @PostMapping()
    public ResponseEntity<Object> addNewPerson(@RequestBody Person newPerson) {
        services.addNewPerson(newPerson);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Obtiene todas las personas de la aplicacion
     */
    @GetMapping()
    public ResponseEntity<?> getPersons() {
        List<Person> persons = services.getAllPersons();
        return new ResponseEntity<>(persons, HttpStatus.ACCEPTED);
    }

    /**
     * Obtiene una persona en base a su ID
     * El id de la persona estará compuesto por el nombre, apellido y el perfil
     */
    @PostMapping(value="/personid")
    public ResponseEntity<?> getPersonById(@RequestBody PersonId personId){

        Person foundPerson = null;
        try {
            foundPerson = services.getPersonById(personId.getNombre(),personId.getApellido(),personId.getPerfil());
            return new ResponseEntity<>(foundPerson, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Elimina una persona de la aplicacion
     */
    @DeleteMapping()
    public ResponseEntity<?> deletePerson(@RequestBody Person personToDelete) {
        Person person = services.deletePerson(personToDelete);
        return new ResponseEntity<>(person, HttpStatus.ACCEPTED);
    }

    /**
     * Elimina todas las personas de la aplicacion
     */
    @DeleteMapping(value = "/all")
    public ResponseEntity<?> deletePersons(@RequestBody List<Person> personsToDelete) {
        try {

            services.deletePersons(personsToDelete);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Modifica la información de una lista de personas de la aplicación
     */
    @PutMapping()
    public ResponseEntity<?> putPersons(@RequestBody List<Person> persons) {
        Person person = null;
        try {
            person = services.updatePerson(persons.get(0).getPersonId().getNombre(), persons.get(0).getPersonId().getApellido(), persons.get(0).getPersonId().getPerfil(), persons.get(1));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(person, HttpStatus.ACCEPTED);
    }

    /**
     * Carga la información de vaerias personas de la aplicación contenidas en un excel
     */
    @PostMapping(value = {"/excel"}, consumes = { "multipart/form-data" })
    public ResponseEntity<?> uploadFile(@RequestParam("excelFile") MultipartFile file) {
        ArrayList<String> logs = new ArrayList<>();
        try {
            fileservices.importPersonsFromExcel(file);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>("Archivo importado correctamente",HttpStatus.ACCEPTED);
    }

    /**
     * Elimina la información de varias personas de la aplicación contenidas dentro de un excel
     */
    @DeleteMapping(value = {"/excel"}, consumes = { "multipart/form-data" })
    public ResponseEntity<?> deleteFile(@RequestParam("excelFile") MultipartFile file) {
        ArrayList<String> logs = new ArrayList<>();
        try {
            fileservices.deletePersonsFromExcel(file);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>("Archivo importado correctamente",HttpStatus.ACCEPTED);
    }

    /**
     * Se encarga de eliminar los logs de la aplicación
     */
    @DeleteMapping(value = {"/logs"})
    public ResponseEntity<?> cleanLogs() {
        try {
            fileservices.cleanLogs();
        } catch (Exception e) {
            return new ResponseEntity<>("Hubo un error al eliminar los logs", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}