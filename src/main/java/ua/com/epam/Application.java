package ua.com.epam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        try {
            DataIngestion.main(args);
        } catch(Exception e) {
            System.err.println("Failed generating ingestion data");
            e.printStackTrace();
        }
        SpringApplication.run(Application.class, args);
    }
}
