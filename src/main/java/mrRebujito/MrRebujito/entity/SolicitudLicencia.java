package mrRebujito.MrRebujito.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class SolicitudLicencia extends DomainEntity {
    @NotNull @Enumerated(EnumType.STRING)
    private EstadoLicencia estadoLicencia;
    @ManyToOne(optional=false)
    private Ayuntamiento ayuntamiento;
    public SolicitudLicencia() {}
    public EstadoLicencia getEstadoLicencia(){return estadoLicencia;}
    public void setEstadoLicencia(EstadoLicencia estadoLicencia){this.estadoLicencia=estadoLicencia;}
    public Ayuntamiento getAyuntamiento(){return ayuntamiento;}
    public void setAyuntamiento(Ayuntamiento ayuntamiento){this.ayuntamiento=ayuntamiento;}
}
