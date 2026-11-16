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
import mrRebujito.MrRebujito.entity.Producto;
import mrRebujito.MrRebujito.service.CasetaService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/caseta")
@Tag(name="Casetas",description="Registro, datos públicos, socios, carta y solicitudes")
public class CasetaController {
    private final CasetaService service;
    public CasetaController(CasetaService service){this.service=service;}

    @GetMapping
    public List<CasetaPublicResponse> findAll(){
        return service.findAllCaseta().stream().map(CasetaPublicResponse::from).toList();
    }
    @GetMapping("/{id}")
    public CasetaPublicResponse findById(@PathVariable int id){
        return service.findCasetaById(id).map(CasetaPublicResponse::from)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Caseta no encontrada"));
    }
    @GetMapping("/carta/{id}")
    public List<Producto> cartaPublica(@PathVariable int id){return service.cartaPublica(id);}
    @GetMapping("/carta")
    public List<Producto> cartaPropia(){return service.getCartaCaseta();}
    @GetMapping("/socios")
    public List<SocioPublicResponse> socios(){
        return service.getAllSociosByCaseta().stream().map(SocioPublicResponse::from).toList();
    }
    @PostMapping
    public ResponseEntity<CasetaPublicResponse> registrar(@Valid @RequestBody Caseta caseta){
        Caseta guardada=service.saveCaseta(caseta);
        return ResponseEntity.created(URI.create("/caseta/"+guardada.getId())).body(CasetaPublicResponse.from(guardada));
    }
    @PutMapping
    public CasetaPublicResponse actualizar(@RequestBody Caseta datos){
        Caseta updated=service.updateCaseta(datos);
        if(updated==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Caseta no encontrada");
        return CasetaPublicResponse.from(updated);
    }
    @DeleteMapping
    public ResponseEntity<Void> borrar(){
        if(!service.deleteCaseta())throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/socios/{idSocio}")
    public ResponseEntity<Void> anadirSocio(@PathVariable int idSocio){
        service.addSocio(idSocio);return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/socios/{idSocio}")
    public ResponseEntity<Void> eliminarSocio(@PathVariable int idSocio){
        service.removeSocio(idSocio);return ResponseEntity.noContent().build();
    }
    @PostMapping("/solicitud/{ayuntamientoId}")
    public ResponseEntity<SolicitudResponse> solicitar(@PathVariable int ayuntamientoId){
        var solicitud=service.crearSolicitud(ayuntamientoId);
        return ResponseEntity.created(URI.create("/solicitud/"+solicitud.getId()))
            .body(SolicitudResponse.from(solicitud));
    }
}
