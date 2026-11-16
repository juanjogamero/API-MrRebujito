package mrRebujito.MrRebujito.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mrRebujito.MrRebujito.entity.Administrador;
import mrRebujito.MrRebujito.entity.Roles;
import mrRebujito.MrRebujito.repository.AdministradorRepository;
import mrRebujito.MrRebujito.security.JWTUtils;
@Service
public class AdministradorService {
    private final AdministradorRepository repo;
    private final PasswordEncoder encoder;
    private final JWTUtils jwt;
    public AdministradorService(AdministradorRepository repo,PasswordEncoder encoder,JWTUtils jwt){
        this.repo=repo;this.encoder=encoder;this.jwt=jwt;
    }
    @Transactional
    public Administrador saveAdministrador(Administrador data){
        data.setId(0);data.setVersion(0);data.setRol(Roles.ADMIN);
        data.setPassword(encoder.encode(data.getPassword()));
        return repo.save(data);
    }
    @Transactional
    public Administrador updateAdministrador(Administrador data){
        Administrador admin=jwt.userLogin();
        if(admin==null)return null;
        admin.setNombre(data.getNombre());admin.setFoto(data.getFoto());admin.setCorreo(data.getCorreo());
        admin.setTelefono(data.getTelefono());admin.setDireccion(data.getDireccion());
        admin.setPrimerApellido(data.getPrimerApellido());admin.setSegundoApellido(data.getSegundoApellido());
        return repo.save(admin);
    }
    public List<Administrador> getAllAdministradores(){return repo.findAll();}
    public Optional<Administrador> getAdministradorById(int id){return repo.findById(id);}
    public Optional<Administrador> findByUsername(String username){return repo.findByUsername(username);}
    @Transactional public boolean deleteAdministrador(){
        Administrador admin=jwt.userLogin();
        if(admin==null)return false;
        repo.delete(admin);return true;
    }
}
