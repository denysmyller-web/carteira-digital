package br.com.ada.carteira.controller;
import br.com.ada.carteira.model.Conta;
import br.com.ada.carteira.repository.ContaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.MediaType;

@SpringBootTest @AutoConfigureMockMvc
class ContaControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ContaRepository repository;
    Long origem, destino;
    @BeforeEach void preparar() {
        repository.deleteAll();
        origem = repository.save(new Conta(null, "Ana", 100.0)).getId();
        destino = repository.save(new Conta(null, "Bruno", 20.0)).getId();
    }
    @Test void criaConta() throws Exception {
        mvc.perform(post("/api/contas").contentType(MediaType.APPLICATION_JSON).content("{\"titular\":\"Carla\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.titular").value("Carla")).andExpect(jsonPath("$.saldo").value(0.0));
    }
    @Test void rejeitaTitularVazio() throws Exception {
        mvc.perform(post("/api/contas").contentType(MediaType.APPLICATION_JSON).content("{\"titular\":\" \"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").exists());
    }
    @Test void consultaSaldo() throws Exception {
        mvc.perform(get("/api/contas/{id}/saldo", origem)).andExpect(status().isOk())
            .andExpect(jsonPath("$.saldo").value(100.0));
    }
    @Test void contaInexistente() throws Exception {
        mvc.perform(get("/api/contas/999999/saldo")).andExpect(status().isNotFound());
    }
    @Test void deposita() throws Exception {
        mvc.perform(post("/api/contas/{id}/depositos", origem).contentType(MediaType.APPLICATION_JSON).content("{\"valor\":25.5}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.saldo").value(125.5));
        assertEquals(125.5, saldo(origem));
    }
    @Test void rejeitaDepositoZero() throws Exception {
        mvc.perform(post("/api/contas/{id}/depositos", origem).contentType(MediaType.APPLICATION_JSON).content("{\"valor\":0}"))
            .andExpect(status().isBadRequest());
        assertEquals(100.0, saldo(origem));
    }
    @Test void rejeitaDepositoNulo() throws Exception {
        mvc.perform(post("/api/contas/{id}/depositos", origem).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
    }
    @Test void transfereEPersisteAmbasContas() throws Exception {
        transferir(origem, destino, 30).andExpect(status().isOk());
        assertEquals(70.0, saldo(origem)); assertEquals(50.0, saldo(destino));
    }
    @Test void saldoInsuficienteRetorna400SemAlteracoes() throws Exception {
        transferir(origem, destino, 101).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mensagem").value("Saldo insuficiente para realizar a transferência."));
        assertEquals(100.0, saldo(origem)); assertEquals(20.0, saldo(destino));
    }
    @Test void destinoInexistenteNaoDebita() throws Exception {
        transferir(origem, 999999L, 10).andExpect(status().isNotFound());
        assertEquals(100.0, saldo(origem));
    }
    @Test void rejeitaTransferenciaNegativa() throws Exception {
        transferir(origem, destino, -1).andExpect(status().isBadRequest());
    }
    @Test void rejeitaMesmaConta() throws Exception {
        transferir(origem, origem, 10).andExpect(status().isBadRequest());
    }
    @Test void rejeitaOrigemAusente() throws Exception {
        mvc.perform(post("/api/transferencias").contentType(MediaType.APPLICATION_JSON)
            .content("{\"destinoId\":1,\"valor\":10}" )).andExpect(status().isBadRequest());
    }
    @Test void jsonMalformadoRetorna400() throws Exception {
        mvc.perform(post("/api/contas").contentType(MediaType.APPLICATION_JSON).content("{"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").exists());
    }
    @Test void idInvalidoRetorna400() throws Exception {
        mvc.perform(get("/api/contas/abc/saldo")).andExpect(status().isBadRequest());
    }
    private org.springframework.test.web.servlet.ResultActions transferir(Long de, Long para, double valor) throws Exception {
        return mvc.perform(post("/api/transferencias").contentType(MediaType.APPLICATION_JSON)
            .content("{\"origemId\":"+de+",\"destinoId\":"+para+",\"valor\":"+valor+"}"));
    }
    private Double saldo(Long id) { return repository.findById(id).orElseThrow().getSaldo(); }
}
