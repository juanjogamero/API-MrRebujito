package mrRebujito.MrRebujito.dto;
import mrRebujito.MrRebujito.entity.Ayuntamiento;
public record AyuntamientoPublicResponse(int id,String nombre,int licenciasMaximas){
    public static AyuntamientoPublicResponse from(Ayuntamiento a){
        return new AyuntamientoPublicResponse(a.getId(),a.getNombre(),a.getLicenciaMax());
    }
}
