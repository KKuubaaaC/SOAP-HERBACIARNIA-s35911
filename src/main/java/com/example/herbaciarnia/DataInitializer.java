package com.example.herbaciarnia;

import com.example.herbaciarnia.model.Tea;
import com.example.herbaciarnia.repo.TeaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);
    private final TeaRepository teaRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (teaRepository.count() > 0) return;

        Tea t1 = Tea.builder()
                .name("Longjing Dragon Well")
                .origin("China")
                .type("GREEN")
                .temperatureCelsius(80)
                .steepingTimeSeconds(120)
                .notes("Delikatna, orzechowa. Nie parz wrzątkiem!")
                .build();

        Tea t2 = Tea.builder()
                .name("Darjeeling First Flush")
                .origin("India")
                .type("BLACK")
                .temperatureCelsius(95)
                .steepingTimeSeconds(240)
                .notes("Muskatowe nuty, parzyć bez mleka.")
                .build();

        Tea t3 = Tea.builder()
                .name("Silver Needle Bai Hao")
                .origin("China")
                .type("WHITE")
                .temperatureCelsius(75)
                .steepingTimeSeconds(180)
                .notes("Bardzo delikatna. Używaj miękkiej wody.")
                .build();

        Tea t4 = Tea.builder()
                .name("Tie Guan Yin")
                .origin("China")
                .type("OOLONG")
                .temperatureCelsius(90)
                .steepingTimeSeconds(150)
                .notes("Kwiatowo-kremowa. Można parzyć wielokrotnie.")
                .build();

        teaRepository.saveAll(Arrays.asList(t1, t2, t3, t4));
        LOG.info("Dane herbat załadowane — {} pozycji.", 4);
    }
}
