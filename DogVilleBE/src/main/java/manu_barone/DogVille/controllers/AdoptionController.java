package manu_barone.DogVille.controllers;

import manu_barone.DogVille.entities.Adozione;
import manu_barone.DogVille.entities.Cane;
import manu_barone.DogVille.entities.Utente;
import manu_barone.DogVille.entities.enums.StatoAdozione;
import manu_barone.DogVille.exceptions.BadRequestException;
import manu_barone.DogVille.payloads.AdoptionDTO;
import manu_barone.DogVille.payloads.CaneDTO;
import manu_barone.DogVille.payloads.validationGroups.Create;
import manu_barone.DogVille.payloads.validationGroups.Update;
import manu_barone.DogVille.services.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/adozioni")
public class AdoptionController {

    @Autowired
    private AdoptionService as;

    @GetMapping("/{adozioneId}")
    public Adozione findAdozioneById(@PathVariable UUID adozioneId) {
        return as.findById(adozioneId);
    }

    @GetMapping("/{id}/state")
    public StatoAdozione getAdoptionState(@PathVariable UUID id) {
        return as.getAdoptionStateById(id);
    }

    @GetMapping("/user")
    public Page<Adozione> getAdoptionByUser(@RequestParam String email, Pageable pageable) {
        return as.findAdoptionByUser(email, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Adozione createAdozione(@RequestBody @Validated(Create.class) AdoptionDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.joining(". "));
            throw new BadRequestException("Payload error: " + message);
        }
        return as.createAdoptionRequest(body);
    }

    @PatchMapping("/{adozioneId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Adozione updateAdoption(@PathVariable UUID adozioneId, @RequestParam String statoAdozione) {
        return as.updateAdoptionState(adozioneId, statoAdozione);
    }

    @DeleteMapping("/{adozioneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCane(@PathVariable UUID adozioneId){
        as.deleteCane(adozioneId);
    }

    @PatchMapping("/{adoptionId}/document")
    public String addAvatar(@PathVariable("adoptionId") UUID adoptionId, @RequestParam("document") MultipartFile file, @AuthenticationPrincipal Utente currentUtente) {
        return as.uploadDocument(file, adoptionId, currentUtente);
    }


}
