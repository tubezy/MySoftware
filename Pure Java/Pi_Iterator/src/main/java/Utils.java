import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Utils {

    String url = "http://3.141592653589793238462643383279502884197169399375105820974944592.com/";
    public static int iteration = 562566;

    public boolean checkIndex(double index) throws IOException, InterruptedException {
        boolean found = false;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "index" + index + ".html"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.body().length() > 1000000) {
            found = true;
        }
        System.out.println("\n");
        System.out.println(response.body().substring(response.body().indexOf("...") + 4, response.body().indexOf("</pre>")).trim());

        return found;
    }

}
