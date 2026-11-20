package br.com.ada.carteira.service;
import br.com.ada.carteira.model.Conta;
import br.com.ada.carteira.repository.ContaRepository;
import br.com.ada.carteira.exception.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {
    @Mock ContaRepository repository;
    @InjectMocks ContaService service;
    Conta origem, destino;
    @BeforeEach void preparar() {
        origem = new Conta(1L, "Ana", 100.0);
        destino = new Conta(2L, "Bruno", 20.0);
    }
    @Test void criaContaComSaldoZero() {
        when(repository.save(any(Conta.class))).thenAnswer(i -> i.getArgument(0));
        Conta conta = service.criarConta(" Ana ");
        assertEquals("Ana", conta.getTitular());
        assertEquals(0.0, conta.getSaldo());
        verify(repository).save(conta);
    }
    @ParameterizedTest @NullSource @ValueSource(strings={"", "  "})
    void rejeitaTitularInvalido(String titular) {
        assertThrows(IllegalArgumentException.class, () -> service.criarConta(titular));
        verifyNoInteractions(repository);
    }
    @Test void consultaSaldo() {
        when(repository.findById(1L)).thenReturn(Optional.of(origem));
        assertEquals(100.0, service.consultarSaldo(1L));
    }
    @Test void consultaContaInexistente() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ContaNaoEncontradaException.class, () -> service.consultarSaldo(9L));
    }
    @Test void depositaValorPositivo() {
        when(repository.buscarParaAtualizar(1L)).thenReturn(Optional.of(origem));
        when(repository.save(origem)).thenReturn(origem);
        assertEquals(125.5, service.depositar(1L, 25.5).getSaldo());
        verify(repository).save(origem);
    }
    @ParameterizedTest @NullSource @ValueSource(doubles={0, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void rejeitaDepositoInvalido(Double valor) {
        assertThrows(IllegalArgumentException.class, () -> service.depositar(1L, valor));
        verifyNoInteractions(repository);
    }
    @Test void depositoEmContaInexistente() {
        when(repository.buscarParaAtualizar(9L)).thenReturn(Optional.empty());
        assertThrows(ContaNaoEncontradaException.class, () -> service.depositar(9L, 10.0));
        verify(repository, never()).save(any());
    }
    @Test void transfereEntreContas() {
        prepararTransferencia();
        service.transferir(1L, 2L, 30.0);
        assertEquals(70.0, origem.getSaldo());
        assertEquals(50.0, destino.getSaldo());
        verify(repository).save(origem); verify(repository).save(destino);
    }
    @Test void transfereSaldoIntegral() {
        prepararTransferencia(); service.transferir(1L, 2L, 100.0);
        assertEquals(0.0, origem.getSaldo()); assertEquals(120.0, destino.getSaldo());
    }
    @Test void bloqueiaSaldoInsuficienteSemAlterarContas() {
        prepararTransferencia();
        assertThrows(SaldoInsuficienteException.class, () -> service.transferir(1L, 2L, 100.01));
        assertEquals(100.0, origem.getSaldo()); assertEquals(20.0, destino.getSaldo());
        verify(repository, never()).save(any());
    }
    @ParameterizedTest @NullSource @ValueSource(doubles={0, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void rejeitaTransferenciaInvalida(Double valor) {
        assertThrows(IllegalArgumentException.class, () -> service.transferir(1L, 2L, valor));
        verifyNoInteractions(repository);
    }
    @Test void rejeitaMesmaConta() {
        assertThrows(IllegalArgumentException.class, () -> service.transferir(1L, 1L, 10.0));
        verifyNoInteractions(repository);
    }
    @Test void rejeitaDestinoInexistenteSemDebitar() {
        when(repository.buscarParaAtualizar(1L)).thenReturn(Optional.of(origem));
        when(repository.buscarParaAtualizar(2L)).thenReturn(Optional.empty());
        assertThrows(ContaNaoEncontradaException.class, () -> service.transferir(1L, 2L, 10.0));
        assertEquals(100.0, origem.getSaldo()); verify(repository, never()).save(any());
    }
    @Test void permiteTransferenciaNaOrdemInversaDeIds() {
        prepararTransferencia(); service.transferir(2L, 1L, 10.0);
        assertEquals(110.0, origem.getSaldo()); assertEquals(10.0, destino.getSaldo());
    }
    private void prepararTransferencia() {
        when(repository.buscarParaAtualizar(1L)).thenReturn(Optional.of(origem));
        when(repository.buscarParaAtualizar(2L)).thenReturn(Optional.of(destino));
    }
}
