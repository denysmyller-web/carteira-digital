package br.com.ada.carteira.interfaceweb;
import br.com.ada.carteira.model.Conta;
import br.com.ada.carteira.repository.ContaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("interface")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class DepositoSeleniumTest {
    @LocalServerPort int porta;
    @Autowired ContaRepository repository;
    WebDriver driver;
    Long id;
    @BeforeEach void abrirNavegador() {
        id = repository.save(new Conta(null, "Teste Selenium", 100.0)).getId();
        ChromeOptions options = new ChromeOptions();
        if (!Boolean.getBoolean("selenium.visivel")) options.addArguments("--headless=new");
        options.addArguments("--window-size=1200,900");
        driver = new ChromeDriver(options);
        driver.get("http://127.0.0.1:"+porta+"/");
    }
    @AfterEach void fecharNavegador() { if (driver != null) driver.quit(); }
    @Test void depositaPeloFormularioEConfirmaPersistencia() throws Exception {
        driver.findElement(By.id("contaId")).sendKeys(id.toString());
        driver.findElement(By.id("valor")).sendKeys("25.50");
        driver.findElement(By.id("depositar")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.textToBePresentInElementLocated(By.id("resultado"), "125,50"));
        assertEquals(125.5, repository.findById(id).orElseThrow().getSaldo());
        Path pasta = Path.of("target", "evidencias"); Files.createDirectories(pasta);
        Files.write(pasta.resolve("deposito-selenium.png"), ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES));
    }
    @Test void mostraErroDaApiParaContaInexistente() {
        driver.findElement(By.id("contaId")).sendKeys("999999");
        driver.findElement(By.id("valor")).sendKeys("10");
        driver.findElement(By.id("depositar")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.textToBePresentInElementLocated(By.id("resultado"), "não encontrada"));
        assertEquals("erro", driver.findElement(By.id("resultado")).getAttribute("class"));
    }
}
