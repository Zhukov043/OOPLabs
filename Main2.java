import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.stream.*;
import java.io.FileInputStream;

class Menu {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int choice;
        do{
            System.out.println("1. Ввести название файла");
            System.out.println("2. Завершить работу");
            choice = in.nextInt();
            if (choice == 1) first();

        } while (choice != 2);

    }

    private static void first(){
        Scanner in = new Scanner(System.in);
        System.out.println("Название файла: ");
        String file = in.nextLine();
        HashMap<String,  ArrayList<String>> fileData = new HashMap<>();
        long start = System.nanoTime();
        if (file.lastIndexOf("csv") == file.length() - 3) fileData = ReadFile.readCSV(file);
        else if (file.lastIndexOf("xml") == file.length() - 3) fileData = ReadFile.readXML(file);
        else System.out.println("Некорректный файл!");
        fileData = ProcessingFile.repeat(fileData);
        ProcessingFile.buildings(fileData);
        long end = System.nanoTime();
        long time = end - start;
        System.out.println("Время работы программы: " + time + " мс");
    }
}

class ReadFile {
    public static HashMap<String,  ArrayList<String>> readCSV(String fileCSV){
        String line;
        String delimiter = ";";
        HashMap<String,  ArrayList<String>> data = new HashMap<>();
        try{
            BufferedReader text = new BufferedReader(new FileReader(fileCSV));
            line = text.readLine();
            while((line = text.readLine()) != null){
                String[] columns = line.split(delimiter);
                String city = quotes(columns[0]);
                StringBuilder inf = new StringBuilder();
                for (int i = 1; i < columns.length; i++){
                    inf.append(quotes(columns[i])).append(", ");
                }
                String value = inf.toString();
                value = value.substring(0, inf.length() - 2);
                if (data.containsKey(city)){
                    data.get(city).add(value);
                }
                else{
                    data.put(city, new ArrayList<>());
                    data.get(city).add(value);
                }
            }
            return data;
        }
        catch(IOException e){
            System.out.println("Ошибка открытия файла!");
            return null;
        }
    }

    public static HashMap<String,  ArrayList<String>> readXML(String fileXML){
        HashMap<String, ArrayList<String>> data = new HashMap<>();
        ArrayList<String> keys = new ArrayList<>();
        keys.add("street");
        keys.add("house");
        keys.add("floor");
        try{
            // Создание парсера
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(fileXML));

            // Парсинг XML
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (reader.getLocalName().equalsIgnoreCase("item")) {
                        String city = reader.getAttributeValue(null, "city");
                        StringBuilder inf = new StringBuilder();
                        for (String key : keys) {
                            inf.append(reader.getAttributeValue(null, key)).append(", ");
                        }
                        String value = inf.toString();
                        value = value.substring(0, inf.length() - 2);
                        if (data.containsKey(city)){
                            data.get(city).add(value);
                        }
                        else{
                            data.put(city, new ArrayList<>());
                            data.get(city).add(value);
                        }
                    }
                }
            }
            return data;
        }
        catch(Exception e) {
            System.out.println("Ошибка открытия файла!");
            return null;
        }
    }

    private static String quotes(String str){
        return str.replace("\"", "").replace("'", "");
    }
}

class ProcessingFile {
    public static HashMap<String,  ArrayList<String>> repeat(HashMap<String,  ArrayList<String>> data){
        for (String key : data.keySet()){
            ArrayList<String> value;
            value = data.get(key);
            int reps = 0;
            Set<String> set = new HashSet<>();
            Set<String> rep = new HashSet<>();
            for (String s : value) {
                if (!set.add(s)) {
                    reps += 1;
                    rep.add(s);
                }
            }
            if (reps != 0){
                System.out.println("Повторения в городе " + key + ": " + reps);
                System.out.println(rep);
                System.out.println();
            }
            value.clear();
            value.addAll(set);
            data.put(key, value);
        }
        return data;
    }

    public static void buildings(HashMap<String, ArrayList<String>> data){
        System.out.println("Здания в городах");
        for (String key : data.keySet()){
            ArrayList<String> value;
            value = data.get(key);
            int[] arr = {0, 0, 0, 0, 0};
            for (String str : value) {
                int build = Integer.parseInt(str.substring(str.length() - 1));
                arr[build - 1] = arr[build - 1] + 1;
            }
            System.out.println("Город " + key + ":");
            for (int i = 0; i < arr.length; i++){
                System.out.println((i + 1) + "-этажные дома: " + arr[i]);
            }
            System.out.println();
        }
    }
}