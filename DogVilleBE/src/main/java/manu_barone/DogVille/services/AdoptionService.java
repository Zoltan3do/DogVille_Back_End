package manu_barone.DogVille.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import manu_barone.DogVille.entities.Adozione;
import manu_barone.DogVille.entities.Cane;
import manu_barone.DogVille.entities.Utente;
import manu_barone.DogVille.entities.enums.StatoAdozione;
import manu_barone.DogVille.exceptions.BadRequestException;
import manu_barone.DogVille.exceptions.NotFoundException;
import manu_barone.DogVille.exceptions.UnauthorizedException;
import manu_barone.DogVille.payloads.AdoptionDTO;
import manu_barone.DogVille.repositories.AdozioneRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Autowired
    private Cloudinary cloudinaryUploader;

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

    public Adozione updateAdoptionState(UUID adoptionId, String newState) {
        Adozione adoption = this.findById(adoptionId);
        StatoAdozione stateEnum;
        try {
            stateEnum = StatoAdozione.valueOf(newState.toUpperCase());
            if (stateEnum == StatoAdozione.IN_ATTESA_VISITA && adoption.getDocument() != null
                    || stateEnum == StatoAdozione.VISITA_SUPERATA && adoption.getVisitDate() != null
                    || stateEnum == StatoAdozione.ADOZIONE_COMPLETATA && adoption.getState() == StatoAdozione.VISITA_SUPERATA) {
                adoption.setState(stateEnum);
                adoption.setLastUpdate(LocalDate.now());
                return adozioneRepo.save(adoption);
            } else {
                throw new BadRequestException("Questa operazione non è disponibile a questo punto dell'adozione");
            }
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Stato non valido: " + newState + ". I valori accettati sono: "
                    + Arrays.toString(StatoAdozione.values()));
        }
    }

    public void deleteCane(UUID id) {
        Adozione adoption = this.findById(id);
        adozioneRepo.delete(adoption);
    }

    public String uploadDocument(MultipartFile file, UUID idAdozione, @AuthenticationPrincipal Utente currentUtente) {
        String url = null;
        try {
            url = (String) cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
        } catch (IOException e) {
            throw new BadRequestException("Ci sono stati problemi con l'upload del file!");
        }
        Adozione found = this.findById(idAdozione);

        System.out.println(currentUtente.getId());
        System.out.println(found.getUserAdoptions().getId());
        if (!found.getUserAdoptions().getId().equals(currentUtente.getId())) {
            throw new UnauthorizedException("Non hai il permesso per modificare questa adozione!");
        }

        if (found.getState() == StatoAdozione.IN_ATTESA_DOCUMENTI) {
            found.setDocument(url);
            adozioneRepo.save(found);
        } else {
            throw new BadRequestException("Il documento è già stato validato!");
        }
        return url;
    }


}
