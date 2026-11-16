package mrRebujito.MrRebujito.controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import mrRebujito.MrRebujito.dto.*;
import mrRebujito.MrRebujito.entity.Caseta;
import mrRebujito.MrRebujito.entity.Socio;
import mrRebujito.MrRebujito.service.SocioService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/socio")
@Tag(name="Socios",description="Registro, perfil y casetas asociadas")
public class SocioController {
    private final SocioService service;
    public SocioController(SocioService service){this.service=service;}
    @PostMapping
    public ResponseEntity<SocioPublicResponse> registrar(@Valid @RequestBody Socio socio){
        Socio creado=service.saveSocio(socio);
        return ResponseEntity.created(URI.create("/socio/"+creado.getId())).body(SocioPublicResponse.from(creado));
    }
    @PutMapping
    public SocioPublicResponse actualizar(@RequestBody Socio data){
        Socio actual=service.updateSocio(data);
        if(actual==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Socio no encontrado");
        return SocioPublicResponse.from(actual);
    }
    @DeleteMapping
    public ResponseEntity<Void> borrar(){
        if(!service.deleteSocio())throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/misCasetas")
    public List<CasetaPublicResponse> casetas(){
        return service.getMisCasetas().stream().map(CasetaPublicResponse::from).toList();
    }
}
