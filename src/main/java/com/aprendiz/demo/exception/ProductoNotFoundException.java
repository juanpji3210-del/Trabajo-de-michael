package com.aprendiz.demo.exception;

public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(Long id) {
        super("El producto con id " + id + " no existe");
    }
}
