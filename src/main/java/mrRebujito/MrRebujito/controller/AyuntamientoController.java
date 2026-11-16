package mrRebujito.MrRebujito.controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import mrRebujito.MrRebujito.dto.AyuntamientoPublicResponse;
import mrRebujito.MrRebujito.entity.Ayuntamiento;
import mrRebujito.MrRebujito.service.AyuntamientoService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/ayuntamiento")
@Tag(name="Ayuntamientos",description="Cupos municipales y gestión de cuentas")
public class AyuntamientoController {
    private final AyuntamientoService service;
    public AyuntamientoController(AyuntamientoService service){this.service=service;}

    @GetMapping
    public List<AyuntamientoPublicResponse> listar(){
        return service.findAllAyuntamiento().stream().map(AyuntamientoPublicResponse::from).toList();
    }
    @GetMapping("/{id}")
    public AyuntamientoPublicResponse obtener(@PathVariable int id){
        return service.findAyuntamientoById(id).map(AyuntamientoPublicResponse::from)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Ayuntamiento no encontrado"));
    }
    @PostMapping
    public ResponseEntity<AyuntamientoPublicResponse> crear(@Valid @RequestBody Ayuntamiento a){
        Ayuntamiento created=service.saveAyuntamiento(a);
        return ResponseEntity.created(URI.create("/ayuntamiento/"+created.getId()))
            .body(AyuntamientoPublicResponse.from(created));
    }
    @PutMapping
    public AyuntamientoPublicResponse actualizar(@RequestBody Ayuntamiento datos){
        Ayuntamiento updated=service.updateAyuntamiento(datos);
        if(updated==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return AyuntamientoPublicResponse.from(updated);
    }
    @DeleteMapping
    public ResponseEntity<Void> borrar(){
        if(!service.deleteAyuntamiento())throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.noContent().build();
    }
}
