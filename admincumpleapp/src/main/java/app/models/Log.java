package app.models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Representa los Logs de la aplicación
 */
public class Log {

    private Logger logger;
    private FileHandler fh;

    public Log(){
        this.logger = Logger.getLogger("MyLog");
    }

    /**
     * Define un archivo de texto en donde se escribiran los logs
     * Recibe como parametro el nombre del archivo
     * Este archivo será guardado en la ruta : src/main/resources/static/logs/
     */
    public void setFileHandler(String fileName) throws Exception {
        String root = (Paths.get(System.getProperty("user.dir")).toString())+"\\src"+"\\main"+"\\resources"+"\\static"+"\\logs";
        this.fh = null;
        try {
            fh = new FileHandler(root+"\\"+fileName+".txt", true);
            System.out.println(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        this.logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }

    /**
     * Escribe un mensaje en el log
     */
    public void writeLog(String message){
        this.logger.info(message);
    }

    /**
     * Escribe varios mensajes en el log al mismo tiempo
     */
    public void writeLogs(List<String> messages){
        StringBuilder log = new StringBuilder();
        log.append(System.lineSeparator());
        for(String message: messages) {
            log.append(message);
            log.append(System.lineSeparator());
        }
        logger.info(log.toString());
    }

    public void closeFh(){
        this.fh.close();
    }
}
