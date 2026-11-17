package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.billing.GenerateInvoicesRequest;
import com.cookcircle.dto.billing.InvoiceDto;
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
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoices & Billing", description = "Monthly Society Cooking Service Billing and Invoicing")
public class InvoiceController {

    private final BillingAndPaymentService billingAndPaymentService;

    public InvoiceController(BillingAndPaymentService billingAndPaymentService) {
        this.billingAndPaymentService = billingAndPaymentService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Generate monthly invoices for all occupied flats in a society")
    public ResponseEntity<ApiResponse<List<InvoiceDto>>> generateInvoices(
            @RequestParam(required = false) Long societyId,
            @Valid @RequestBody GenerateInvoicesRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long targetSocietyId = societyId;
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            targetSocietyId = currentUser.getSocietyId();
        } else if (targetSocietyId == null) {
            targetSocietyId = currentUser.getSocietyId() != null ? currentUser.getSocietyId() : 1L;
        }

        List<InvoiceDto> generated = billingAndPaymentService.generateMonthlyInvoices(targetSocietyId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Monthly invoices generated successfully", generated));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN')")
    @Operation(summary = "Get invoices for a society (filter by billingMonth, status)")
    public ResponseEntity<ApiResponse<List<InvoiceDto>>> getSocietyInvoices(
            @RequestParam(required = false) Long societyId,
            @RequestParam(required = false) String billingMonth,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long targetSocietyId = societyId;
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            targetSocietyId = currentUser.getSocietyId();
        } else if (targetSocietyId == null) {
            targetSocietyId = currentUser.getSocietyId() != null ? currentUser.getSocietyId() : 1L;
        }

        List<InvoiceDto> list = billingAndPaymentService.getSocietyInvoices(targetSocietyId, billingMonth, status);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RESIDENT')")
    @Operation(summary = "Get monthly invoices for the logged-in resident")
    public ResponseEntity<ApiResponse<List<InvoiceDto>>> getMyInvoices(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<InvoiceDto> list = billingAndPaymentService.getResidentInvoices(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SOCIETY_ADMIN', 'RESIDENT')")
    @Operation(summary = "Get invoice details by ID")
    public ResponseEntity<ApiResponse<InvoiceDto>> getInvoiceById(@PathVariable Long id) {
        InvoiceDto dto = billingAndPaymentService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}

