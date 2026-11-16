package mrRebujito.MrRebujito.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mrRebujito.MrRebujito.entity.Ayuntamiento;
import mrRebujito.MrRebujito.entity.EstadoLicencia;
import mrRebujito.MrRebujito.entity.Roles;
import mrRebujito.MrRebujito.repository.AyuntamientoRepository;
import mrRebujito.MrRebujito.repository.SolicitudLicenciaRepository;
import mrRebujito.MrRebujito.security.JWTUtils;

@Service
public class AyuntamientoService {
    private final AyuntamientoRepository repo;
    private final SolicitudLicenciaRepository licencias;
    private final JWTUtils jwt;
    private final PasswordEncoder encoder;
    public AyuntamientoService(AyuntamientoRepository repo,SolicitudLicenciaRepository licencias,
                               JWTUtils jwt,PasswordEncoder encoder){
        this.repo=repo;this.licencias=licencias;this.jwt=jwt;this.encoder=encoder;
    }
    public Optional<Ayuntamiento> findByUsername(String username){return repo.findByUsername(username);}
    public Optional<Ayuntamiento> findAyuntamientoById(int id){return repo.findById(id);}
    public List<Ayuntamiento> findAllAyuntamiento(){return repo.findAll();}
    @Transactional
    public Ayuntamiento saveAyuntamiento(Ayuntamiento a){
        a.setId(0);a.setVersion(0);a.setRol(Roles.AYUNTAMIENTO);
        a.setPassword(encoder.encode(a.getPassword()));
        return repo.save(a);
    }
    public long getLicenciasAprobadas(int id){return licencias.countByAyuntamientoIdAndEstadoLicencia(id,EstadoLicencia.APROBADA);}
    @Transactional
    public Ayuntamiento updateAyuntamiento(Ayuntamiento updated){
        Ayuntamiento session=jwt.userLogin();
        if(session==null)return null;
        Ayuntamiento a=repo.findByIdForUpdate(session.getId()).orElse(null);
        if(a==null)return null;
        if(updated.getLicenciaMax()<getLicenciasAprobadas(a.getId()))
            throw new IllegalStateException("No se puede reducir el cupo por debajo de las licencias aprobadas");
        a.setNombre(updated.getNombre());a.setFoto(updated.getFoto());a.setCorreo(updated.getCorreo());
        a.setTelefono(updated.getTelefono());a.setDireccion(updated.getDireccion());
        a.setLicenciaMax(updated.getLicenciaMax());
        return repo.save(a);
    }
    @Transactional
    public boolean deleteAyuntamiento(){
        Ayuntamiento a=jwt.userLogin();
        if(a==null)return false;
        if(licencias.countByAyuntamientoId(a.getId())>0)
            throw new IllegalStateException("No se puede borrar un ayuntamiento con historial de licencias");
        repo.deleteById(a.getId());return true;
    }
}
