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
 * Classe de testes automatizados para a funcionalidade de pesquisa do Blog do Agi.
 */
@DisplayName("Testes automatizados da funcionalidade de pesquisa")
public class SearchTests {

    private static final String BLOG_URL = "https://blogdoagi.com.br/";
    private static final String LUPA_PESQUISA_XPATH = "//*[@id=\"ast-desktop-header\"]/div[1]/div/div/div/div[3]/div[2]/div/div/a";
    private static final String SEARCH_FIELD_CLASS = "//*[@id=\"search-field\"]";
    private static final String NO_RESULTS_CLASS = "no-results";
    private static final String MENSAGEM_NAO_ENCONTRADO = "Lamentamos, mas nada foi encontrado para sua pesquisa, tente novamente com outras palavras.";
    private static final String PAGE_TITLE_CLASS = "page-title";

    private WebDriver driver;
    private FluentWait<WebDriver> wait;
    private String termoDePesquisa;

    @BeforeEach
    public void setUp() {
        // Configura o WebDriver e o FluentWait
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // Adiciona as opções do Chrome:

        options.addArguments("--disable-dev-shm-usage");
        // Supera problemas de recursos limitados ao redirecionar o Chrome para usar o disco em vez da memória compartilhada (/dev/shm).

        options.addArguments("start-maximized");
        // Abre o navegador em modo maximizado para ter uma visualização completa da página.

        options.addArguments("disable-infobars");
        // Desabilita a barra de informações que avisa "Chrome is being controlled by automated test software".

        options.addArguments("--disable-extensions");
        // Desabilita todas as extensões do Chrome para evitar interferências durante os testes.

        options.addArguments("--disable-gpu");
        // Desativa a aceleração de hardware de GPU. Aplicável principalmente para sistemas operacionais Windows, mas usado em outros sistemas para evitar bugs relacionados à GPU.

        options.addArguments("--no-sandbox");
        // Desativa o sandboxing de segurança do SO, necessário em alguns ambientes de CI para evitar problemas de permissão.

        options.addArguments("--headless");
        // Executa o Chrome em modo headless (sem interface gráfica), essencial para ambientes de CI.

        options.addArguments("--disable-software-rasterizer");
        // Desativa o rasterizador de software, útil para melhorar o desempenho em sistemas com baixa capacidade gráfica.

        options.addArguments("--remote-debugging-port=9222");
        // Configura a porta para depuração remota, permitindo que o Chrome seja depurado de maneira remota durante a execução em modo headless.

        driver = new ChromeDriver(options);

        wait = new WebDriverWait(driver, Duration.ofSeconds(90))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
    }

