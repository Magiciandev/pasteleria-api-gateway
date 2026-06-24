package com.example.producto_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.producto_service.model.DetalleProducto;
import com.example.producto_service.model.Producto;
import com.example.producto_service.repository.ProductoRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final ProductoRepository productoRepository;

    public DataLoader(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() > 0) {
            logger.info("DataLoader: ya existen productos, se omite la carga inicial");
            return;
        }

        logger.info("DataLoader: cargando productos y detalles de producto iniciales");

        crearProducto("Torta Tres Leches", 15000.0, 10,
                "Torta clásica elaborada con tres tipos de leche: leche condensada, leche evaporada y crema de leche. Suave y deliciosa.",
                true, true, false, true, "Lácteos, Huevos, Gluten", "Refrigerar entre 2-8°C", "Porciones");

        crearProducto("Torta de Chocolate", 18000.0, 8,
                "Torta de chocolate puro con interior humedo y cobertura de ganache. Ideal para amantes del chocolate.",
                true, true, false, true, "Lácteos, Huevos, Gluten, Soja", "Refrigerar entre 2-8°C", "Porciones");

        crearProducto("Cheesecake de Frambuesa", 12000.0, 15,
                "Fresco y delicioso cheesecake con base de galleta y cobertura de frambuesa fresca.",
                true, true, false, true, "Lácteos, Huevos, Gluten", "Refrigerar entre 2-8°C", "Porciones");

        crearProducto("Pie de Limón", 10000.0, 20,
                "Pie tradicional de limón con merengue, acidez balanceada y dulzor perfecto.",
                true, true, false, true, "Lácteos, Huevos, Gluten", "Refrigerar entre 2-8°C", "Porciones");

        crearProducto("Kuchen de Nuez", 14000.0, 12,
                "Kuchen alemán elaborado con nueces frescas, suave y aromático.",
                true, true, true, true, "Lácteos, Huevos, Gluten, Frutos Secos (Nueces)", "Mantener en lugar fresco y seco", "Porciones");

        crearProducto("Cupcake de Vainilla", 2000.0, 50,
                "Pequeños y esponjosos cupcakes de vainilla pura con cobertura de buttercream.",
                true, true, false, true, "Lácteos, Huevos, Gluten", "Consumir dentro de 3 días", "Unidades");

        crearProducto("Cupcake Red Velvet", 2500.0, 40,
                "Cupcakes elegantes con sabor de terciopelo rojo y cobertura de queso crema.",
                true, true, false, true, "Lácteos, Huevos, Gluten", "Consumir dentro de 3 días", "Unidades");

        crearProducto("Galletas con Chips de Chocolate", 500.0, 100,
                "Galletas crujientes cargadas de chips de chocolate puro de calidad.",
                true, false, false, true, "Lácteos, Gluten, Soja", "Mantener en frasco hermético", "Bolsa");

        crearProducto("Pan Amasado (Docena)", 3000.0, 30,
                "Pan casero amasado a mano, esponjoso y con miga abierta. Tradicional chileno.",
                false, false, false, true, "Gluten", "Consumir el mismo día", "Docena");

        crearProducto("Empanada de Pino", 2000.0, 60,
                "Empanada rellena de pino jugoso con carne molida, cebolla y especias.",
                false, true, false, true, "Gluten, Soja", "Servir caliente", "Unidades");

        logger.info("DataLoader: carga inicial de productos completada, total={}", productoRepository.count());
    }

    private void crearProducto(String nombre, Double precio, Integer stock, String descripcion,
                                boolean lactosa, boolean huevos, boolean frutosSecos, boolean gluten,
                                String alergenos, String instrucciones, String presentacion) {
        DetalleProducto detalle = new DetalleProducto();
        detalle.setDescripcion(descripcion);
        detalle.setContieneLactosa(lactosa);
        detalle.setContieneHuevos(huevos);
        detalle.setContieneFrutosSecos(frutosSecos);
        detalle.setContieneGluten(gluten);
        detalle.setAlergenos(alergenos);
        detalle.setInstruccionesAlmacenamiento(instrucciones);
        detalle.setPresentacion(presentacion);

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setDetalleProducto(detalle);

        productoRepository.save(producto);
    }
}
