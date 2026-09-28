package com.aprendiz.demo.repository;

import com.aprendiz.demo.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repository;

    @Test
    void findByNombreContainingIgnoreCase_ignoraMayusculas() {
        repository.save(new Producto("Camiseta Roja", "Algodón", new BigDecimal("35000"), 10));
        repository.save(new Producto("Pantalón", "Jean", new BigDecimal("80000"), 5));

        List<Producto> resultado = repository.findByNombreContainingIgnoreCase("camiseta");

        assertEquals(1, resultado.size());
        assertEquals("Camiseta Roja", resultado.get(0).getNombre());
    }

    @Test
    void findByNombreContainingIgnoreCase_sinCoincidencias_retornaListaVacia() {
        repository.save(new Producto("Gorra", null, new BigDecimal("20000"), 3));

        assertTrue(repository.findByNombreContainingIgnoreCase("zapato").isEmpty());
    }
}
