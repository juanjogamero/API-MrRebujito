package mrRebujito.MrRebujito.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Caseta extends Actor {
    @NotBlank @Column(unique=true) private String razonS;
    @Min(0) private int aforo;
    private boolean publica;
    @JsonIgnore @ManyToMany private List<Socio> socios=new ArrayList<>();
    @JsonIgnore @OneToMany private List<SolicitudLicencia> solicitudesLicencia=new ArrayList<>();
    @JsonIgnore @ManyToMany private List<Producto> productos=new ArrayList<>();
    public Caseta() {}
    public String getRazonS(){return razonS;}
    public void setRazonS(String razonS){this.razonS=razonS;}
    public int getAforo(){return aforo;}
    public void setAforo(int aforo){this.aforo=aforo;}
    public boolean isPublica(){return publica;}
    public void setPublica(boolean publica){this.publica=publica;}
    public List<Socio> getSocios(){return socios;}
    public void setSocios(List<Socio> socios){this.socios=socios;}
    public List<SolicitudLicencia> getSolicitudesLicencia(){return solicitudesLicencia;}
    public void setSolicitudesLicencia(List<SolicitudLicencia> solicitudesLicencia){this.solicitudesLicencia=solicitudesLicencia;}
    public List<Producto> getProductos(){return productos;}
    public void setProductos(List<Producto> productos){this.productos=productos;}
}
