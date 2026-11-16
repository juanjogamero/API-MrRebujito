package mrRebujito.MrRebujito.service;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import mrRebujito.MrRebujito.entity.*;
import mrRebujito.MrRebujito.repository.*;
import mrRebujito.MrRebujito.security.JWTUtils;

@Service
public class SolicitudLicenciaService {
    private final SolicitudLicenciaRepository repo;
    private final AyuntamientoRepository ayuntamientos;
    private final CasetaRepository casetas;
    private final JWTUtils jwt;
    public SolicitudLicenciaService(SolicitudLicenciaRepository repo,AyuntamientoRepository ayuntamientos,
                                  CasetaRepository casetas,JWTUtils jwt){
        this.repo=repo;this.ayuntamientos=ayuntamientos;this.casetas=casetas;this.jwt=jwt;
    }
    public Optional<SolicitudLicencia> findSolicitudById(int id){return repo.findById(id);}
    @Transactional(readOnly=true)
    public List<SolicitudLicencia> getAllSolicitudesByCaseta(){
        Caseta c=jwt.userLogin();
        return c==null?List.of():repo.findAllByCasetaId(c.getId());
    }
    public List<SolicitudLicencia> getAllSolicitudesByAyuntamiento(){
        Ayuntamiento a=jwt.userLogin();
        return a==null?List.of():repo.findAllByAyuntamiento(a);
    }
    public Optional<SolicitudLicencia> findOwnedById(int id){
        Optional<SolicitudLicencia> found=repo.findById(id);
        if(found.isEmpty())return found;
        Actor user=jwt.userLogin();
        if(user instanceof Ayuntamiento a&&found.get().getAyuntamiento().getId()==a.getId())return found;
        if(user instanceof Caseta c&&repo.countByCasetaAndSolicitud(c.getId(),id)>0)return found;
        return Optional.empty();
    }
    @Transactional
    public boolean deleteSolicitud(int id){
        Caseta actor=jwt.userLogin();
        if(actor==null)return false;
        Caseta c=casetas.findByIdForUpdate(actor.getId()).orElseThrow();
        SolicitudLicencia s=repo.findByIdForUpdate(id).orElse(null);
        if(s==null||s.getEstadoLicencia()!=EstadoLicencia.PENDIENTE)return false;
        if(c.getSolicitudesLicencia().stream().noneMatch(item->item.getId()==id))return false;
        c.getSolicitudesLicencia().removeIf(item->item.getId()==id);
        casetas.saveAndFlush(c);repo.delete(s);return true;
    }
    @Transactional
    public boolean aceptarSolicitud(int id){return cambiarEstado(id,EstadoLicencia.APROBADA);}
    @Transactional
    public boolean rechazarSolicitud(int id){return cambiarEstado(id,EstadoLicencia.RECHAZADA);}
    private boolean cambiarEstado(int id,EstadoLicencia newState){
        Ayuntamiento actor=jwt.userLogin();
        if(actor==null)return false;
        // El bloqueo municipal serializa aprobaciones simultáneas y protege el cupo.
        Ayuntamiento a=ayuntamientos.findByIdForUpdate(actor.getId()).orElse(null);
        if(a==null)return false;
        SolicitudLicencia s=repo.findByIdForUpdate(id).orElse(null);
        if(s==null||s.getAyuntamiento().getId()!=a.getId()||s.getEstadoLicencia()!=EstadoLicencia.PENDIENTE)
            return false;
        if(newState==EstadoLicencia.APROBADA&&
            repo.countByAyuntamientoIdAndEstadoLicencia(a.getId(),EstadoLicencia.APROBADA)>=a.getLicenciaMax())
            throw new IllegalStateException("El ayuntamiento ya ha alcanzado el cupo de licencias aprobadas");
        s.setEstadoLicencia(newState);repo.save(s);return true;
    }
}
