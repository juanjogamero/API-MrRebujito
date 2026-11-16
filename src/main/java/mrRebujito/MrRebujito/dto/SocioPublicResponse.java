package mrRebujito.MrRebujito.dto;
import mrRebujito.MrRebujito.entity.Socio;
public record SocioPublicResponse(int id,String nombre,String primerApellido){
    public static SocioPublicResponse from(Socio socio){
        return new SocioPublicResponse(socio.getId(),socio.getNombre(),socio.getPrimerApellido());
    }
}
