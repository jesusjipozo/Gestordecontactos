package org.example;

import com.opencsv.CSVReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class GestorDeContactos {

    // Tarea 1: Gestor de Contactos
    private static final String CONTACT_FILE = "contactos.txt";

    public void añadirContacto(String nombre, String telefono, String email) throws IOException {
        Contacto contacto = new Contacto(nombre, telefono, email);
        try (FileWriter fw = new FileWriter(CONTACT_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(contacto.toString());
            bw.newLine();
        }
    }

    public void listarContactos() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(CONTACT_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }
        }
    }

    public void eliminarContacto(String nombre) throws IOException {
        List<Contacto> contactos = cargarContactos();
        contactos.removeIf(c -> c.getNombre().equalsIgnoreCase(nombre));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CONTACT_FILE))) {
            for (Contacto contacto : contactos) {
                bw.write(contacto.toString());
                bw.newLine();
            }
        }
    }

    private List<Contacto> cargarContactos() throws IOException {
        List<Contacto> contactos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CONTACT_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(", ");
                if (datos.length == 3) {
                    contactos.add(new Contacto(datos[0], datos[1], datos[2]));
                }
            }
        }
        return contactos;
    }

    // Tarea 2: Conversor CSV a XML
    public void convertirCSVtoXML(String csvFilePath, String xmlFilePath) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] headers = reader.readNext();
            if (headers == null) {
                throw new IOException("El archivo CSV está vacío o tiene un formato incorrecto.");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("Estudiantes");
            doc.appendChild(root);

            String[] row;
            while ((row = reader.readNext()) != null) {
                Element estudiante = doc.createElement("Estudiante");
                for (int i = 0; i < headers.length; i++) {
                    Element elem = doc.createElement(headers[i]);
                    elem.appendChild(doc.createTextNode(row[i]));
                    estudiante.appendChild(elem);
                }
                root.appendChild(estudiante);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new FileWriter(xmlFilePath)));

            System.out.println("Conversión completada: " + xmlFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tarea 3: Analizador de Logs
    public void analizarLogs(String logFilePath, String reportFilePath) {
        Map<String, Integer> logCounts = new HashMap<>();
        Map<String, Integer> errorMessages = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    String level = parts[1];
                    String message = line.substring(line.indexOf(" ", line.indexOf(" ") + 1)).trim();

                    logCounts.put(level, logCounts.getOrDefault(level, 0) + 1);

                    if (level.equals("ERROR")) {
                        errorMessages.put(message, errorMessages.getOrDefault(message, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(reportFilePath))) {
            bw.write("Log Levels:\n");
            for (Map.Entry<String, Integer> entry : logCounts.entrySet()) {
                bw.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }

            bw.write("\nTop 5 Error Messages:\n");
            errorMessages.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> {
                        try {
                            bw.write(entry.getKey() + ": " + entry.getValue() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            System.out.println("Análisis completado: " + reportFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase interna Contacto para la Tarea 1
    private static class Contacto {
        private String nombre;
        private String telefono;
        private String email;

        public Contacto(String nombre, String telefono, String email) {
            this.nombre = nombre;
            this.telefono = telefono;
            this.email = email;
        }

        public String getNombre() {
            return nombre;
        }

        @Override
        public String toString() {
            return nombre + ", " + telefono + ", " + email;
        }
    }

    public static void main(String[] args) {
        GestorDeContactos app = new GestorDeContactos();

        // Ejemplos de uso

        // Tarea 1: Añadir y listar contactos
        try {
            app.añadirContacto("Juan Perez", "123456789", "juan@ejemplo.com");
            app.añadirContacto("Ana Garcia", "987654321", "ana@ejemplo.com");
            System.out.println("Lista de contactos:");
            app.listarContactos();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Tarea 2: Convertir CSV a XML
        app.convertirCSVtoXML("estudiantes.csv", "estudiantes.xml");

        // Tarea 3: Analizar logs
        app.analizarLogs("archivo.log", "informe_log.txt");
    }
}
