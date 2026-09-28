package com.aprendiz.demo.service;

import com.aprendiz.demo.dto.ProductoRequest;
import com.aprendiz.demo.dto.ProductoResponse;
import com.aprendiz.demo.exception.ProductoNotFoundException;
import com.aprendiz.demo.model.Producto;
import com.aprendiz.demo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(String nombre) {
        List<Producto> productos = (nombre == null || nombre.isBlank())
                ? repository.findAll()
                : repository.findByNombreContainingIgnoreCase(nombre.trim());
        return productos.stream().map(ProductoResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        return ProductoResponse.desde(obtenerEntidad(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto(
                request.nombre(), request.descripcion(), request.precio(), request.stock());
        return ProductoResponse.desde(repository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = obtenerEntidad(id);
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        return ProductoResponse.desde(repository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ProductoNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private Producto obtenerEntidad(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
    }
}
