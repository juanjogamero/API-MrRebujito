package mrRebujito.MrRebujito.service;

import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import mrRebujito.MrRebujito.entity.Actor;
import mrRebujito.MrRebujito.repository.ActorRepository;

@Service
public class ActorService implements UserDetailsService {
    private final ActorRepository repo;
    public ActorService(ActorRepository repo){this.repo=repo;}
    public Optional<Actor> findByUsername(String username){return repo.findByUsername(username);}
    @Override
    public UserDetails loadUserByUsername(String username){
        Actor actor=repo.findByUsername(username)
            .orElseThrow(()->new UsernameNotFoundException("Cuenta no encontrada"));
        if(actor.getRol()==null||actor.getPassword()==null)
            throw new UsernameNotFoundException("Cuenta no disponible");
        return new User(actor.getUsername(),actor.getPassword(),
            java.util.List.of(new SimpleGrantedAuthority(actor.getRol().name())));
    }
}
