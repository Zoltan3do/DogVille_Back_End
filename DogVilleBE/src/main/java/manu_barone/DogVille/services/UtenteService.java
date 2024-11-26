package manu_barone.DogVille.services;

import manu_barone.DogVille.entities.Utente;
import manu_barone.DogVille.entities.enums.Ruolo;
import manu_barone.DogVille.exceptions.BadRequestException;
import manu_barone.DogVille.exceptions.NotFoundException;
import manu_barone.DogVille.payloads.UtenteDTO;
import manu_barone.DogVille.repositories.UtenteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UtenteService {
    @Autowired
    private UtenteRepo userRepository;

    @Autowired
    private PasswordEncoder bcrypt;

    public Utente findById(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nessun utente trovato con ID: " + id));
    }


    public Utente findByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Nessun utente registrato con questa email"));
    }


    public Utente save(UtenteDTO body) {
        this.userRepository.findByEmail(body.email()).ifPresent(
                user -> {
                    throw new BadRequestException("Email " + body.email() + " già in uso!");
                }
        );

        Utente newUser = new Utente(body.name(), body.surname(), body.email(), bcrypt.encode(body.password()), body.address(), body.telephoneNumber());

        return this.userRepository.save(newUser);
    }


    public Utente updateUtente(UUID id, UtenteDTO body) {
        Utente utente = this.findById(id);

        if (body.name() != null) utente.setName(body.name());
        if (body.surname() != null) utente.setSurname(body.surname());
        if (body.email() != null) utente.setEmail(body.email());
        if (body.address() != null) utente.setAddress(body.address());
        if (body.telephoneNumber() != null) utente.setTelephoneNumber(body.telephoneNumber());
        if (body.password() != null && !body.password().isEmpty()) {
            utente.setPassword(bcrypt.encode(body.password()));
        }

        return userRepository.save(utente);
    }

    public List<Utente> findAll(){
        return userRepository.findAll();
    }


    public void deleteUtente(UUID id) {
        Utente utente = this.findById(id);
        userRepository.delete(utente);
    }

    public Utente switchRole(UUID id) {
        Utente utente = this.findById(id);
        utente.setRole(utente.getRole() == Ruolo.USER ? Ruolo.ADMIN : Ruolo.USER);
        return userRepository.save(utente);
    }


}
