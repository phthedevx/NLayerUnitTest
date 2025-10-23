package com.snack.applications;

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
import java.util.NoSuchElementException; // Importe esta exceção

import static org.junit.jupiter.api.Assertions.*;

public class ProductApplicationTest {
    private final String CAMINHO_IMAGENS_ORIGEM_TESTE = "C:\\temp_testes\\";
    private final String CAMINHO_BANCO_IMAGENS_DESTINO = "C:\\Users\\aluno\\BancoImagens\\";
    private ProductRepository productRepository;
    private ProductService productService;
    private ProductApplication productApplication; // A classe que estamos a testar
    private Product product1;
    private Path arquivoImagemFalso1;
    private Path arquivoImagemFalso2; // Para o teste de update

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE));
        Files.createDirectories(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO));
        arquivoImagemFalso1 = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "fake1.jpg");
        Files.deleteIfExists(arquivoImagemFalso1); // Garante que está limpo
        Files.createFile(arquivoImagemFalso1); // Cria C:\temp_testes\fake1.jpg
        arquivoImagemFalso2 = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "fake2.png");
        Files.deleteIfExists(arquivoImagemFalso2); // Garante que está limpo
        productRepository = new ProductRepository();
        productService = new ProductService(); // Usa o construtor vazio
        productApplication = new ProductApplication(productRepository, productService);
        product1 = new Product(1, "Hot Dog", 10.4f, arquivoImagemFalso1.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(arquivoImagemFalso1);
        Files.deleteIfExists(arquivoImagemFalso2);
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg"));
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.png"));
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "2.jpg"));
    }

    @Test
    public void deveListarTodosOsProdutosDoRepositorio() {
        // Arrange
        productRepository.append(product1);
        Product product2 = new Product(2, "X-Burger", 12.5f, "img/x.jpg");
        productRepository.append(product2);
        // Act
        List<Product> listaDeProdutos = productApplication.getAll();
        // Assert
        assertNotNull(listaDeProdutos);
        assertEquals(2, listaDeProdutos.size());
    }
    @Test
    public void deveObterProdutoPorIdValido() {
        // Arrange
        productRepository.append(product1);
        // Act
        Product produtoEncontrado = productApplication.getById(1);
        // Assert
        assertNotNull(produtoEncontrado);
        assertEquals(1, produtoEncontrado.getId());
    }

    @Test
    public void deveLancarExcecaoAoObterProdutoPorIdInvalido() {
        assertThrows(NoSuchElementException.class, () -> {
            productApplication.getById(99);
        });
    }
    @Test
    public void deveVerificarSeProdutoExistePorIdValido() {
        // Arrange
        productRepository.append(product1);
        // Act
        boolean resultado = productApplication.exists(1);
        // Assert
        assertTrue(resultado);
    }
    @Test
    public void deveRetornarFalsoAoVerificarProdutoComIdInvalido() {
        // Arrange
        // (Repositório está vazio)

        // Act
        boolean resultado = productApplication.exists(99);

        // Assert
        assertFalse(resultado);
    }
    @Test
    public void deveAdicionarProdutoESalvarImagem() {
        // Arrange
        Path arquivoDestinoEsperado = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        // Act
        productApplication.append(product1);
        // Assert
        assertTrue(productRepository.exists(1));
        assertTrue(Files.exists(arquivoDestinoEsperado));
    }
    @Test
    public void deveRemoverProdutoEDeletarImagem() {
        // Arrange
        productApplication.append(product1);
        Path arquivoParaDeletar = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertTrue(productRepository.exists(1));
        assertTrue(Files.exists(arquivoParaDeletar));

        // Act
        productApplication.remove(1);
        // Assert
        assertFalse(productRepository.exists(1));
        assertFalse(Files.exists(arquivoParaDeletar));
    }
    @Test
    public void deveLancarExcecaoAoRemoverProdutoInexistente() {
        assertThrows(NoSuchElementException.class, () -> {
            productApplication.remove(99);
        });
    }

    @Test
    public void deveAtualizarProdutoESubstituirImagem() throws IOException {
        // Arrange

        productApplication.append(product1);
        Path imagemAntiga = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertTrue(Files.exists(imagemAntiga));
        Files.createFile(arquivoImagemFalso2);
        Product dadosAtualizados = new Product(1, "Super Hot Dog", 20.0f, arquivoImagemFalso2.toString());
        Path imagemNova = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.png");
        // Act
        productApplication.update(1, dadosAtualizados);
        // Assert
        assertEquals("Super Hot Dog", productRepository.getById(1).getDescription());
        assertEquals(20.0f, productRepository.getById(1).getPrice());
        assertFalse(Files.exists(imagemAntiga));
        assertTrue(Files.exists(imagemNova));
    }
}