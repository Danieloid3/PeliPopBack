package bd.pelipop.Config;

import bd.pelipop.Models.Country;
import bd.pelipop.Repositories.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    private final CountryRepository countryRepository;

    public DataSeeder(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (countryRepository.count() == 0) {
            logger.info("La tabla 'countries' está vacía. Poblando con datos iniciales...");

            List<String> countryNames = Arrays.asList(
                    "Argentina", "Bolivia", "Brasil", "Chile", "Colombia", "Costa Rica",
                    "Cuba", "Ecuador", "El Salvador", "España", "Estados Unidos", "Guatemala",
                    "Honduras", "México", "Nicaragua", "Panamá", "Paraguay", "Perú",
                    "Puerto Rico", "República Dominicana", "Uruguay", "Venezuela"
            );

            List<Country> countries = countryNames.stream().map(name -> {
                Country country = new Country();
                country.setName(name);
                return country;
            }).collect(Collectors.toList());

            countryRepository.saveAll(countries);
            logger.info("Se han insertado {} países en la base de datos.", countries.size());
        } else {
            logger.info("La tabla 'countries' ya contiene datos. No se requiere acción.");
        }
    }
}
