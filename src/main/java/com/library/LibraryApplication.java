package com.library;

import com.library.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

// Try and find a way to disable the auto configuration based on the conditions.
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
public class LibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Component
    public static class StartupRunner implements CommandLineRunner {
        private final Environment environment;
        private final ApplicationContext applicationContext;
        private final BookService bookService;

        public StartupRunner(Environment environment, ApplicationContext applicationContext, BookService bookService) {
            this.environment = environment;
            this.applicationContext = applicationContext;
            this.bookService = bookService;
        }

        @Override
        public void run(String... args) {
            // Using SpeL try and read properties into a String array which are separated with ‘-’
            bookService.printMyValues();

            // Using @ConfigurationProperties try and read different date and time formats
            bookService.printDateFormats();

            // Using the CommandLineRunner print out all the properties that are in the Environment,
            // and print out all names of beans present in the application context
//            System.out.println("------ All Properties in Environment ------");
//            for (var propertySource : ((org.springframework.core.env.AbstractEnvironment) environment).getPropertySources()) {
//                if (propertySource instanceof org.springframework.core.env.EnumerablePropertySource<?>) {
//                    String[] propertyNames = ((org.springframework.core.env.EnumerablePropertySource<?>) propertySource).getPropertyNames();
//                    for (String name : propertyNames) {
//                        System.out.println(name + "=" + environment.getProperty(name));
//                    }
//                }
//            }

//            System.out.println("\n------ Bean Names in ApplicationContext ------");
//            String[] beanNames = applicationContext.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.print(beanName);
//            }
        }
    }
}
