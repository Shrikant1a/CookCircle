package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.billing.*;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.BillingAndPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Razorpay Test Mode Checkout, HMAC-SHA256 Verification and Offline Payments")
public class PaymentController {

    private final BillingAndPaymentService billingAndPaymentService;

    public PaymentController(BillingAndPaymentService billingAndPaymentService) {
        this.billingAndPaymentService = billingAndPaymentService;
    }

    @PostMapping("/razorpay/create-order")
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Create a Razorpay Test Mode Order for an invoice")
    public ResponseEntity<ApiResponse<RazorpayOrderResponse>> createRazorpayOrder(
            @Valid @RequestBody CreateRazorpayOrderRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        RazorpayOrderResponse order = billingAndPaymentService.createRazorpayOrder(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Razorpay test order created successfully", order));
    }

    @PostMapping("/razorpay/verify")
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Verify Razorpay HMAC-SHA256 signature and mark invoice as PAID")
    public ResponseEntity<ApiResponse<PaymentDto>> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {
        PaymentDto payment = billingAndPaymentService.verifyRazorpayPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment verified successfully via Razorpay test gateway", payment));
    }

    @PostMapping("/offline")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Record offline cash or manual UPI transaction for an invoice")
    public ResponseEntity<ApiResponse<PaymentDto>> recordOfflinePayment(
            @Valid @RequestBody RecordOfflinePaymentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PaymentDto payment = billingAndPaymentService.recordOfflinePayment(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Offline payment recorded successfully", payment));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'RESIDENT')")
    @Operation(summary = "Get payment history for an invoice")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getInvoicePayments(@PathVariable Long invoiceId) {
        List<PaymentDto> payments = billingAndPaymentService.getInvoicePayments(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
}

