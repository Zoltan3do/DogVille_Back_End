package manu_barone.DogVille.services;

import manu_barone.DogVille.entities.Adozione;
import manu_barone.DogVille.entities.Cane;
import manu_barone.DogVille.entities.Utente;
import manu_barone.DogVille.entities.enums.StatoAdozione;
import manu_barone.DogVille.exceptions.BadRequestException;
import manu_barone.DogVille.exceptions.NotFoundException;
import manu_barone.DogVille.payloads.AdoptionDTO;
import manu_barone.DogVille.repositories.AdozioneRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

@Service
public class AdoptionService {

    @Autowired
    private AdozioneRepo adozioneRepo;

    @Autowired
    private UtenteService us;

    @Autowired
    private CaneService cs;

    public Adozione findById(UUID id) {
        return adozioneRepo.findById(id).orElseThrow(() -> new NotFoundException("Nessun adozione trovata"));
    }

    public Page<Adozione> findAdoptionByUser(String userEmail, Pageable pageable) {
        Utente user = us.findByEmail(userEmail);
        return adozioneRepo.findByUserAdoptions(user, pageable);
    }

    public Adozione createAdoptionRequest(AdoptionDTO body) {
        Cane dog = cs.findById(body.caneId());
        if (adozioneRepo.existsByDog(dog)) {
            throw new BadRequestException("Questo cane ha già una adozione assegnatagli");
        }
        Utente user = us.findByEmail(body.userEmail());
        Adozione adoption = new Adozione(dog, user);
        return adozioneRepo.save(adoption);
    }

    public StatoAdozione getAdoptionStateById(UUID adoptionId) {
        Adozione adozione = adozioneRepo.findById(adoptionId)
                .orElseThrow(() -> new NotFoundException("Richiesta di adozione non trovata con ID: " + adoptionId));
        return adozione.getState();
    }

    public Adozione updateAdoptionState(UUID adoptionId,  String newState) {
        Adozione adoption = this.findById(adoptionId);

        StatoAdozione stateEnum;
        try {
            stateEnum = StatoAdozione.valueOf(newState.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Stato non valido: " + newState + ". I valori accettati sono: "
                    + Arrays.toString(StatoAdozione.values()));
        }

        adoption.setState(stateEnum);
        adoption.setLastUpdate(LocalDate.now());
        return adozioneRepo.save(adoption);
    }

    public void deleteCane(UUID id) {
        Adozione adoption = this.findById(id);
        adozioneRepo.delete(adoption);
    }


}
