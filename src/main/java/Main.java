

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileName2 = "data.xml";
        String csvToJson = "data.json";
        String xmlToJson = "data2.json";

        // конвертация из csv в json
        List<Employee> list = parserCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, csvToJson);

        // конвертация из xml в json
        List<Employee> list1 = parserXML(fileName2);
        String json2 = listToJson(list1);
        writeString(json2, xmlToJson);

        String jsonText = readString(xmlToJson);
        //System.out.println(jsonText);

        // преобразование json в объект
        List<Employee> list2 = jsonToList(jsonText);

        for (Employee e : list2) {
            System.out.println(e);
        }
    }

    // для задачи 1
    public static List<Employee> parserCSV(String[] columnMapping, String fileName) {
        List<Employee> employeeList = new ArrayList<>();

        try(CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> employeeCsv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            employeeList = employeeCsv.parse();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return employeeList;
    }

    public static String listToJson (List<Employee> list) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>(){}.getType();

        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {

        try(FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            writer.flush();
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

     //для задачи 2
    public static List<Employee> parserXML(String fileName) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc  = builder.parse(new File(fileName));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("employee");

            for (int i = 0 ; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element employee = (Element) node;
                    NodeList nodeListElement1 = employee.getElementsByTagName("id");
                    Element element1 = (Element) nodeListElement1.item(0);
                    NodeList nodeList1 = element1.getChildNodes();
                    String id = nodeList1.item(0).getNodeValue();
                    //System.out.println("id: " + id);

                    NodeList nodeListElement2 = employee.getElementsByTagName("firstName");
                    Element element2 = (Element) nodeListElement2.item(0);
                    NodeList nodeList2 = element2.getChildNodes();
                    String firstName = nodeList2.item(0).getNodeValue();
                    //System.out.println("firstName: " + firstName);

                    NodeList nodeListElement3 = employee.getElementsByTagName("lastName");
                    Element element3 = (Element) nodeListElement3.item(0);
                    NodeList nodeList3 = element3.getChildNodes();
                    String lastName = nodeList3.item(0).getNodeValue();
                    //System.out.println("lastName: " + lastName);

                    NodeList nodeListElement4 = employee.getElementsByTagName("country");
                    Element element4 = (Element) nodeListElement4.item(0);
                    NodeList nodeList4 = element4.getChildNodes();
                    String country = nodeList4.item(0).getNodeValue();
                    //System.out.println("country: " + country);

                    NodeList nodeListElement5 = employee.getElementsByTagName("age");
                    Element element5 = (Element) nodeListElement5.item(0);
                    NodeList nodeList5 = element5.getChildNodes();
                    String age = nodeList5.item(0).getNodeValue();
                    //System.out.println("age: " + age);

                    Employee employee1 = new Employee
                            (Long.parseLong(id), firstName, lastName, country, Integer.parseInt(age));
                    employeeList.add(employee1);

                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return employeeList;
    }

    // для задачи 3
    public static String readString(String jsonData) {
        StringBuilder jsonText = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(jsonData))) {
            String s;
            while ((s = br.readLine()) != null) {
                jsonText.append(s);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return jsonText.toString();
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> employees = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            for (Object obj : jsonArray) {
                employees.add(gson.fromJson(String.valueOf(obj), Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return employees;
    }
}
