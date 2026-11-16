package mrRebujito.MrRebujito.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class Actor extends DomainEntity {
    @NotBlank private String nombre;
    @URL private String foto;
    @NotBlank @Email private String correo;
    @Pattern(regexp="^[6-9][0-9]{8}$") private String telefono;
    private String direccion;
    @NotBlank @Column(nullable=false,unique=true) private String username;
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY) @NotBlank private String password;
    @Enumerated(EnumType.STRING) private Roles rol;

    public Actor() {}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public String getFoto(){return foto;}
    public void setFoto(String foto){this.foto=foto;}
    public String getCorreo(){return correo;}
    public void setCorreo(String correo){this.correo=correo;}
    public String getTelefono(){return telefono;}
    public void setTelefono(String telefono){this.telefono=telefono;}
    public String getDireccion(){return direccion;}
    public void setDireccion(String direccion){this.direccion=direccion;}
    public String getUsername(){return username;}
    public void setUsername(String username){this.username=username;}
    public String getPassword(){return password;}
    public void setPassword(String password){this.password=password;}
    public Roles getRol(){return rol;}
    public void setRol(Roles rol){this.rol=rol;}
}
