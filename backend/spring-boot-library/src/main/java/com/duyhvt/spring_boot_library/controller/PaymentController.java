package com.duyhvt.spring_boot_library.controller;

import com.duyhvt.spring_boot_library.request_models.PaymentInfoRequest;
import com.duyhvt.spring_boot_library.service.PaymentService;
import com.duyhvt.spring_boot_library.utils.ExtractJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("https://localhost:3000")
@RestController
@RequestMapping("/api/payment/secure")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentInfoRequest paymentInfo)
            throws StripeException {

        PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfo);
        String paymentString = paymentIntent.toJson();

        return new ResponseEntity<>(paymentString, HttpStatus.OK);
    }

    @PutMapping("/payment-complete")
    public ResponseEntity<String> stripePaymentComplete(@RequestHeader(value = "Authorization") String token)
        throws Exception {

        String userEmail = ExtractJWT.payloadJWTExtraction(token, "sub");
        if (userEmail == null) {
            throw new Exception("User Email is missing");
        }

        return paymentService.stripePayment(userEmail);
    }
}
