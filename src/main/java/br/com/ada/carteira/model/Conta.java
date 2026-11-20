package br.com.ada.carteira.model;
import jakarta.persistence.*;
@Entity
public class Conta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String titular;
    @Column(nullable = false) private Double saldo;
    protected Conta() { }
    public Conta(Long id, String titular, Double saldo) {
        this.id = id; this.titular = titular; this.saldo = saldo;
    }
    public Long getId() { return id; }
    public String getTitular() { return titular; }
    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }
}
