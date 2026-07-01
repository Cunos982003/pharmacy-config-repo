package re.edu.pharmacyservice;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private MedicineRepository medicineRepository;
    @Retry(name = "invoiceServiceRetry", fallbackMethod = "invoiceFallback")
    @RateLimiter(name = "invoiceRateLimiter", fallbackMethod = "invoiceFallback")
    public String createInvoice(String invoiceData) {
        try {
            logger.info("Processing invoice data: {}", invoiceData);
            return "Medicine ordered successfully!";
        } catch (Exception e) {
            logger.error("Error creating invoice", e);
            throw new RuntimeException("Error creating invoice", e);
        }
    }

    // Fallback method when retry is exhausted or rate limit is exceeded
    public String invoiceFallback(String invoiceData, Throwable throwable) {
        return "Invoice processing temporarily unavailable. Please try again later.";
    }
}