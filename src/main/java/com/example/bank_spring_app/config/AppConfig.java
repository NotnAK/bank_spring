package com.example.bank_spring_app.config;

import com.example.bank_spring_app.dto.UserDTO;
import com.example.bank_spring_app.entity.Currency;
import com.example.bank_spring_app.entity.RateOfExchange;
import com.example.bank_spring_app.entity.UserRole;
import com.example.bank_spring_app.repository.RateOfExchangeRepository;
import com.example.bank_spring_app.service.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Bean
    public CommandLineRunner demo(final RateOfExchangeRepository rateOfExchangeRepository, final BankService bankService, final PasswordEncoder encoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                rateOfExchangeRepository.save(new RateOfExchange(Currency.USD, 38.5));
                rateOfExchangeRepository.save(new RateOfExchange(Currency.EUR, 42.0));
                rateOfExchangeRepository.save(new RateOfExchange(Currency.UAH, 1.0));
                UserDTO userDTO = new UserDTO("admin", encoder.encode("123"), UserRole.ADMIN, "Admin", null);
                bankService.addUser(userDTO);
                UserDTO userDTO2 = new UserDTO("user", encoder.encode("123"), UserRole.USER, "user", null);
                bankService.addUser(userDTO2);
            }
        };
    }
}
