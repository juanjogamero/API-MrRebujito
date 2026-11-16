package mrRebujito.MrRebujito.dto;
import mrRebujito.MrRebujito.entity.SolicitudLicencia;
import mrRebujito.MrRebujito.entity.EstadoLicencia;
public record SolicitudResponse(int id,EstadoLicencia estado,int ayuntamientoId,String ayuntamiento){
    public static SolicitudResponse from(SolicitudLicencia s){
        return new SolicitudResponse(s.getId(),s.getEstadoLicencia(),s.getAyuntamiento().getId(),
            s.getAyuntamiento().getNombre());
    }
}
