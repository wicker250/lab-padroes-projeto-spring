package one.digitalinnovation.gof.dto.mapper;

import one.digitalinnovation.gof.dto.ClienteRequestDTO;
import one.digitalinnovation.gof.dto.ClienteResponseDTO;
import one.digitalinnovation.gof.dto.EnderecoResponseDTO;
import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.Endereco;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static Cliente toEntity(ClienteRequestDTO dto) {
        Cliente c = new Cliente();
        c.setNome(dto.getNome());

        Endereco e = new Endereco();
        // o service é quem vai hidratar o endereço completo via CEP
        e.setCep(dto.getEndereco().getCep());
        c.setEndereco(e);

        return c;
    }

    public static ClienteResponseDTO toResponse(Cliente entity) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setEndereco(toEnderecoResponse(entity.getEndereco()));
        return dto;
    }

    private static EnderecoResponseDTO toEnderecoResponse(Endereco e) {
        if (e == null) {
            return null;
        }
        EnderecoResponseDTO dto = new EnderecoResponseDTO();
        dto.setCep(e.getCep());
        dto.setLogradouro(e.getLogradouro());
        dto.setComplemento(e.getComplemento());
        dto.setBairro(e.getBairro());
        dto.setLocalidade(e.getLocalidade());
        dto.setUf(e.getUf());
        dto.setIbge(e.getIbge());
        dto.setGia(e.getGia());
        dto.setDdd(e.getDdd());
        dto.setSiafi(e.getSiafi());
        return dto;
    }
}
