package com.rikkei.busticketpro.controller;

import com.rikkei.busticketpro.entity.Ticket;
import com.rikkei.busticketpro.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;

import com.rikkei.busticketpro.service.EmailService;

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PayOS payOS;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private EmailService emailService;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    public String createPayOSPaymentLink(Ticket ticket, String phone) throws Exception {
        String ticketCode = ticket.getTicketCode();
        long orderCode = Long.parseLong(ticketCode.substring(3)); // Lấy phần số sau
        long amount = ticket.getTotalAmount().longValue();

        String dynamicReturnUrl = returnUrl + "?phone=" + phone;
        String dynamicCancelUrl = cancelUrl + "?phone=" + phone;

        PaymentLinkItem item = PaymentLinkItem.builder()
                .name("Vé xe " + ticketCode)
                .price(amount)
                .quantity(1)
                .build();

        CreatePaymentLinkRequest requestData = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description("Ve xe " + ticketCode)
                .returnUrl(dynamicReturnUrl)
                .cancelUrl(dynamicCancelUrl)
                .items(Collections.singletonList(item))
                .build();

        CreatePaymentLinkResponse data = payOS.paymentRequests().create(requestData);
        return data.getCheckoutUrl();
    }

     // Tạo lại URL thanh toán cho vé PENDING
    @GetMapping("/pay-now")
    public String payNow(@RequestParam String ticketCode,
                         @RequestParam String phone,
                         RedirectAttributes redirectAttributes) {
        try {
            Ticket ticket = ticketService.markFindByCode(ticketCode);
            String checkoutUrl = createPayOSPaymentLink(ticket, phone);
            return "redirect:" + checkoutUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tạo liên kết thanh toán: " + e.getMessage());
            return "redirect:/tickets/" + ticketCode + "?phone=" + phone;
        }
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam Map<String, String> params,
                                 @RequestParam(required = false) String phone,
                                 RedirectAttributes redirectAttributes) {
        String orderCodeStr = params.get("orderCode");
        String ticketCode = "BTP" + orderCodeStr;

        try {
            // Verify trên PayOS (gọi API getPaymentLink để chắc chắn đã PAID)
            PaymentLink paymentLinkData = payOS.paymentRequests().get(Long.parseLong(orderCodeStr));
            
            if (PaymentLinkStatus.PAID.equals(paymentLinkData.getStatus())) {
                Ticket ticket = ticketService.markAsPaid(ticketCode);
                // Gửi email xác nhận qua EmailJS (Bất đồng bộ)
                emailService.sendPaymentSuccessEmail(ticket.getId());

                return "redirect:http://localhost:8080/tickets/" + ticketCode + "?phone=" + phone + "&paymentMessage=success";
            } else {
                return "redirect:http://localhost:8080/tickets/" + ticketCode + "?phone=" + phone + "&paymentMessage=failed";
            }
        } catch (Exception e) {
            return "redirect:http://localhost:8080/tickets/" + ticketCode + "?phone=" + phone + "&paymentMessage=error";
        }
    }

    @GetMapping("/cancel")
    public String paymentCancel(@RequestParam Map<String, String> params,
                                @RequestParam(required = false) String phone,
                                RedirectAttributes redirectAttributes) {
        String orderCodeStr = params.get("orderCode");
        String ticketCode = "BTP" + orderCodeStr;

        return "redirect:http://localhost:8080/tickets/" + ticketCode + "?phone=" + phone + "&paymentMessage=cancel";
    }
}
