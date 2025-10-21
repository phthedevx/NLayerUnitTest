package com.snack.facade; // <- Pacote da camada Facade

import com.snack.applications.ProductApplication;
import com.snack.entities.Product;
import com.snack.repositories.ProductRepository;
import com.snack.services.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductFacadeTest {

    // Dependências de ambiente (necessárias para o ProductService)
    private final String CAMINHO_IMAGENS_ORIGEM_TESTE = "C:\\temp_testes\\";
    private final String CAMINHO_BANCO_IMAGENS_DESTINO = "C:\\Users\\aluno\\BancoImagens\\";

    // A stack completa de classes REAIS (sem mocks)
    private ProductRepository productRepository;
    private ProductService productService;
    private ProductApplication productApplication;
    private ProductFacade productFacade; // A classe que estamos a testar

    // Dados de teste
    private Product product1;
    private Path arquivoImagemFalso1;

    @BeforeEach
    void setUp() throws IOException {
        // 1. Criar o ambiente de ficheiros (para o Service)
        Files.createDirectories(Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE));
        Files.createDirectories(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO));
        arquivoImagemFalso1 = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "fake1.jpg");
        Files.deleteIfExists(arquivoImagemFalso1); // Garante que está limpo
        Files.createFile(arquivoImagemFalso1);

        // 2. Instanciar as dependências REAIS (como no App.java)
        productRepository = new ProductRepository();
        productService = new ProductService();
        productApplication = new ProductApplication(productRepository, productService);

        // 3. Instanciar a classe-alvo (Facade)
        productFacade = new ProductFacade(productApplication);

        // 4. Preparar dados de teste
        product1 = new Product(1, "Hot Dog", 10.4f, arquivoImagemFalso1.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        // Limpar os ficheiros criados
        Files.deleteIfExists(arquivoImagemFalso1);
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg"));
    }

    // --- TESTES DE INTEGRAÇÃO DO ProductFacade ---

    /**
     * Teste 1: "Retornar a lista completa de produtos ao chamar o método getAll."
     */
    @Test
    public void deveRetornarListaCompletaDeProdutos() {
        // Arrange
        // Adiciona 2 produtos diretamente no repositório para o setup
        productRepository.append(product1);
        productRepository.append(new Product(2, "Burger", 15f, ""));

        // Act
        // Chama o método da Fachada
        List<Product> lista = productFacade.getAll();

        // Assert
        assertNotNull(lista);
        assertEquals(2, lista.size());
    }

    /**
     * Teste 2: "Retornar o produto correto ao fornecer um ID válido no método getById."
     */
    @Test
    public void deveRetornarProdutoCorretoPorIdValido() {
        // Arrange
        productRepository.append(product1); // Coloca o produto no sistema

        // Act
        Product produtoEncontrado = productFacade.getById(1);

        // Assert
        assertNotNull(produtoEncontrado);
        assertEquals(1, produtoEncontrado.getId());
        assertEquals("Hot Dog", produtoEncontrado.getDescription());
    }

    /**
     * Teste 3: "Retornar true para um ID existente e false para um ID inexistente"
     * (Dividido em dois testes)
     */
    @Test
    public void deveRetornarTrueParaIdExistente() {
        // Arrange
        productRepository.append(product1);

        // Act
        boolean resultado = productFacade.exists(1);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void deveRetornarFalseParaIdInexistente() {
        // Arrange
        // (Repositório está vazio)

        // Act
        boolean resultado = productFacade.exists(99);

        // Assert
        assertFalse(resultado);
    }

    /**
     * Teste 4: "Adicionar um novo produto corretamente ao chamar o método append."
     * (Valida a integração completa: Facade -> App -> Repo + Service)
     */
    @Test
    public void deveAdicionarNovoProdutoCorretamente() {
        // Arrange
        // (product1 está pronto no @BeforeEach)
        Path arquivoDestinoEsperado = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertFalse(Files.exists(arquivoDestinoEsperado), "Setup falhou: Imagem de destino já existe.");

        // Act
        productFacade.append(product1);

        // Assert
        // 1. Verifica a camada de Repositório (memória)
        assertTrue(productRepository.exists(1), "Produto não foi salvo no repositório.");

        // 2. Verifica a camada de Serviço (disco)
        assertTrue(Files.exists(arquivoDestinoEsperado), "Imagem não foi salva no disco.");
    }

    /**
     * Teste 5: "Remover um produto existente ao fornecer um ID válido no método remove."
     * (Valida a integração completa: Facade -> App -> Repo + Service)
     */
    @Test
    public void deveRemoverProdutoExistenteCorretamente() {
        // Arrange
        // 1. Adiciona o produto em todo o sistema
        productFacade.append(product1);

        // 2. Confirma que ele existe em todo o lado
        Path arquivoParaDeletar = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertTrue(productRepository.exists(1), "Setup falhou: Produto não está no repo.");
        assertTrue(Files.exists(arquivoParaDeletar), "Setup falhou: Imagem não está no disco.");

        // Act
        productFacade.remove(1);

        // Assert
        // 1. Verifica a camada de Repositório (memória)
        assertFalse(productRepository.exists(1), "Produto não foi removido do repositório.");

        // 2. Verifica a camada de Serviço (disco)
        assertFalse(Files.exists(arquivoParaDeletar), "Imagem não foi removida do disco.");
    }
}