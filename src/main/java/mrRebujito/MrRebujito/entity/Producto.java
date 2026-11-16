package mrRebujito.MrRebujito.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Producto extends DomainEntity {
    @NotBlank private String nombre;
    @NotNull @Enumerated(EnumType.STRING) private TipoAlimento tipoAlimento;
    @DecimalMin("0.0") private double precio;
    public Producto() {}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public TipoAlimento getTipoAlimento(){return tipoAlimento;}
    public void setTipoAlimento(TipoAlimento tipoAlimento){this.tipoAlimento=tipoAlimento;}
    public double getPrecio(){return precio;}
    public void setPrecio(Double precio){this.precio=precio;}
}
