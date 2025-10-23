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
    private final String CAMINHO_IMAGENS_ORIGEM_TESTE = "C:\\temp_testes\\";
    private final String CAMINHO_BANCO_IMAGENS_DESTINO = "C:\\Users\\aluno\\BancoImagens\\";
    private ProductRepository productRepository;
    private ProductService productService;
    private ProductApplication productApplication;
    private ProductFacade productFacade;
    private Product product1;
    private Path arquivoImagemFalso1;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE));
        Files.createDirectories(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO));
        arquivoImagemFalso1 = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "fake1.jpg");
        Files.deleteIfExists(arquivoImagemFalso1);
        Files.createFile(arquivoImagemFalso1);
        productRepository = new ProductRepository();
        productService = new ProductService();
        productApplication = new ProductApplication(productRepository, productService);
        productFacade = new ProductFacade(productApplication);
        product1 = new Product(1, "Hot Dog", 10.4f, arquivoImagemFalso1.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(arquivoImagemFalso1);
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg"));
    }

    @Test
    public void deveRetornarListaCompletaDeProdutos() {
        // Arrange
        productRepository.append(product1);
        productRepository.append(new Product(2, "Burger", 15f, ""));
        // Act
        List<Product> lista = productFacade.getAll();
        // Assert
        assertNotNull(lista);
        assertEquals(2, lista.size());
    }

    @Test
    public void deveRetornarProdutoCorretoPorIdValido() {
        // Arrange
        productRepository.append(product1);
        // Act
        Product produtoEncontrado = productFacade.getById(1);
        // Assert
        assertNotNull(produtoEncontrado);
        assertEquals(1, produtoEncontrado.getId());
        assertEquals("Hot Dog", produtoEncontrado.getDescription());
    }
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
        // Act
        boolean resultado = productFacade.exists(99);
        // Assert
        assertFalse(resultado);
    }
    @Test
    public void deveAdicionarNovoProdutoCorretamente() {
        // Arrange
        Path arquivoDestinoEsperado = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertFalse(Files.exists(arquivoDestinoEsperado));
        // Act
        productFacade.append(product1);
        // Assert
        assertTrue(productRepository.exists(1));
        assertTrue(Files.exists(arquivoDestinoEsperado));
    }
    @Test
    public void deveRemoverProdutoExistenteCorretamente() {
        // Arrange
        productFacade.append(product1);
        Path arquivoParaDeletar = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertTrue(productRepository.exists(1));
        assertTrue(Files.exists(arquivoParaDeletar));
        // Act
        productFacade.remove(1);
        // Assert
        assertFalse(productRepository.exists(1));
        assertFalse(Files.exists(arquivoParaDeletar));
    }
}