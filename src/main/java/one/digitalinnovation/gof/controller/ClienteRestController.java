package one.digitalinnovation.gof.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import one.digitalinnovation.gof.dto.ClienteRequestDTO;
import one.digitalinnovation.gof.dto.ClienteResponseDTO;
import one.digitalinnovation.gof.dto.mapper.ClienteMapper;
import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.service.ClienteService;

/**
 * Esse { @link RestController} representa nossa <b>Facade</b>, pois abstrai toda
 *  a complexidade de integrações (Banco de Dados H2 e API do ViaCEP) em uma
 * interface simples e coesa (API REST).
 *
 * Melhorias aplicadas:
 * - DTOs de entrada/saída (não expõe entidade JPA diretamente)
 * - Validação de entrada (@Valid)
 * - Retornos HTTP mais corretos (201/204/404/400)
 * * @author falvojr
 * * @author wicker250 (refatorações/melhorias)
 */
@RestController
@RequestMapping("/clientes")
public class ClienteRestController {

	private final ClienteService clienteService;

	public ClienteRestController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@GetMapping
	public ResponseEntity<List<ClienteResponseDTO>> buscarTodos() {
		Iterable<Cliente> clientes = clienteService.buscarTodos();
		List<ClienteResponseDTO> body = StreamSupport.stream(clientes.spliterator(), false)
				.map(ClienteMapper::toResponse)
				.collect(Collectors.toList());
		return ResponseEntity.ok(body);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
		Cliente cliente = clienteService.buscarPorId(id);
		return ResponseEntity.ok(ClienteMapper.toResponse(cliente));
	}

	@PostMapping
	public ResponseEntity<ClienteResponseDTO> inserir(@Valid @RequestBody ClienteRequestDTO request) {
		Cliente salvo = clienteService.inserir(ClienteMapper.toEntity(request));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(salvo.getId())
				.toUri();
		return ResponseEntity.created(location).body(ClienteMapper.toResponse(salvo));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO request) {
		Cliente atualizado = clienteService.atualizar(id, ClienteMapper.toEntity(request));
		return ResponseEntity.ok(ClienteMapper.toResponse(atualizado));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		clienteService.deletar(id);
		return ResponseEntity.noContent().build();
	}
}
