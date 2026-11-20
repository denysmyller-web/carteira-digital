package br.com.ada.carteira.controller;
import br.com.ada.carteira.model.Conta;
import br.com.ada.carteira.service.ContaService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ContaController {
    private final ContaService service;
    public ContaController(ContaService service) { this.service = service; }
    public record CriarContaRequest(String titular) { }
    public record ValorRequest(Double valor) { }
    public record TransferenciaRequest(Long origemId, Long destinoId, Double valor) { }
    public record SaldoResponse(Double saldo) { }
    public record MensagemResponse(String mensagem) { }

    @PostMapping("/contas")
    public ResponseEntity<Conta> criar(@RequestBody CriarContaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarConta(request.titular()));
    }
    @GetMapping("/contas/{id}/saldo")
    public SaldoResponse saldo(@PathVariable Long id) { return new SaldoResponse(service.consultarSaldo(id)); }
    @PostMapping("/contas/{id}/depositos")
    public Conta depositar(@PathVariable Long id, @RequestBody ValorRequest request) {
        return service.depositar(id, request.valor());
    }
    @PostMapping("/transferencias")
    public MensagemResponse transferir(@RequestBody TransferenciaRequest request) {
        service.transferir(request.origemId(), request.destinoId(), request.valor());
        return new MensagemResponse("Transferência realizada com sucesso.");
    }
}
