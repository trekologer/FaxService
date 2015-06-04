package net.trekologer.fax;

import net.trekologer.fax.servlet.FaxServiceServletContextListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Created by adb on 6/2/15.
 */
@SpringBootApplication
public class Application {

    @Bean
    ServletListenerRegistrationBean listener(){
        return new ServletListenerRegistrationBean(
                new FaxServiceServletContextListener()
        );
    }

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
    }
}
