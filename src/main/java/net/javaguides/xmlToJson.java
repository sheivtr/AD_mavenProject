package net.javaguides;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class xmlToJson {

    public static void main(String[] args) {
    try {
    // Cargar el XML desde resources/yo.xml
    InputStream is = xmlToJson.class.getClassLoader().getResourceAsStream("yo.xml");
    if (is == null) {
    System.out.println("No se encontró yo.xml en resources");
    return;
    }
    String xml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
    // Mapper para XML -> JsonNode
    XmlMapper xmlMapper = new XmlMapper();
    JsonNode node = xmlMapper.readTree(xml.getBytes(StandardCharsets.UTF_8));
    // Mapper para JsonNode -> String JSON
    ObjectMapper jsonMapper = new ObjectMapper();
    String json = jsonMapper.writerWithDefaultPrettyPrinter()
    .writeValueAsString(node);
    System.out.println("XML original:\n" + xml);
    System.out.println("\nJSON resultante:\n" + json);
    } catch (IOException e) {
    e.printStackTrace();
    }
    }
    }
