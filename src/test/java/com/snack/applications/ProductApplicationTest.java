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

    // Dependências de ambiente (herdadas do ProductService)
    private final String CAMINHO_IMAGENS_ORIGEM_TESTE = "C:\\temp_testes\\";
    private final String CAMINHO_BANCO_IMAGENS_DESTINO = "C:\\Users\\aluno\\BancoImagens\\";

    // As classes reais que vamos integrar
    private ProductRepository productRepository;
    private ProductService productService;
    private ProductApplication productApplication; // A classe que estamos a testar

    // Dados de teste
    private Product product1;
    private Path arquivoImagemFalso1;
    private Path arquivoImagemFalso2; // Para o teste de update

    @BeforeEach
    void setUp() throws IOException {
        // 1. Criar o ambiente de ficheiros
        Files.createDirectories(Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE));
        Files.createDirectories(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO));

        arquivoImagemFalso1 = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "fake1.jpg");
        Files.deleteIfExists(arquivoImagemFalso1); // Garante que está limpo
        Files.createFile(arquivoImagemFalso1); // Cria C:\temp_testes\fake1.jpg

        arquivoImagemFalso2 = Paths.get(CAMINHO_IMAGENS_ORIGEM_TESTE + "fake2.png");
        Files.deleteIfExists(arquivoImagemFalso2); // Garante que está limpo

        // 2. Instanciar as dependências REAIS
        productRepository = new ProductRepository();
        productService = new ProductService(); // Usa o construtor vazio

        // 3. Instanciar a classe-alvo
        productApplication = new ProductApplication(productRepository, productService);

        // 4. Preparar dados de teste
        product1 = new Product(1, "Hot Dog", 10.4f, arquivoImagemFalso1.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        // Limpar os ficheiros criados
        Files.deleteIfExists(arquivoImagemFalso1);
        Files.deleteIfExists(arquivoImagemFalso2);
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg"));
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.png"));
        Files.deleteIfExists(Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "2.jpg"));
    }

    // --- TESTES DE INTEGRAÇÃO DO ProductApplication ---

    /**
     * Teste 1: "Listar todos os produtos do repositório"
     */
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

    /**
     * Teste 2: "Obter um produto por ID válido"
     */
    @Test
    public void deveObterProdutoPorIdValido() {
        // Arrange
        productRepository.append(product1); // Adiciona direto no repo para este teste

        // Act
        Product produtoEncontrado = productApplication.getById(1);

        // Assert
        assertNotNull(produtoEncontrado);
        assertEquals(1, produtoEncontrado.getId());
    }

    /**
     * Teste 3: "Retornar nulo ou erro ao tentar obter produto por ID inválido"
     * Este teste PROVA o bug do .get() no ProductRepository.
     */
    @Test
    public void deveLancarExcecaoAoObterProdutoPorIdInvalido() {
        // Arrange
        // (Repositório está vazio)

        // Act & Assert
        // A Aplicação chama o Repositório,
        // que usa .get() num Optional vazio
        assertThrows(NoSuchElementException.class, () -> {
            productApplication.getById(99);
        });
    }

    /**
     * Teste 4: "Verificar se um produto existe por ID válido"
     */
    @Test
    public void deveVerificarSeProdutoExistePorIdValido() {
        // Arrange
        productRepository.append(product1);

        // Act
        boolean resultado = productApplication.exists(1);

        // Assert
        assertTrue(resultado);
    }

    /**
     * Teste 5: "Retornar falso ao verificar a existência de um produto com ID inválido"
     */
    @Test
    public void deveRetornarFalsoAoVerificarProdutoComIdInvalido() {
        // Arrange
        // (Repositório está vazio)

        // Act
        boolean resultado = productApplication.exists(99);

        // Assert
        assertFalse(resultado);
    }

    /**
     * Teste 6: "Adicionar um novo produto e salvar sua imagem corretamente"
     * Este é o principal teste de integração do 'append'.
     */
    @Test
    public void deveAdicionarProdutoESalvarImagem() {
        // Arrange
        // (product1 está pronto no @BeforeEach)
        Path arquivoDestinoEsperado = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");

        // Act
        // O append coordena o Repo e o Service
        productApplication.append(product1);

        // Assert
        // 1. Verificamos se foi salvo no repositório (memória)
        assertTrue(productRepository.exists(1));

        // 2. Verificamos se foi salvo no serviço (disco)
        assertTrue(Files.exists(arquivoDestinoEsperado), "A imagem não foi salva no disco.");
    }

    /**
     * Teste 7: "Remover um produto existente e deletar sua imagem"
     * Este é o principal teste de integração do 'remove'.
     */
    @Test
    public void deveRemoverProdutoEDeletarImagem() {
        // Arrange
        // 1. Adiciona o produto e a imagem ao sistema
        productApplication.append(product1);

        // 2. Confirma que eles existem antes do teste
        Path arquivoParaDeletar = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertTrue(productRepository.exists(1), "Setup falhou: produto não foi adicionado ao repo.");
        assertTrue(Files.exists(arquivoParaDeletar), "Setup falhou: imagem não foi salva no disco.");

        // Act
        // O remove coordena o Repo e o Service
        productApplication.remove(1);

        // Assert
        // 1. Verificamos se foi removido do repositório (memória)
        assertFalse(productRepository.exists(1));

        // 2. Verificamos se foi removido do serviço (disco)
        assertFalse(Files.exists(arquivoParaDeletar), "A imagem não foi deletada do disco.");
    }

    /**
     * Teste 8: "Não alterar o sistema ao tentar remover um produto com ID inexistente"
     * Este teste PROVA o bug do .get() no ProductService.
     */
    @Test
    public void deveLancarExcecaoAoRemoverProdutoInexistente() {
        // Arrange
        // (Repositório está vazio)

        // Act & Assert
        // A Aplicação chama o Service,
        // que chama getImagePathById,
        // que usa .get() num Optional vazio
        assertThrows(NoSuchElementException.class, () -> {
            productApplication.remove(99);
        });
    }

    /**
     * Teste 9: "Atualizar um produto existente e substituir sua imagem"
     * Este é o principal teste de integração do 'update'.
     */
    @Test
    public void deveAtualizarProdutoESubstituirImagem() throws IOException {
        // Arrange
        // 1. Adiciona o produto original (cria "1.jpg")
        productApplication.append(product1);
        Path imagemAntiga = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.jpg");
        assertTrue(Files.exists(imagemAntiga), "Setup falhou: imagem antiga não foi criada.");

        // 2. Cria uma nova imagem de origem
        Files.createFile(arquivoImagemFalso2); // Cria "fake2.png"

        // 3. Cria o objeto com os dados de atualização
        Product dadosAtualizados = new Product(1, "Super Hot Dog", 20.0f, arquivoImagemFalso2.toString());
        Path imagemNova = Paths.get(CAMINHO_BANCO_IMAGENS_DESTINO + "1.png"); // Esperamos uma "1.png"

        // Act
        // O update coordena o Repo e o Service
        productApplication.update(1, dadosAtualizados);

        // Assert
        // 1. Verifica o repositório (memória)
        assertEquals("Super Hot Dog", productRepository.getById(1).getDescription());
        assertEquals(20.0f, productRepository.getById(1).getPrice());

        // 2. Verifica o serviço (disco)
        assertFalse(Files.exists(imagemAntiga), "A imagem antiga (1.jpg) não foi removida.");
        assertTrue(Files.exists(imagemNova), "A imagem nova (1.png) não foi criada.");
    }
}