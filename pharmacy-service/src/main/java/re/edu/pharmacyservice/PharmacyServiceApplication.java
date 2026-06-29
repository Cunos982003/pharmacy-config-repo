package re.edu.pharmacyservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PharmacyServiceApplication implements CommandLineRunner {

    @Value("${app.branch-name}")
    private String branchName;

    @Value("${app.hotline}")
    private String hotline;

    public static void main(String[] args) {
        SpringApplication.run(PharmacyServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Branch Name: " + branchName);
        System.out.println("Hotline: " + hotline);
    }
}