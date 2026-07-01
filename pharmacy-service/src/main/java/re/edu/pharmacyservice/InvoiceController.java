package re.edu.pharmacyservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/invoice")
@RefreshScope
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Value("${app.branch-name}")
    private String branchName;

    @PostMapping("/create")
    public Map<String, Object> createInvoice(@RequestBody Map<String, Object> request) {
        String invoiceData = (String) request.getOrDefault("invoiceData", "");

        String result = invoiceService.createInvoice(invoiceData);

        Map<String, Object> response = new HashMap<>();
        response.put("branchName", branchName);
        response.put("result", result);
        response.put("timestamp", System.currentTimeMillis());

        return response;
    }
}