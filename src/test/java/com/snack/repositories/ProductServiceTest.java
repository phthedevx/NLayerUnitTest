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
        //Instancia o serviço. Ele vai usar o caminho hardcoded.
        productService = new ProductService();
        //Garante que as pastas de teste existam
        Files.createDirectories(Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE));
        Files.createDirectories(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO));

        //Cria um arquivo de imagem falso na ORIGEM
        arquivoImagemFalso = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "fake.jpg");
        if (!Files.exists(arquivoImagemFalso)) {
            Files.createFile(arquivoImagemFalso);
        }

        //Cria o produto que aponta para o arquivo de origem FALSO
        product = new Product(1, "Produto Teste", 10f, arquivoImagemFalso.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(arquivoImagemFalso);
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg"));
    }

    @Test
    public void deveSalvarProdutoComImagemValida() {
        // Arrange
        // (Feito no @BeforeEach)
        // O caminho de destino esperado (baseado no código hardcoded)
        Path arquivoDestinoEsperado = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");

        // Act
        // Chamamos o método que queremos testar
        boolean resultado = productService.save(product);

        // Assert
        // ESTA LINHA VAI FALHAR, porque 'resultado' será 'false'
        assertTrue(resultado, "O método save() deveria ter retornado true, mas retornou false");

        // ESTA LINHA TAMBÉM VAI FALHAR, porque o arquivo de destino nunca foi criado
        assertTrue(Files.exists(arquivoDestinoEsperado), "O arquivo de destino não foi criado");
    }
}
