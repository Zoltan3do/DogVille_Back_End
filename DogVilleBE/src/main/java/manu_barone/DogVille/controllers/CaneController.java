package manu_barone.DogVille.controllers;

import manu_barone.DogVille.entities.Cane;
import manu_barone.DogVille.exceptions.BadRequestException;
import manu_barone.DogVille.payloads.CaneDTO;
import manu_barone.DogVille.payloads.validationGroups.Create;
import manu_barone.DogVille.payloads.validationGroups.Update;
import manu_barone.DogVille.services.CaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cani")
public class CaneController {

    @Autowired
    private CaneService cs;

    @GetMapping("/filter")
    public Page<Cane> getCani(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "id") String sortBy,
                              @RequestParam(required = false) Integer age,
                              @RequestParam(required = false) String weanedCheck,
                              @RequestParam(required = false) String race,
                              @RequestParam(required = false) String healthState,
                              @RequestParam(required = false) Character gender,
                              @RequestParam(required = false) String dogSize,
                              @RequestParam(required = false) String adoptedCheck

    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return cs.findWithFilters(adoptedCheck, age, weanedCheck, race, healthState, gender, dogSize, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cane saveDog(@RequestBody @Validated(Create.class) CaneDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.joining(". "));
            throw new BadRequestException("Payload error: " + message);
        }
        return cs.addCane(body);
    }

    @GetMapping("/{caneId}")
    public Cane findCaneById(@PathVariable UUID caneId) {
        return cs.findById(caneId);
    }

    @GetMapping("/ordered-by-likes")
    public ResponseEntity<List<Cane>> getAllDogsOrderedByLikes() {
        List<Cane> caniOrdinati = cs.getAllCaniOrderedByLikes();
        return ResponseEntity.ok(caniOrdinati);
    }

    @PutMapping("/{caneId}")
    public Cane updateCane(@PathVariable UUID caneId, @RequestBody @Validated(Update.class) CaneDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).collect(Collectors.joining(". "));
            throw new BadRequestException("Ci sono stati errori nel payload! " + message);
        }
        return cs.updateCane(caneId, body);
    }

    @DeleteMapping("/{caneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCane(@PathVariable UUID caneId){
        cs.deleteCane(caneId);
    }

    @PatchMapping("/{caneId}/avatar")
    public String addAvatar(@PathVariable("caneId") UUID caneId, @RequestParam("avatar") MultipartFile file) {
        return cs.uploadPhoto(file, caneId);
    }


}
