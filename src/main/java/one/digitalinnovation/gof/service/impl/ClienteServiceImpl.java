package one.digitalinnovation.gof.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import one.digitalinnovation.gof.exception.BadRequestException;
import one.digitalinnovation.gof.exception.ResourceNotFoundException;
import one.digitalinnovation.gof.integration.viacep.ViaCepResponse;
import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 * 
 * @author falvojr
 */
@Service
public class ClienteServiceImpl implements ClienteService {

	private final ClienteRepository clienteRepository;
	private final EnderecoRepository enderecoRepository;
	private final ViaCepService viaCepService;

	public ClienteServiceImpl(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository,
			ViaCepService viaCepService) {
		this.clienteRepository = clienteRepository;
		this.enderecoRepository = enderecoRepository;
		this.viaCepService = viaCepService;
	}
	
	// Strategy: Implementar os métodos definidos na interface.
	// Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

	@Override
	public Iterable<Cliente> buscarTodos() {
		// Buscar todos os Clientes.
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		// Buscar Cliente por ID.
		return clienteRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: id=" + id));
	}

	@Override
	@Transactional
	public Cliente inserir(Cliente cliente) {
		return salvarClienteComCep(cliente);
	}

	@Override
	@Transactional
	public Cliente atualizar(Long id, Cliente cliente) {
		// Buscar Cliente por ID, caso exista:
		clienteRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: id=" + id));
		cliente.setId(id);
		return salvarClienteComCep(cliente);
	}

	@Override
	public void deletar(Long id) {
		// Deletar Cliente por ID.
		if (!clienteRepository.existsById(id)) {
			throw new ResourceNotFoundException("Cliente não encontrado: id=" + id);
		}
		clienteRepository.deleteById(id);
	}

	private Cliente salvarClienteComCep(Cliente cliente) {
		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = extractAndNormalizeCep(cliente);
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno.
			ViaCepResponse viaCep = viaCepService.consultarCep(cep);
			if (viaCep == null || Boolean.TRUE.equals(viaCep.getErro())) {
				throw new BadRequestException("CEP inválido: " + cep);
			}
			Endereco novoEndereco = mapViaCepToEndereco(viaCep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		return clienteRepository.save(cliente);
	}

	private String extractAndNormalizeCep(Cliente cliente) {
		if (cliente == null || cliente.getEndereco() == null || cliente.getEndereco().getCep() == null) {
			throw new BadRequestException("Informe o endereço com o CEP");
		}
		String cep = cliente.getEndereco().getCep().replaceAll("\\D", "");
		if (cep.length() != 8) {
			throw new BadRequestException("cep deve conter 8 dígitos");
		}
		return cep;
	}

	private Endereco mapViaCepToEndereco(ViaCepResponse r) {
		Endereco e = new Endereco();
		e.setCep(r.getCep());
		e.setLogradouro(r.getLogradouro());
		e.setComplemento(r.getComplemento());
		e.setBairro(r.getBairro());
		e.setLocalidade(r.getLocalidade());
		e.setUf(r.getUf());
		e.setIbge(r.getIbge());
		e.setGia(r.getGia());
		e.setDdd(r.getDdd());
		e.setSiafi(r.getSiafi());
		return e;
	}

}
