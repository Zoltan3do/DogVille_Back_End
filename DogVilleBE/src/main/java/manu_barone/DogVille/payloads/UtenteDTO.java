package manu_barone.DogVille.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import manu_barone.DogVille.payloads.validationGroups.Create;

public record UtenteDTO(
        @NotEmpty(message = "Il nome è obbligatorio!" , groups = Create.class)
        @Size(min = 2, max = 40, message = "Il nome deve essere compreso tra 2 e 40 caratteri!")
        String name,
        @NotEmpty(message = "Il cognome è obbligatorio!", groups = Create.class)
        @Size(min = 2, max = 40, message = "Il cognome deve essere compreso tra 2 e 40 caratteri!")
        String surname,
        @NotEmpty(message = "Lo username è obbligatorio!", groups = Create.class)
        @Email(message = "L'email inserita non è un'email valida!")
        @Size(min = 4, message = "La email deve avere almeno 4 caratteri!")
        String email,
        @NotEmpty(message = "password vuota", groups = Create.class)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "La password non segue i criteri comuni")
        String password,
        @NotEmpty(message = "L'indirizzo è obbligatorio!", groups = Create.class)
        @Size(min = 4, message = "L'indirizzo deve avere almeno 4 caratteri!")
        String address,
        @NotEmpty(groups = Create.class)
        @Pattern(regexp = "^\\+39\\d{10}$")
        String telephoneNumber
) {
}
