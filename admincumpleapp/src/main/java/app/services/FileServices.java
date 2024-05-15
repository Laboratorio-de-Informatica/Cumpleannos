package app.services;

import app.models.ImportResult;
import app.models.Log;
import app.models.Person;
import app.repositories.PersonRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Clase que representa los servicios de importacion de personas a partir de informacion contenida en
 * CSV,xlsx o xls.
 * La estructura del archivo debe ser una serie filas conteniendo la siguiente información :
 * carnet,nombre,apellido,dia,mes,correo,perfil,observacion,linkedin,ruta foto
 */
@Service
public class FileServices {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonServices services;

    /**
     * Verifica la extension de un archivo
     * Las extensiones validas son xls,xlsx y csv
     */
    private void checkFileExtension(MultipartFile file) throws Exception {
        boolean validExtension = Arrays.stream(new String[]{"xls","xlsx","csv"}).anyMatch(Objects.requireNonNull(file.getOriginalFilename())::endsWith);
        if (!validExtension){
            throw new Exception("El archivo no tiene la extension correcta. Las extensiones validas son: xlsx y csv");
        }
    }

    /**
     * Importa personas provenientes de un archivo CSV. Además, escribe logs para informar del proceso.
     */
    private void importPersonsFromCsv(MultipartFile file) throws Exception {
        /* Lectura del archivo csv*/
        Log log = new Log();
        log.setFileHandler("file_import");
        ArrayList<String> logs = new ArrayList<>();
        ArrayList<Person> personsToSave = new ArrayList<>();
        Iterable<CSVRecord> csvRecords = null;
        CSVParser csvParser = null;
        try {
            BufferedReader fileReader = new BufferedReader(new
                    InputStreamReader(file.getInputStream(), StandardCharsets.ISO_8859_1));
            csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT);
        } catch (Exception e) {
            throw new Exception("Error en la lectura del archivo csv: "+e.getMessage());
        }
        try {
            /* Lectura de columnas del archivo */
            csvRecords = csvParser.getRecords();
            int intRecord = 1;
            for (CSVRecord record : csvRecords) {
                if(record.size()<9){
                    throw new Exception("El numero de columnas no corresponde al formato: carnet,nombre,apellido,dia,mes,correo,perfil,observacion,linkedin,foto. Recuerde que la foto no es obligatoria.");
                }
                if (intRecord > 1) {
                    logs.add("Log de la linea: "+(intRecord-1));
                    ArrayList<String> values = new ArrayList<>();
                    for (int i = 0; i < record.size(); i++) {
                        values.add(record.get(i));
                    }
                    /* Construcción de persona a partir de la información de la columnas*/
                    ImportResult result = services.buildPersonfromListOfValues(values);
                    if (!(result.getPerson() == null)) {
                        personsToSave.add(result.getPerson());
                    }
                    logs.addAll(result.getLogs());
                }
                intRecord += 1;
            }
            /* Guardar informacion en la base de datos*/
            log.writeLogs(logs);
            services.saveListOfPersons(personsToSave);
            log.closeFh();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Importa personas provenientes de un archivo Excel (xls o xlsx). Ademas, escribe logs para informar del proceso
     */
    public void importPersonsFromExcel(MultipartFile file) throws Exception {
        /* Lectura del archivo excel*/
        Log log = new Log();
        log.setFileHandler("file_import");
        ArrayList<String> logs = new ArrayList<>();
        checkFileExtension(file);
        ArrayList<Person> personsToSave = new ArrayList<>();
        Iterator<Row> rowIterator = null;
        /* Verifica si es csv,xls o xlsx*/
        if (file.getOriginalFilename().endsWith("csv")) {
            importPersonsFromCsv(file);
        }
        else {
            if (file.getOriginalFilename().endsWith("xls")) {
                HSSFSheet sheet = getXlsxWorkSheet(file);
                rowIterator = sheet.iterator();
            }
            else if (file.getOriginalFilename().endsWith("xlsx")) {
                XSSFSheet sheet = getXlsWorkSheet(file);
                rowIterator = sheet.iterator();
            }
            try {
                /* Recorrido atraves de cada una de las filas del archivo*/
                int numColumns = rowIterator.next().getLastCellNum();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    ArrayList<String> values = new ArrayList<String>();
                    for (int i = 0; i < numColumns; i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell == null) {
                            values.add("");
                        } else {
                            CellType type = cell.getCellType();
                            String value = null;
                            if (type == CellType.NUMERIC) {
                                value = NumberToTextConverter.toText(cell.getNumericCellValue());
                            } else {
                                value = cell.toString();
                            }
                            values.add(value);
                        }
                    }
                    /* Construccion de la persona a partir de la informacion de las columnas */
                    logs.add("Mensajes de la linea: " + row.getRowNum());
                    ImportResult result = services.buildPersonfromListOfValues(values);
                    if (!(result.getPerson() == null)) {
                        personsToSave.add(result.getPerson());
                    }
                    logs.addAll(result.getLogs());
                }
                /* Guarda la informacion en la base de datos*/
                log.writeLogs(logs);
                services.saveListOfPersons(personsToSave);
                log.closeFh();
            } catch (Exception e) {
                throw new Exception("Error en el procesamiento del archivo" + e.getMessage());
            }

        }
    }

    /**
     * Elimina personas provenientes de un archivo CSV (xls o xlsx). Ademas, escribe logs para informar del proceso
     */
    private void deletePersonsFromCsv(MultipartFile file) throws Exception {
        /* Lectura del archivo */
        Log log = new Log();
        log.setFileHandler("file_import");
        ArrayList<String> logs = new ArrayList<>();
        ArrayList<Person> personsToSave = new ArrayList<>();
        Iterable<CSVRecord> csvRecords = null;
        CSVParser csvParser = null;
        try {
            BufferedReader fileReader = new BufferedReader(new
                    InputStreamReader(file.getInputStream(), StandardCharsets.ISO_8859_1));
            csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT);
        } catch (Exception e) {
            throw new Exception("Error en la lectura del archivo csv: "+e.getMessage());
        }
        try {
            /* Recorrido atraves de cada una de las filas del archivo*/
            csvRecords = csvParser.getRecords();
            int intRecord = 1;
            for (CSVRecord record : csvRecords) {
                if (intRecord > 1) {
                    logs.add("Log de la linea: "+(intRecord-1));
                    ArrayList<String> values = new ArrayList<>();
                    for (int i = 0; i < record.size(); i++) {
                        values.add(record.get(i));
                    }
                    /* Intentar encontrar persona por id*/
                    Person person = services.getPersonByIdWithoutException(values.get(1),values.get(2),values.get(6));
                    boolean found = true;
                    if(person == null){
                        /* Intentar encontrar persona por correo*/
                        person = services.getPersonByEmail(values.get(5).trim());
                        if(person == null){
                            found = false;
                            logs.add("ERROR AL ELIMINAR: No fue encontrada la persona "+ values.get(1)+ " "+ values.get(2)+ " con el correo "+ values.get(5));
                        }
                    }
                    /* Elimina a la persona si fue encontrada*/
                    if (found) {
                        logs.add("La persona "+ person.getPersonId().getNombre()+" "+person.getPersonId().getApellido()+" fue eliminada correctamente");
                        personsToSave.add(person);
                    }
                }
                intRecord += 1;
            }
            log.writeLogs(logs);
            services.deletePersons(personsToSave);
            log.closeFh();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Elimina personas provenientes de un archivo Excel (xls o xlsx). Ademas, escribe logs para informar del proceso
     */
    public void deletePersonsFromExcel(MultipartFile file) throws Exception {
        /* Lectura del archivo excel */
        Log log = new Log();
        log.setFileHandler("file_import");
        ArrayList<String> logs = new ArrayList<>();
        checkFileExtension(file);
        ArrayList<Person> personsToSave = new ArrayList<>();
        Iterator<Row> rowIterator = null;
        /* Verifica el tipo de extension del archivo*/

        if (file.getOriginalFilename().endsWith("csv")) {
            deletePersonsFromCsv(file);
        }
        else {
            if (file.getOriginalFilename().endsWith("xls")) {
                HSSFSheet sheet = getXlsxWorkSheet(file);
                rowIterator = sheet.iterator();
            }
            else if (file.getOriginalFilename().endsWith("xlsx")) {
                XSSFSheet sheet = getXlsWorkSheet(file);
                rowIterator = sheet.iterator();
            }
            try {
                /* Recorrido por cada una de las columnas */

                int numColumns = rowIterator.next().getLastCellNum();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    ArrayList<String> values = new ArrayList<String>();
                    if(!isRowEmpty(row)) {
                        for (int i = 0; i < numColumns; i++) {
                            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            if (cell == null) {
                                values.add("");
                            } else {
                                CellType type = cell.getCellType();
                                String value = null;
                                if (type == CellType.NUMERIC) {
                                    value = NumberToTextConverter.toText(cell.getNumericCellValue());
                                } else {
                                    value = cell.toString();
                                }
                                values.add(value);
                            }
                        }
                        logs.add("Mensajes de la linea: " + row.getRowNum());
                        Person person = null;
                        boolean found = true;
                        person = services.getPersonByIdWithoutException(values.get(1), values.get(2), values.get(6));
                        /* Intenta encontrar a la persona */
                        if (person == null) {
                            person = services.getPersonByEmail(values.get(5).trim());
                            if (person == null) {
                                found = false;
                                logs.add("ERROR AL ELIMINAR: No fue encontrada la persona "+ values.get(1)+ " "+ values.get(2)+ " con el correo "+ values.get(5));
                            }
                        }
                        /* Si encuentra a la persona la elimina */
                        if (found) {
                            logs.add("La persona " + person.getPersonId().getNombre() + " " + person.getPersonId().getApellido() + " fue eliminada correctamente");
                            personsToSave.add(person);
                        }
                    }
                }
                log.writeLogs(logs);
                services.deletePersons(personsToSave);
                log.closeFh();
            } catch (Exception e) {
                throw new Exception("Error en el procesamiento del archivo " + e.getMessage());
            }

        }
    }

    /**
     * Obtiene una hoja de trabajo para un archivo xls
     */
    private XSSFSheet getXlsWorkSheet(MultipartFile file) throws Exception {
        XSSFWorkbook workbook = null;
        XSSFSheet sheet = null;

        try {
            workbook = new XSSFWorkbook(file.getInputStream());
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            throw new Exception("Ocurrió un error al leer el archivo xlsx" + file.getOriginalFilename()+"."+e.getMessage());
        }
        return sheet;

    }

    /**
     * Obtiene una hoja de trabajo para un archivo xlsx
     */
    private HSSFSheet getXlsxWorkSheet(MultipartFile file) throws Exception {
        HSSFWorkbook workbook = null;
        HSSFSheet sheet = null;

        try {
            workbook = new HSSFWorkbook(file.getInputStream());
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            throw new Exception("Ocurrió un error al leer el archivo xls" + file.getOriginalFilename()+"."+e.getMessage());
        }
        return sheet;

    }

    /**
     * Valida si una fila está vacia
     */
    public static boolean isRowEmpty(Row row) {
        boolean isEmpty = true;
        DataFormatter dataFormatter = new DataFormatter();
        if(row != null) {
            for(Cell cell: row) {
                if(dataFormatter.formatCellValue(cell).trim().length() > 0) {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }

    /**
     * Limpia los logs del archivo file_import.txt
     */
    public void cleanLogs() throws Exception {
        try {
            PrintWriter pw = new PrintWriter(System.getProperty("user.dir")+"\\src"+"\\main"+"\\resources"+"\\static"+"\\logs"+"\\file_import.txt");
            pw.close();
        } catch (Exception e) {
            throw new Exception("Archivo no existente");
        }
    }
}
