package com.cydeo.controller;

import com.cydeo.dto.PaymentDto;
import com.cydeo.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    @Value("${STRIPE_API_SECRET}")
    private String stripeApiKey;
    @Value("${STRIPE_API_SECRET}")
    private String endpointSecret;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostConstruct
    public void init() {
        // Initialize Stripe with your API key
        Stripe.apiKey = stripeApiKey;
    }

    @GetMapping("/list")
    public String listPayments(Model model, 
                               @RequestParam(name = "year", 
                                       defaultValue = "#{T(java.time.Year).now().getValue()}", 
                                       required = false) Integer year){
        
        model.addAttribute("payments", paymentService.listPaymentsByYear(year));
        model.addAttribute("selectedYear", year);
        return "payment/list";
    }
    
    @GetMapping("/pay/{id}")
    public String initiateCheckout(@PathVariable("id") Long paymentId, 
                              HttpServletRequest request) throws StripeException {
    // Get payment details
    PaymentDto payment = paymentService.getPaymentById(paymentId);
    
    if (payment.isPaid()) {
        // Payment already processed
        return "redirect:/payments/list?error=Payment already processed";
    }
    
    // Get base URL dynamically
    String baseUrl = request.getScheme() + "://" + request.getServerName();
    if (request.getServerPort() != 80 && request.getServerPort() != 443) {
        baseUrl += ":" + request.getServerPort();
    }
    // Create checkout session parameters
    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setSuccessUrl(baseUrl + "/payments/success?session_id={CHECKOUT_SESSION_ID}")
        .setCancelUrl(baseUrl + "/payments/cancel")
        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
        .addLineItem(
            SessionCreateParams.LineItem.builder()
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("usd")
                        .setUnitAmount(payment.getAmount().multiply(new java.math.BigDecimal(100)).longValue())
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName("Payment for " + payment.getMonth().getValue())
                                .setDescription("Payment for " + payment.getYear() + " " + payment.getMonth().getValue())
                                .build()
                        )
                        .build()
                )
                .setQuantity(1L)
                .build()
        )
        .putMetadata("payment_id", paymentId.toString())
        .build();
    
    // Create the checkout session
    Session session = Session.create(params);
    
    // Redirect to Stripe's checkout page
    return "redirect:" + session.getUrl();
}
    
    // Handle successful payment
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam(value = "session_id", required = false) String sessionId, Model model) {
        // If no session ID is provided, show generic success page
        if (sessionId == null || sessionId.isEmpty()) {
            model.addAttribute("message", "Payment successful!");
            return "payment/success";
        }
        
        try {
            // Verify the session
            Session session = Session.retrieve(sessionId);
            
            // Check payment status (optional extra verification)
            if ("paid".equals(session.getPaymentStatus())) {
                String paymentId = session.getMetadata().get("payment_id");
                if (paymentId != null) {
                    paymentService.markAsPaid(Long.valueOf(paymentId));
                    model.addAttribute("message", "Payment was successfully processed and recorded!");
                } else {
                    model.addAttribute("message", "Payment was successful, but we couldn't find the associated payment record.");
                }
            } else {
                model.addAttribute("message", "Payment was received but is still being processed.");
            }
        } catch (StripeException e) {
            // Log the error for debugging
            System.err.println("Stripe error: " + e.getMessage());
            model.addAttribute("error", "Could not verify payment: " + e.getMessage());
        }
        
        return "payment/success";
    }
    
    // Handle cancelled payment
    @GetMapping("/cancel")
    public String paymentCancelled(Model model) {
        model.addAttribute("message", "Payment was cancelled.");
        return "payment/cancel";
    }
    
    // Webhook endpoint for Stripe events
    @PostMapping("/webhook")
    @ResponseBody
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                     @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Verify webhook signature
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            
            // Handle the checkout.session.completed event
            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().get();
                
                // Extract payment ID from metadata and update payment status
                String paymentId = session.getMetadata().get("payment_id");
                if (paymentId != null) {
                    paymentService.markAsPaid(Long.valueOf(paymentId));
                }
            }
            
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing webhook: " + e.getMessage());
        }
    }
    
    // Handle invoice generation for paid payments
    @GetMapping("/invoice/{id}")
    public String generateInvoice(@PathVariable("id") Long paymentId, Model model) {
        PaymentDto payment = paymentService.getPaymentById(paymentId);
        
        if (!payment.isPaid()) {
            return "redirect:/payments/list?error=Payment not yet processed";
        }
        
        model.addAttribute("payment", payment);
        return "payment/invoice";
    }
}