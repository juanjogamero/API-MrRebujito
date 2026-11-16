package mrRebujito.MrRebujito.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mrRebujito.MrRebujito.entity.*;
import mrRebujito.MrRebujito.repository.*;
import mrRebujito.MrRebujito.security.JWTUtils;

@Service
public class SocioService {
    private final SocioRepository socios;
    private final CasetaRepository casetas;
    private final JWTUtils jwt;
    private final PasswordEncoder encoder;
    public SocioService(SocioRepository socios,CasetaRepository casetas,JWTUtils jwt,PasswordEncoder encoder){
        this.socios=socios;this.casetas=casetas;this.jwt=jwt;this.encoder=encoder;
    }
    public Optional<Socio> findSocioById(int id){return socios.findById(id);}
    public Optional<Socio> findByUsername(String username){return socios.findByUsername(username);}
    public List<Socio> findAllSocios(){return socios.findAll();}
    public Optional<Socio> findSocioByUsername(String username){return socios.findByUsername(username);}
    @Transactional
    public Socio saveSocio(Socio socio){
        socio.setId(0);socio.setVersion(0);socio.setRol(Roles.SOCIO);
        socio.setPassword(encoder.encode(socio.getPassword()));
        return socios.save(socio);
    }
    @Transactional
    public Socio updateSocio(Socio data){
        Socio session=jwt.userLogin();
        if(session==null)return null;
        Socio s=socios.findById(session.getId()).orElse(null);
        if(s==null)return null;
        s.setNombre(data.getNombre());s.setPrimerApellido(data.getPrimerApellido());
        s.setSegundoApellido(data.getSegundoApellido());s.setFoto(data.getFoto());
        s.setCorreo(data.getCorreo());s.setDireccion(data.getDireccion());s.setTelefono(data.getTelefono());
        return socios.save(s);
    }
    @Transactional
    public boolean deleteSocio(){
        Socio session=jwt.userLogin();
        if(session==null)return false;
        for(Caseta c:casetas.findAllBySocioId(session.getId())){
            c.getSocios().removeIf(s->s.getId()==session.getId());
            casetas.save(c);
        }
        socios.deleteById(session.getId());
        return true;
    }
    @Transactional(readOnly=true)
    public List<Caseta> getMisCasetas(){
        Socio session=jwt.userLogin();
        if(session==null)return List.of();
        return casetas.findAllBySocioId(session.getId());
    }
}
