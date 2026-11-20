package br.com.ada.carteira.service;
import br.com.ada.carteira.model.Conta;
import br.com.ada.carteira.repository.ContaRepository;
import br.com.ada.carteira.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContaService {
    private final ContaRepository repository;
    public ContaService(ContaRepository repository) { this.repository = repository; }

    @Transactional
    public Conta criarConta(String titular) {
        if (titular == null || titular.isBlank()) throw new IllegalArgumentException("Informe o titular.");
        return repository.save(new Conta(null, titular.trim(), 0.0));
    }
    @Transactional(readOnly = true)
    public Double consultarSaldo(Long id) {
        validarId(id);
        return repository.findById(id).orElseThrow(() -> new ContaNaoEncontradaException(id)).getSaldo();
    }
    @Transactional
    public Conta depositar(Long id, Double valor) {
        validarValor(valor);
        validarId(id);
        Conta conta = buscarParaAtualizar(id);
        conta.setSaldo(conta.getSaldo() + valor);
        return repository.save(conta);
    }
    @Transactional
    public void transferir(Long origemId, Long destinoId, Double valor) {
        validarValor(valor);
        validarId(origemId);
        validarId(destinoId);
        if (origemId.equals(destinoId)) throw new IllegalArgumentException("As contas devem ser diferentes.");
        // Bloqueios sempre na mesma ordem evitam deadlock em transferências opostas.
        Long menor = Math.min(origemId, destinoId), maior = Math.max(origemId, destinoId);
        Conta primeira = buscarParaAtualizar(menor);
        Conta segunda = buscarParaAtualizar(maior);
        Conta origem = origemId.equals(menor) ? primeira : segunda;
        Conta destino = destinoId.equals(menor) ? primeira : segunda;
        if (origem.getSaldo() < valor) throw new SaldoInsuficienteException();
        origem.setSaldo(origem.getSaldo() - valor);
        destino.setSaldo(destino.getSaldo() + valor);
        repository.save(origem); repository.save(destino);
    }
    private Conta buscarParaAtualizar(Long id) {
        return repository.buscarParaAtualizar(id).orElseThrow(() -> new ContaNaoEncontradaException(id));
    }
    private void validarValor(Double valor) {
        if (valor == null || !Double.isFinite(valor) || valor <= 0)
            throw new IllegalArgumentException("O valor deve ser positivo e finito.");
    }
    private void validarId(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Informe um identificador positivo.");
    }
}
