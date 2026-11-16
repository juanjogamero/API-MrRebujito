package mrRebujito.MrRebujito.dto;
import mrRebujito.MrRebujito.entity.Caseta;
public record CasetaPublicResponse(int id,String nombre,String razonSocial,
    int aforo,boolean publica,String foto) {
    public static CasetaPublicResponse from(Caseta c){
        return new CasetaPublicResponse(c.getId(),c.getNombre(),c.getRazonS(),c.getAforo(),c.isPublica(),c.getFoto());
    }
}
