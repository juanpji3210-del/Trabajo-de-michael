package com.aprendiz.demo.service;

import com.aprendiz.demo.dto.ProductoRequest;
import com.aprendiz.demo.dto.ProductoResponse;
import com.aprendiz.demo.exception.ProductoNotFoundException;
import com.aprendiz.demo.model.Producto;
import com.aprendiz.demo.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    private Producto crearProducto(Long id, String nombre) {
        Producto producto = new Producto(nombre, "Descripción", new BigDecimal("10000"), 5);
        producto.setId(id);
        return producto;
    }

    private ProductoRequest crearRequest() {
        return new ProductoRequest("Camiseta", "Algodón", new BigDecimal("35000"), 10);
    }

    @Test
    void listar_sinFiltro_retornaTodosLosProductos() {
        when(repository.findAll()).thenReturn(List.of(crearProducto(1L, "A"), crearProducto(2L, "B")));

        List<ProductoResponse> resultado = service.listar(null);

        assertEquals(2, resultado.size());
        verify(repository, never()).findByNombreContainingIgnoreCase(any());
    }

    @Test
    void listar_conNombreEnBlanco_retornaTodosLosProductos() {
        when(repository.findAll()).thenReturn(List.of(crearProducto(1L, "A")));

        List<ProductoResponse> resultado = service.listar("   ");

        assertEquals(1, resultado.size());
    }

    @Test
    void listar_conNombre_usaLaBusquedaPorNombre() {
        when(repository.findByNombreContainingIgnoreCase("cami"))
                .thenReturn(List.of(crearProducto(1L, "Camiseta")));

        List<ProductoResponse> resultado = service.listar("  cami ");

        assertEquals(1, resultado.size());
        assertEquals("Camiseta", resultado.get(0).nombre());
        verify(repository, never()).findAll();
    }

    @Test
    void buscarPorId_existente_retornaElProducto() {
        when(repository.findById(1L)).thenReturn(Optional.of(crearProducto(1L, "Camiseta")));

        ProductoResponse resultado = service.buscarPorId(1L);

        assertEquals(1L, resultado.id());
        assertEquals("Camiseta", resultado.nombre());
    }

    @Test
    void buscarPorId_inexistente_lanzaProductoNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ProductoNotFoundException ex =
                assertThrows(ProductoNotFoundException.class, () -> service.buscarPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void crear_guardaElProductoYRetornaLaRespuesta() {
        when(repository.save(any(Producto.class))).thenAnswer(invocacion -> {
            Producto guardado = invocacion.getArgument(0);
            guardado.setId(1L);
            return guardado;
        });

        ProductoResponse resultado = service.crear(crearRequest());

        assertEquals(1L, resultado.id());
        assertEquals("Camiseta", resultado.nombre());
        assertEquals(new BigDecimal("35000"), resultado.precio());
        assertEquals(10, resultado.stock());
    }

    @Test
    void actualizar_existente_modificaLosCampos() {
        when(repository.findById(1L)).thenReturn(Optional.of(crearProducto(1L, "Viejo")));
        when(repository.save(any(Producto.class))).then(returnsFirstArg());

        ProductoResponse resultado = service.actualizar(1L, crearRequest());

        assertEquals("Camiseta", resultado.nombre());
        assertEquals("Algodón", resultado.descripcion());
        assertEquals(10, resultado.stock());
    }

    @Test
    void actualizar_inexistente_lanzaExcepcionYNoGuarda() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        ProductoRequest request = crearRequest();

        assertThrows(ProductoNotFoundException.class, () -> service.actualizar(99L, request));

        verify(repository, never()).save(any());
    }

    @Test
    void eliminar_existente_eliminaElProducto() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void eliminar_inexistente_lanzaExcepcionYNoElimina() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ProductoNotFoundException.class, () -> service.eliminar(99L));

        verify(repository, never()).deleteById(any());
    }
}
