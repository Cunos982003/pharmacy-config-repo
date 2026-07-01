package re.edu.pharmacyservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/invoice")
@RefreshScope
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public InvoiceController(InvoiceService invoiceService, KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.invoiceService = invoiceService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${app.branch-name}")
    private String branchName;

    @PostMapping("/create")
    public Map<String, Object> createInvoice(@RequestBody Map<String, Object> request) {
        String invoiceData = (String) request.getOrDefault("invoiceData", "");

        String result = invoiceService.createInvoice(invoiceData);

        // Extract order information from invoiceData (this is a simplified example)
        // In a real implementation, you would parse the invoiceData to extract the necessary fields
        String orderId = (String) request.getOrDefault("orderId", "");
        String medicineId = (String) request.getOrDefault("medicineId", "");
        int quantity = ((Number) request.getOrDefault("quantity", 0)).intValue();

        // Create OrderEvent and send to Kafka
        if (!orderId.isEmpty() && !medicineId.isEmpty() && quantity > 0) {
            OrderEvent orderEvent = new OrderEvent(orderId, medicineId, quantity, Instant.now().toEpochMilli());
            kafkaTemplate.send("medicine-stock-events", medicineId, orderEvent);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("branchName", branchName);
        response.put("result", result);
        response.put("timestamp", System.currentTimeMillis());

        return response;
    }
}