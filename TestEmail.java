import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class TestEmail {
    public static void main(String[] args) throws Exception {
        String urlString = "https://api.emailjs.com/api/v1.0/email/send";
        String payload = "{\"service_id\":\"service_3yke50g\",\"template_id\":\"template_mcutrc8\",\"user_id\":\"mhVhUeI-C6HkEfVNy\",\"accessToken\":\"lhAfrXJWJpwyWGhFxSHnY\",\"template_params\":{\"to_email\":\"tri21092006@gmail.com\",\"customerName\":\"Test User\",\"ticketCode\":\"123\",\"fromLocation\":\"A\",\"toLocation\":\"B\",\"departureTime\":\"10:00\",\"seatNumber\":\"1\",\"totalAmount\":\"1000\"}}";
        
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);			
        }
        
        int code = conn.getResponseCode();
        System.out.println("Response Code: " + code);
        
        InputStream is = (code < 400) ? conn.getInputStream() : conn.getErrorStream();
        if (is != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println("Response Body: " + response.toString());
        }
    }
}
