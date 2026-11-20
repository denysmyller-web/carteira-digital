package br.com.ada.carteira.service;
import br.com.ada.carteira.model.Conta;
import br.com.ada.carteira.repository.ContaRepository;
import org.springframework.stereotype.Service;
@Service
public class ContaService {
    private final ContaRepository repository;
    public ContaService(ContaRepository repository) { this.repository = repository; }
    public Conta criarConta(String titular) { throw new UnsupportedOperationException(); }
    public Double consultarSaldo(Long id) { throw new UnsupportedOperationException(); }
    public Conta depositar(Long id, Double valor) { throw new UnsupportedOperationException(); }
    public void transferir(Long origemId, Long destinoId, Double valor) { throw new UnsupportedOperationException(); }
}
