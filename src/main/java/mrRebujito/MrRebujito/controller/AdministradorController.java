package mrRebujito.MrRebujito.controller;

import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import mrRebujito.MrRebujito.entity.Administrador;
import mrRebujito.MrRebujito.service.AdministradorService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/administrador")
@Tag(name="Administración",description="Gestión restringida al rol ADMIN")
public class AdministradorController {
    private final AdministradorService service;
    public AdministradorController(AdministradorService service){this.service=service;}
    @PostMapping
    public ResponseEntity<String> crear(@Valid @RequestBody Administrador admin){
        Administrador created=service.saveAdministrador(admin);
        return ResponseEntity.created(URI.create("/administrador/"+created.getId()))
            .body("Administrador creado");
    }
    @PutMapping
    public ResponseEntity<String> actualizar(@RequestBody Administrador data){
        if(service.updateAdministrador(data)==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Cuenta no encontrada");
        return ResponseEntity.ok("Administrador actualizado");
    }
    @DeleteMapping
    public ResponseEntity<Void> borrar(){
        if(!service.deleteAdministrador())throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.noContent().build();
    }
}
