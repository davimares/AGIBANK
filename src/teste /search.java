import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes automatizados.
 */
@DisplayName("Campo de pesquisa")
public class SearchTests {

    private static final String BLOG_URL = "https://blog.agibank.com.br/";
    private static final String LUPA_PESQUISA_XPATH = "//*[@id=\"ast-desktop-header\"]/div[1]/div/div/div/div[3]/div[2]/div/div/a";
    private static final String SEARCH_FIELD_XPATH = "//*[@id=\"search-field\"]";
    private static final String NO_RESULTS_CLASS = "no-results";
    private static final String MENSAGEM_NAO_ENCONTRADO = "Lamentamos, mas nada foi encontrado para sua pesquisa, tente novamente com outras palavras.";
    private static final String PAGE_TITLE_CLASS = "page-title";

    private WebDriver driver;
    private FluentWait<WebDriver> wait;
    private String termoDePesquisa;

    @BeforeEach
    public void setUp() {
        // Configuração do WebDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox", "--disable-extensions", "--start-maximized");

        driver = new ChromeDriver(options);

        // Configuração do WebDriverWait
        wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(90))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Pesquisar um termo existente")
    public void testPesquisarUmTermoExistente() {
        termoDePesquisa = "banco";

        acessarBlog();
        realizarPesquisa(termoDePesquisa);
        rolarPaginaParaResultadosAdicionais();

        // Verifica se o termo foi encontrado nos resultados
        assertTrue(verificarResultados(termoDePesquisa), "O termo de pesquisa não foi encontrado.");
    }

    @Test
    @DisplayName("Pesquisar um termo inexistente")
    public void testPesquisarUmTermoInexistente() {
        termoDePesquisa = "asdasdasd";

        acessarBlog();
        realizarPesquisa(termoDePesquisa);

        // Verifica se a mensagem de "não encontrado" foi exibida corretamente
        assertEquals(0, verificarNumeroDeArtigosRetornados(), "Deveriam ser 0 resultados.");
        assertTrue(verificarMensagemNaoEncontrado(), "A mensagem 'não encontrado' não foi exibida.");
    }

    @Test
    @DisplayName("Testar SQL Injection no campo de pesquisa")
    public void testSqlInjectionInSearchField() {
        termoDePesquisa = "' OR '1'='1'";

        acessarBlog();
        realizarPesquisa(termoDePesquisa);

        // Verifica se a SQL Injection foi bem-sucedida
        assertFalse(verificarNumeroDeArtigosRetornados() > 0, "O campo de pesquisa é vulnerável a SQL Injection.");
    }

    private void acessarBlog() {
        driver.get(BLOG_URL);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(LUPA_PESQUISA_XPATH)));
    }

    private void realizarPesquisa(String termoDePesquisa) {
        clicarElemento(By.xpath(LUPA_PESQUISA_XPATH));
        WebElement campoPesquisa = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SEARCH_FIELD_XPATH)));
        campoPesquisa.sendKeys(termoDePesquisa);
        campoPesquisa.submit();
    }

    private void clicarElemento(By by) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
        element.click();
    }

    private void rolarPaginaParaResultadosAdicionais() {
        // Rola a página para carregar mais resultados
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(PAGE_TITLE_CLASS)));
        Actions actions = new Actions(driver);
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromViewport(10, 10);

        for (int i = 0; i < 250; i++) {
            actions.scrollFromOrigin(scrollOrigin, 0, 1000).perform();
        }
    }

    private boolean verificarResultados(String termoDePesquisa) {
        List<WebElement> artigos = driver.findElements(By.tagName("article"));
        for (WebElement artigo : artigos) {
            if (!verificarTermoNoArtigo(artigo, termoDePesquisa)) {
                return false;
            }
        }
        return true;
    }

    private boolean verificarTermoNoArtigo(WebElement artigo, String termoDePesquisa) {
        String textoArtigo = artigo.getText().toLowerCase();
        if (textoArtigo.contains(termoDePesquisa.toLowerCase())) {
            return true;
        } else {
            artigo.findElement(By.tagName("a")).click();
            WebElement articleText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("entry-content")));
            boolean termoEncontrado = articleText.getText().toLowerCase().contains(termoDePesquisa.toLowerCase());
            driver.navigate().back();
            return termoEncontrado;
        }
    }

    private int verificarNumeroDeArtigosRetornados() {
        List<WebElement> artigos = driver.findElements(By.tagName("article"));
        return artigos.size();
    }

    private boolean verificarMensagemNaoEncontrado() {
        WebElement mensagemNaoEncontrado = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(NO_RESULTS_CLASS)));
        return mensagemNaoEncontrado.getText().contains(MENSAGEM_NAO_ENCONTRADO);
    }
}

