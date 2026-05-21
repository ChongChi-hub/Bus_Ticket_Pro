package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.entity.Ticket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.rikkei.busticketpro.repository.TicketRepository;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${emailjs.service-id}")
    private String serviceId;

    @Value("${emailjs.template-id}")
    private String templateId;

    @Value("${emailjs.public-key}")
    private String publicKey;

    @Value("${emailjs.private-key}")
    private String privateKey;

    private static final String EMAILJS_API_URL = "https://api.emailjs.com/api/v1.0/email/send";

    @Autowired
    private TicketRepository ticketRepository;

    @Async
    @Transactional(readOnly = true)
    public void sendPaymentSuccessEmail(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) return;

        if (serviceId == null || serviceId.contains("YOUR_SERVICE_ID")) {
            System.out.println("⚠️ Bỏ qua gửi email vì chưa cấu hình EmailJS keys.");
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo map template parameters
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("ticketCode", ticket.getTicketCode());
        templateParams.put("customerName", ticket.getCustomerName());
        templateParams.put("to_email", ticket.getEmail());
        templateParams.put("fromLocation", ticket.getTrip().getRoute().getFromLocation().getName());
        templateParams.put("toLocation", ticket.getTrip().getRoute().getToLocation().getName());
        templateParams.put("departureTime", ticket.getTrip().getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));
        templateParams.put("seatNumber", ticket.getSeat().getSeatNumber());
        templateParams.put("totalAmount", ticket.getTotalAmount().toString() + " VND");

        // Payload cho EmailJS
        Map<String, Object> payload = new HashMap<>();
        payload.put("service_id", serviceId);
        payload.put("template_id", templateId);
        payload.put("user_id", publicKey);
        payload.put("accessToken", privateKey);
        payload.put("template_params", templateParams);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(EMAILJS_API_URL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Đã gửi email xác nhận thành công qua EmailJS cho vé: " + ticket.getTicketCode());
            } else {
                System.err.println("❌ Lỗi khi gửi email qua EmailJS: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception khi gửi email: " + e.getMessage());
        }
    }
}
