import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.awt.Desktop; ////Для открытия в браузере
import java.io.BufferedReader; //Поток данных
import java.io.InputStreamReader; //Мост между потоками
import java.io.IOException; //Ошибка подключения
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection; //Отправка запроса
import java.net.URI; //Для открытия в браузере
import java.net.URL; //URL объекты
import java.net.URLEncoder; //Encoding
import java.util.Scanner;  //Ввод с консоли

class WikiSearch {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Gson gson = new Gson();

        // Получение запроса
        System.out.println("Введите поисковый запрос:");
        String request = in.nextLine();
        String encodedRequest = encode(request);

        // Отправка запроса
        String link = "https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=" + encodedRequest;
        String response = sendGetRequest(link);

        // Парсинг результатов
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
        JsonObject requestObject = jsonResponse.getAsJsonObject("query");
        JsonArray searchResults = requestObject.getAsJsonArray("search");

        if (searchResults.size() == 0) {
            System.out.println("Результаты не найдены.");
            return;
        }

        // Отображение найденных статей
        System.out.println("Найденные статьи:");
        String[] titles = new String[searchResults.size()];
        String[] pageid = new String[searchResults.size()];
        for (int i = 0; i < searchResults.size(); i++) {
            JsonObject article = searchResults.get(i).getAsJsonObject();
            titles[i] = article.get("title").getAsString();
            pageid[i] = article.get("pageid").getAsString();
            System.out.println((i + 1) + ". " + titles[i]);
        }

        // Выбор статьи
        System.out.println("Выберете статью:");
        int choice = in.nextInt();
        if (choice < 1 || choice > titles.length) {
            System.out.println("Статьи с таким номером не существует.");
            return;
        }

        // Генерация ссылки на статью
        String articleUrl = "https://ru.wikipedia.org/w/index.php?curid=" + pageid[choice - 1];

        // Открытие статьи в браузере
        System.out.println("Открытие статьи: " + titles[choice - 1]);
        openWebpage(articleUrl);
    }

    // Отправка запроса
    private static String sendGetRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        catch (IOException e){
            System.out.println("Ошибка соединения");
            return null;
        }
    }

    // Открытие URL в браузере
    private static void openWebpage(String urlString) {
        try {
            URI uri = new URI(urlString);
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            System.err.println("Не удалось открыть браузер");
        }
    }

    // Encoding
    private static String encode(String str) {
        try {
            String encodedStr = URLEncoder.encode(str, "UTF-8");
            return encodedStr;
        } catch (UnsupportedEncodingException e) {
            System.out.println("Некорректный запрос");
            return null;
        }
    }

}
