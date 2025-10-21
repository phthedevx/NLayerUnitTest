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
        Files.deleteIfExists(arquivoImagemFalso); // C:\temp_testes\fake.jpg
        Files.deleteIfExists(Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "nova_imagem.png")); // Limpa o novo
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg")); // Limpa o antigo
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.png")); // Limpa o novo
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
    @Test
    public void deveRetornarFalseAoSalvarProdutoComImagemInexistente() {
        // Arrange
        Path arquivoInexistente = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "imagem_fantasma.jpg");
        product.setImage(arquivoInexistente.toString()); // Sobrescrevemos o 'fakeImageFile' do @BeforeEach
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
        // 1. Salva a imagem original (fake.jpg -> 1.jpg)
        //    Isso simula o "produto existente".
        productService.save(product);
        Path oldImageFile = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertTrue(Files.exists(oldImageFile), "Setup falhou: O arquivo antigo (1.jpg) não foi criado.");

        // 2. Cria uma *nova* imagem de origem para a atualização
        //    Usamos .png para ter certeza que é um arquivo diferente.
        Path newSourceImage = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "nova_imagem.png");
        Files.createFile(newSourceImage); // Cria C:\temp_testes\nova_imagem.png

        // 3. Atualiza o objeto 'product' para apontar para a nova imagem
        product.setImage(newSourceImage.toString());

        // 4. Define o caminho de destino esperado para a nova imagem (com a nova extensão)
        Path newImageFile = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.png");

        // Act
        // Chama o método 'update' que queremos testar
        productService.update(product);

        // Assert
        // 1. O arquivo antigo (1.jpg) deve ter sido removido
        assertFalse(Files.exists(oldImageFile), "O arquivo de imagem antigo (1.jpg) não foi removido.");

        // 2. O arquivo novo (1.png) deve ter sido criado
        assertTrue(Files.exists(newImageFile), "O novo arquivo de imagem (1.png) não foi criado.");
    }
    @Test
    public void deveRemoverProdutoExistente() throws IOException {
        // Arrange
        // 1. Precisamos que o arquivo de imagem exista.
        //    Primeiro, salvamos o produto, o que copia
        //    "fake.jpg" (origem) para "1.jpg" (destino).
        productService.save(product);

        // 2. Definimos o caminho para o arquivo que DEVE ser removido
        Path arquivoParaRemover = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");

        // 3. Verificação (Assert) do Arrange:
        //    Garante que o arquivo a ser removido realmente existe ANTES da ação.
        assertTrue(Files.exists(arquivoParaRemover), "Setup falhou: O arquivo '1.jpg' não foi criado antes do teste de remoção.");

        // Act
        // Chamamos o métod de remoção que queremos testar
        productService.remove(1); // O ID é 1 (do 'product')

        // Assert
        // Verificamos se o métod 'remove' fez seu trabalho:
        // O arquivo "1.jpg" não deve mais existir.
        assertFalse(Files.exists(arquivoParaRemover), "O arquivo '1.jpg' não foi removido com sucesso.");
    }
    @Test
    public void deveObterCaminhoDaImagemPorId() {
        // Arrange
        // 1. Garante que o ficheiro "1.jpg" existe na pasta de destino
        productService.save(product);

        // 2. Define o caminho exato que esperamos que o método retorne
        Path arquivoEsperado = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");

        // Act
        // Chama o método que queremos testar
        String caminhoEncontrado = productService.getImagePathById(1);

        // Assert
        // Verifica se o caminho retornado é exatamente o que esperávamos
        assertNotNull(caminhoEncontrado);
        assertEquals(arquivoEsperado.toString(), caminhoEncontrado);
    }
}
