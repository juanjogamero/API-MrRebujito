package mrRebujito.MrRebujito.controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import mrRebujito.MrRebujito.entity.Producto;
import mrRebujito.MrRebujito.service.ProductoService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/producto")
@Tag(name="Productos",description="Cartas de casetas y operaciones de su propietario")
public class ProductoController {
    private final ProductoService service;
    public ProductoController(ProductoService service){this.service=service;}
    @GetMapping
    public List<Producto> listar(){return service.findAllProducto();}
    @GetMapping("/{id}")
    public Producto detalle(@PathVariable int id){
        return service.findProductoById(id)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado"));
    }
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto){
        Producto p=service.saveProducto(producto);
        return ResponseEntity.created(URI.create("/producto/"+p.getId())).body(p);
    }
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable int id,@Valid @RequestBody Producto producto){
        return service.updateProducto(id,producto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable int id){
        service.deleteById(id);return ResponseEntity.noContent().build();
    }
}
