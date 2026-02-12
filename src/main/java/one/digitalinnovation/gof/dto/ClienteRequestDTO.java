package one.digitalinnovation.gof.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class ClienteRequestDTO {

    @NotBlank(message = "nome é obrigatório")
    private String nome;

    @Valid
    @NotNull(message = "endereco é obrigatório")
    private EnderecoRequestDTO endereco;

    public String getNome() {

        return nome;
    }

    public void setNome(String nome) {

        this.nome = nome;
    }

    public EnderecoRequestDTO getEndereco() {

        return endereco;
    }

    public void setEndereco(EnderecoRequestDTO endereco) {

        this.endereco = endereco;
    }

    public static class EnderecoRequestDTO {

         @NotBlank(message = "cep é obrigatório")
         @javax.validation.constraints.Pattern(regexp = "\\d{8}", message = "cep deve conter 8 dígitos")
         private String cep;

         public String getCep() {
            return cep;
        }

         public void setCep(String cep) {
            this.cep = cep;
        }
    }
}
