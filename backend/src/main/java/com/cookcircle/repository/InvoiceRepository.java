package com.cookcircle.repository;

import com.cookcircle.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByResidentId(Long residentId);
    List<Invoice> findByApartmentId(Long apartmentId);
    List<Invoice> findBySocietyId(Long societyId);
    List<Invoice> findBySocietyIdAndBillingMonth(Long societyId, String billingMonth);
    Optional<Invoice> findByApartmentIdAndBillingMonth(Long apartmentId, String billingMonth);
    boolean existsByApartmentIdAndBillingMonth(Long apartmentId, String billingMonth);
}

