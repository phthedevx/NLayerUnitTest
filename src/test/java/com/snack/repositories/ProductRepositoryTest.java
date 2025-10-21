package com.snack.repositories;

import com.snack.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ProductRepositoryTest {
    private ProductRepository productRepository;
    private Product product1;

    @BeforeEach
    public void setup() {
        productRepository = new ProductRepository();
        product1 = new Product(1, "Hot Dog", 10.4f, "");
    }

    @Test
    public void verificarSeOProdutoEstaNoArray() {
        // Arrange
        productRepository.append(product1);
        // Act
        Product productId1 = productRepository.getById(1);
        // Assert
        assertNotNull(productId1);
    }
    @Test
    public void deveRecuperarProdutoPorId(){
        // Arrange
        productRepository.append(product1);
        // Act
        Product productRetornado = productRepository.getById(1);
        // Assert
        assertEquals(product1.getId(), productRetornado.getId());
    }
    @Test
    public void deveConfirmarProdutoNoArrayList(){
        // Arrange
        productRepository.append(product1);
        //Act
        boolean resultado = productRepository.exists(product1.getId());
        //Assert
        assertTrue(resultado);
    }
    @Test
    public void deveRemoverProdutoDoRepositorio(){
        // Arrange
        productRepository.append(product1);
        //Act
        productRepository.remove(product1.getId());
        //Assert
        assertFalse(productRepository.exists(product1.getId()));
    }
    @Test
    public void deveAtualizarProduto(){
        // Arrange
        productRepository.append(product1);
        Product p = new Product(1, "Cachorro Quente", 10.4f, "");
        //Act
        productRepository.update(product1.getId(), p);
        //Assert
        assertEquals(p.getDescription(), product1.getDescription());
    }
    @Test
    public void deveRecuperarProdutosArmazenados(){
        // Arrange
        Product p = new Product(2, "Cachorro Quente", 12.4f, "");
        productRepository.append(product1);
        productRepository.append(p);
        //Act
        List<Product> todosOsProdutos = productRepository.getAll();
        // Assert
        assertNotNull(todosOsProdutos);
        assertEquals(2, todosOsProdutos.size());
    }
    @Test
    public void verificaComportamentoRemoverProdutoInexistente(){
        //Arrange
        productRepository.append(product1);
        //Act
        productRepository.remove(2);
    }


    @Test
    public void testarAtualizarProdutoInexistente(){
        //Arrange
        Product p = new Product(1, "Cachorro Quente", 10.4f, "");
        //Act e Assert
        assertThrows(NoSuchElementException.class, () -> {
            productRepository.update(2, p);
        });
    }
    @Test
    public void verificaDuplicacaoDeId(){
        //Arrange
        productRepository.append(product1);
        Product p = new Product(1, "Cachorro Quente", 10.4f, "");
        productRepository.append(p);
        //Act
        boolean result = product1.getId() == p.getId();
        //Assert
        assertTrue(result);
    }
    @Test
    public void confirmaRetornoListaVaziaAoInicializar(){
        //Arrange e Act
        List<Product>  listavazia = productRepository.getAll();
        //Assert
        assertTrue(listavazia.isEmpty());
    }

}
