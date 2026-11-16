package mrRebujito.MrRebujito.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import mrRebujito.MrRebujito.entity.Administrador;
import mrRebujito.MrRebujito.repository.ActorRepository;
import mrRebujito.MrRebujito.service.AdministradorService;
@Configuration
public class AdminBootstrap {
    @Bean CommandLineRunner firstAdmin(ActorRepository actorRepo,AdministradorService service,
        @Value("${APP_ADMIN_USER:}")String username,@Value("${APP_ADMIN_PASSWORD:}")String password){
        return args->{
            if(username.isBlank()&&password.isBlank())return;
            if(username.isBlank()||password.length()<12)
                throw new IllegalArgumentException("APP_ADMIN_USER y APP_ADMIN_PASSWORD (12+ caracteres) obligatorios juntos");
            if(actorRepo.findByUsername(username).isPresent())return;
            Administrador admin=new Administrador();
            admin.setUsername(username);admin.setPassword(password);
            admin.setNombre("Administrador");admin.setPrimerApellido("Inicial");
            admin.setCorreo("admin@localhost.invalid");
            service.saveAdministrador(admin);
        };
    }
}
