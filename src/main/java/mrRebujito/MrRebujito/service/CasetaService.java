package mrRebujito.MrRebujito.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import mrRebujito.MrRebujito.entity.*;
import mrRebujito.MrRebujito.repository.*;
import mrRebujito.MrRebujito.security.JWTUtils;

@Service
public class CasetaService {
    private final CasetaRepository casetas;
    private final AyuntamientoRepository ayuntamientos;
    private final SolicitudLicenciaRepository licencias;
    private final SocioRepository socios;
    private final PasswordEncoder encoder;
    private final JWTUtils jwt;
    public CasetaService(CasetaRepository casetas,AyuntamientoRepository ayuntamientos,
       SolicitudLicenciaRepository licencias,SocioRepository socios,PasswordEncoder encoder,JWTUtils jwt){
        this.casetas=casetas;this.ayuntamientos=ayuntamientos;this.licencias=licencias;
        this.socios=socios;this.encoder=encoder;this.jwt=jwt;
    }
    public Optional<Caseta> findCasetaById(int id){return casetas.findById(id);}
    public Optional<Caseta> findByUsername(String username){return casetas.findByUsername(username);}
    public List<Caseta> findAllCaseta(){return casetas.findAll();}
    @Transactional(readOnly=true)
    public List<Socio> getAllSociosByCaseta(){
        Caseta user=jwt.userLogin();
        if(user==null)return List.of();
        return new ArrayList<>(casetas.findById(user.getId()).orElseThrow().getSocios());
    }
    @Transactional(readOnly=true)
    public List<Producto> getCartaCaseta(){
        Caseta user=jwt.userLogin();
        if(user==null)return List.of();
        return new ArrayList<>(casetas.findById(user.getId()).orElseThrow().getProductos());
    }
    @Transactional(readOnly=true)
    public List<Producto> cartaPublica(int id){
        return new ArrayList<>(casetas.findById(id)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Caseta no encontrada")).getProductos());
    }
    @Transactional
    public Caseta saveCaseta(Caseta c){
        c.setId(0);c.setVersion(0);c.setRol(Roles.CASETA);
        c.setPassword(encoder.encode(c.getPassword()));
        c.setSocios(new ArrayList<>());c.setProductos(new ArrayList<>());c.setSolicitudesLicencia(new ArrayList<>());
        return casetas.save(c);
    }
    @Transactional
    public Caseta updateCaseta(Caseta data){
        Caseta session=jwt.userLogin();
        if(session==null)return null;
        Caseta c=casetas.findByIdForUpdate(session.getId()).orElse(null);
        if(c==null)return null;
        if(data.getAforo()<c.getSocios().size())
            throw new IllegalStateException("El aforo no puede ser inferior al número de socios");
        c.setNombre(data.getNombre());c.setCorreo(data.getCorreo());c.setTelefono(data.getTelefono());
        c.setDireccion(data.getDireccion());c.setFoto(data.getFoto());c.setRazonS(data.getRazonS());
        c.setAforo(data.getAforo());c.setPublica(data.isPublica());
        return casetas.save(c);
    }
    @Transactional
    public boolean deleteCaseta(){
        Caseta session=jwt.userLogin();
        if(session==null)return false;
        Caseta c=casetas.findByIdForUpdate(session.getId()).orElseThrow();
        if(!c.getSolicitudesLicencia().isEmpty())
            throw new IllegalStateException("La caseta tiene historial de licencias; no se puede eliminar");
        c.getSocios().clear();c.getProductos().clear();
        casetas.saveAndFlush(c);casetas.delete(c);return true;
    }
    @Transactional
    public SolicitudLicencia crearSolicitud(int ayuntamientoId){
        Caseta session=jwt.userLogin();
        if(session==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Caseta c=casetas.findByIdForUpdate(session.getId()).orElseThrow();
        Ayuntamiento a=ayuntamientos.findById(ayuntamientoId)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Ayuntamiento no encontrado"));
        long repetidas=licencias.countOpenByCasetaAndAyuntamiento(c.getId(),ayuntamientoId,
            List.of(EstadoLicencia.PENDIENTE,EstadoLicencia.APROBADA));
        if(repetidas>0)throw new IllegalStateException("Ya existe una solicitud pendiente o aprobada para este ayuntamiento");
        SolicitudLicencia solicitud=new SolicitudLicencia();
        solicitud.setAyuntamiento(a);solicitud.setEstadoLicencia(EstadoLicencia.PENDIENTE);
        solicitud=licencias.save(solicitud);
        c.getSolicitudesLicencia().add(solicitud);
        casetas.save(c);
        return solicitud;
    }
    @Transactional
    public Caseta addSocio(int id){
        Caseta session=jwt.userLogin();
        if(session==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Caseta c=casetas.findByIdForUpdate(session.getId()).orElseThrow();
        Socio socio=socios.findById(id)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Socio no encontrado"));
        if(c.getSocios().stream().anyMatch(s->s.getId()==id))
            throw new IllegalStateException("El socio ya pertenece a esta caseta");
        if(c.getSocios().size()>=c.getAforo())
            throw new IllegalStateException("La caseta ha alcanzado su aforo");
        c.getSocios().add(socio);
        return casetas.save(c);
    }
    @Transactional
    public Caseta removeSocio(int id){
        Caseta session=jwt.userLogin();
        if(session==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Caseta c=casetas.findByIdForUpdate(session.getId()).orElseThrow();
        if(!c.getSocios().removeIf(s->s.getId()==id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Este socio no pertenece a la caseta");
        return casetas.save(c);
    }
}
