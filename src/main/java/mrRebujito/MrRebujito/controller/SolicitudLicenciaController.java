package mrRebujito.MrRebujito.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import mrRebujito.MrRebujito.dto.SolicitudResponse;
import mrRebujito.MrRebujito.service.SolicitudLicenciaService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/solicitud")
@Tag(name="Licencias",description="Solicitudes de casetas, revisión municipal y cupos")
public class SolicitudLicenciaController {
    private final SolicitudLicenciaService service;
    public SolicitudLicenciaController(SolicitudLicenciaService service){this.service=service;}
    @GetMapping({"/caseta","/Caseta"})
    public List<SolicitudResponse> mias(){
        return service.getAllSolicitudesByCaseta().stream().map(SolicitudResponse::from).toList();
    }
    @GetMapping({"/ayuntamiento","/Ayuntamiento"})
    public List<SolicitudResponse> municipales(){
        return service.getAllSolicitudesByAyuntamiento().stream().map(SolicitudResponse::from).toList();
    }
    @GetMapping("/{id}")
    public SolicitudResponse detalle(@PathVariable int id){
        return service.findOwnedById(id).map(SolicitudResponse::from)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Solicitud no encontrada"));
    }
    @PutMapping("/aceptar/{id}")
    public ResponseEntity<String> aceptar(@PathVariable int id){
        if(!service.aceptarSolicitud(id))throw new ResponseStatusException(HttpStatus.CONFLICT,
            "Solicitud inexistente, no pendiente o de otro ayuntamiento");
        return ResponseEntity.ok("Licencia aprobada");
    }
    @PutMapping("/rechazar/{id}")
    public ResponseEntity<String> rechazar(@PathVariable int id){
        if(!service.rechazarSolicitud(id))throw new ResponseStatusException(HttpStatus.CONFLICT,
            "Solicitud inexistente, no pendiente o de otro ayuntamiento");
        return ResponseEntity.ok("Licencia rechazada");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id){
        if(!service.deleteSolicitud(id))throw new ResponseStatusException(HttpStatus.CONFLICT,
            "Solo se pueden retirar solicitudes propias y pendientes");
        return ResponseEntity.noContent().build();
    }
}
