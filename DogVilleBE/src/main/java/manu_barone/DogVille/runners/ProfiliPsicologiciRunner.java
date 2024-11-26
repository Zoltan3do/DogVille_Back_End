package manu_barone.DogVille.runners;

import manu_barone.DogVille.payloads.CompatibilitaProfiliDTO;
import manu_barone.DogVille.services.ProfiloPsicologicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ProfiliPsicologiciRunner implements CommandLineRunner {

    @Autowired
    private ProfiloPsicologicoService pps;

    @Override
    public void run(String... args) throws Exception {
        List<CompatibilitaProfiliDTO> profiles = new ArrayList<>();
        profiles.add(new CompatibilitaProfiliDTO(
                "Energico", "Attivo, dinamico, richiede molte attività e stimolazione.", Arrays.asList("Energico", "Curioso")));
        profiles.add(new CompatibilitaProfiliDTO(
                "Tranquillo","Calmo, pacato, ama la tranquillità e la routine.", Arrays.asList("Tranquillo", "Indipendente")));
        profiles.add(new CompatibilitaProfiliDTO(
                "Affettuoso","Ama il contatto, la vicinanza e la compagnia costante.", Arrays.asList("Affettuoso", "Protettivo")));
        profiles.add(new CompatibilitaProfiliDTO(
                "Protettivo","Vigile, può essere territoriale, adatto a chi cerca un compagno protettivo.", Arrays.asList("Protettivo", "Affettuoso")));
        profiles.add(new CompatibilitaProfiliDTO(
                "Curioso","Esploratore, intelligente, ha bisogno di stimoli mentali e fisici.", Arrays.asList("Curioso", "Energico")));
        profiles.add(new CompatibilitaProfiliDTO(
                "Indipendente","Autonomo, necessita di poco contatto continuo, tende a fare le proprie cose.", Arrays.asList("Indipendente", "Tranquillo")));


        pps.saveAllProfilesWithCompatibility(profiles);


    }
}
