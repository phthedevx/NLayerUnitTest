package com.snack.repositories;

import com.snack.entities.Product;
import com.snack.services.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {
    private final String CAMINHO_IMAGENS_ORIGEM_TESTE = "C:\\temp_testes\\";
    private final String CAMINHO_BANCO_IMAGENS_DESTINO = "C:\\Users\\aluno\\BancoImagens\\";

    private ProductService productService;
    private Path arquivoImagemFalso;
    private Product product;

    @BeforeEach
    void setUp() throws IOException {
        // Arrange
        productService = new ProductService();
        Files.createDirectories(Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE));
        Files.createDirectories(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO));
        arquivoImagemFalso = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "fake.jpg");
        if (!Files.exists(arquivoImagemFalso)) {
            Files.createFile(arquivoImagemFalso);
        }
        product = new Product(1, "Produto Teste", 10f, arquivoImagemFalso.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(arquivoImagemFalso); // C:\temp_testes\fake.jpg
        Files.deleteIfExists(Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "nova_imagem.png"));
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg"));
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.png"));
    }

    @Test
    public void deveSalvarProdutoComImagemValida() {
        // Arrange
        Path arquivoDestinoEsperado = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        // Act
        boolean resultado = productService.save(product);
        // Assert
        assertTrue(resultado);
        assertTrue(Files.exists(arquivoDestinoEsperado));
    }
    @Test
    public void deveRetornarFalseAoSalvarProdutoComImagemInexistente() {
        // Arrange
        Path arquivoInexistente = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "imagem_fantasma.jpg");
        product.setImage(arquivoInexistente.toString());
        Path arquivoDestinoEsperado = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        // Act
        boolean resultado = productService.save(product);
        // Assert
        assertFalse(resultado);
        assertFalse(Files.exists(arquivoDestinoEsperado));
    }
    @Test
    public void deveAtualizarProdutoExistente() throws IOException {
        // Arrange
        productService.save(product);
        Path oldImageFile = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertTrue(Files.exists(oldImageFile));
        Path newSourceImage = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "nova_imagem.png");
        Files.createFile(newSourceImage);
        product.setImage(newSourceImage.toString());
        Path newImageFile = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.png");
        // Act
        productService.update(product);
        // Assert
        assertFalse(Files.exists(oldImageFile));
        assertTrue(Files.exists(newImageFile));
    }
    @Test
    public void deveRemoverProdutoExistente() throws IOException {
        // Arrange
        productService.save(product);
        Path arquivoParaRemover = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertTrue(Files.exists(arquivoParaRemover));
        // Act
        productService.remove(1); // O ID é 1 (do 'product')
        // Assert
        assertFalse(Files.exists(arquivoParaRemover));
    }
    @Test
    public void deveObterCaminhoDaImagemPorId() {
        // Arrange
        productService.save(product);
        Path arquivoEsperado = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        // Act
        String caminhoEncontrado = productService.getImagePathById(1);
        // Assert
        assertNotNull(caminhoEncontrado);
        assertEquals(arquivoEsperado.toString(), caminhoEncontrado);
    }
}