    @AfterEach
    public void tearDown() {
        // Fecha o navegador após cada teste
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Pesquisar um termo existente")
    public void testPesquisarUmTermoExistente() {
        termoDePesquisa = "financiamento";

        acessarBlog();
        realizarPesquisa(termoDePesquisa);
        rolarPaginaParaResultadosAdicionais();
        boolean termoEncontrado = verificarResultados(termoDePesquisa);

        // Verifica se o termo de pesquisa foi encontrado em todos os artigos
        assertTrue(termoEncontrado, "O termo de pesquisa não foi encontrado em pelo menos 1 dos resultados.");
    }

    @Test
    @DisplayName("Pesquisar um termo inexistente")
    public void testPesquisarUmTermoInexistente() {
        termoDePesquisa = "basculho";

        acessarBlog();
        realizarPesquisa(termoDePesquisa);

        // Verifica se nenhum artigo foi retornado e se a mensagem de "não encontrado" foi exibida corretamente
        assertEquals(0, verificarNumeroDeArtigosRetornados());
        assertTrue(verificarMensagemNaoEncontrado(), "Mensagem de artigo não encontrado não foi exibida corretamente");
    }

    @Test
    @DisplayName("Testar SQL Injection no campo de pesquisa")
    public void testSqlInjectionInSearchField() {
        termoDePesquisa = "' OR '1'='1'";

        acessarBlog();
        realizarPesquisa(termoDePesquisa);

        // Verifica se o termo injetado retorna mais resultados do que o esperado
        boolean isInjectionSuccessful = verificarNumeroDeArtigosRetornados() > 0;

        // O teste falha se a injeção for bem-sucedida
        assertFalse(isInjectionSuccessful, "O campo de pesquisa é vulnerável a SQL Injection.");
    }

    /**
     * Acessa o blog do Agi.
     */
    private void acessarBlog() {
        driver.get(BLOG_URL);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(LUPA_PESQUISA_XPATH)));
    }

    /**
     * Realiza a pesquisa no blog utilizando o termo fornecido.
     *
     * @param termoDePesquisa O termo a ser pesquisado.
     */
    private void realizarPesquisa(String termoDePesquisa) {

        clicarElemento(By.xpath(LUPA_PESQUISA_XPATH));
        WebElement campoPesquisa = driver.findElement(By.xpath(SEARCH_FIELD_CLASS));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SEARCH_FIELD_CLASS)));
        campoPesquisa.sendKeys(termoDePesquisa);
        campoPesquisa.submit();
    }

    /**
     * Clica em um elemento da página identificado pelo By fornecido.
     *
     * @param by Localizador do elemento a ser clicado.
     */
    private void clicarElemento(By by) {

        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    /**
     * Rola a página para carregar mais resultados.
     */
    private void rolarPaginaParaResultadosAdicionais() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(PAGE_TITLE_CLASS)));
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromViewport(10, 10);
        Actions actions = new Actions(driver);

        for (int i = 0; i < 250; i++) {
            actions.scrollFromOrigin(scrollOrigin, 0, 1000).perform();
        }
    }

    /**
     * Verifica se o termo de pesquisa aparece em todos os artigos retornados.
     *
     * @param termoDePesquisa O termo a ser verificado.
     * @return True se o termo foi encontrado em todos os artigos, False caso contrário.
     */
    private boolean verificarResultados(String termoDePesquisa) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("article")));
        List<WebElement> artigos = driver.findElements(By.tagName("article"));
        for (WebElement artigo : artigos) {
            if (!verificarTermoNoArtigo(artigo, termoDePesquisa)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se o termo de pesquisa que não aparece no título aparece no corpo do artigo.
     *
     * @param artigo          O artigo a ser verificado.
     * @param termoDePesquisa O termo a ser pesquisado.
     * @return True se o termo foi encontrado, False caso contrário.
     */
    private boolean verificarTermoNoArtigo(WebElement artigo, String termoDePesquisa) {
        if (artigo.getText().toLowerCase().contains(termoDePesquisa.toLowerCase())) {
            return true;
        } else {
            artigo.findElement(By.tagName("a")).click();
            WebElement articleText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("entry-content")));
            boolean termoEncontrado = articleText.getText().toLowerCase().contains(termoDePesquisa.toLowerCase());
            driver.navigate().back();
            return termoEncontrado;
        }
    }

    /**
     * Verifica o número de artigos retornados pela pesquisa.
     *
     * @return O número de artigos encontrados.
     */
    private int verificarNumeroDeArtigosRetornados() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(PAGE_TITLE_CLASS)));
        List<WebElement> artigos = driver.findElements(By.tagName("article"));
        return artigos.size();
    }

    /**
     * Verifica se a mensagem de "não encontrado" foi exibida corretamente.
     *
     * @return True se a mensagem foi exibida, False caso contrário.
     */
    private boolean verificarMensagemNaoEncontrado() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(PAGE_TITLE_CLASS)));
        WebElement mensagemNaoEncontrado = driver.findElement(By.className(NO_RESULTS_CLASS));
        return mensagemNaoEncontrado.getText().contains(MENSAGEM_NAO_ENCONTRADO);
    }
}
