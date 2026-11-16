package mrRebujito.MrRebujito.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import mrRebujito.MrRebujito.entity.*;
import mrRebujito.MrRebujito.repository.*;
import mrRebujito.MrRebujito.security.JWTUtils;

class SolicitudLicenciaServiceTest {
    private final SolicitudLicenciaRepository licencias=mock(SolicitudLicenciaRepository.class);
    private final AyuntamientoRepository aytos=mock(AyuntamientoRepository.class);
    private final CasetaRepository casetas=mock(CasetaRepository.class);
    private final JWTUtils jwt=mock(JWTUtils.class);
    private final SolicitudLicenciaService service=
        new SolicitudLicenciaService(licencias,aytos,casetas,jwt);
    @Test void noAprobarAlLlegarAlCupo(){
        Ayuntamiento a=new Ayuntamiento();a.setId(1);a.setLicenciaMax(2);
        SolicitudLicencia s=new SolicitudLicencia();
        s.setAyuntamiento(a);s.setEstadoLicencia(EstadoLicencia.PENDIENTE);
        when(jwt.<Ayuntamiento>userLogin()).thenReturn(a);
        when(aytos.findByIdForUpdate(1)).thenReturn(Optional.of(a));
        when(licencias.findByIdForUpdate(8)).thenReturn(Optional.of(s));
        when(licencias.countByAyuntamientoIdAndEstadoLicencia(1,EstadoLicencia.APROBADA)).thenReturn(2L);
        assertThrows(IllegalStateException.class,()->service.aceptarSolicitud(8));
        verify(licencias,never()).save(any());
    }
    @Test void unaSolicitudRechazadaNoPuedeAprobarse(){
        Ayuntamiento a=new Ayuntamiento();a.setId(1);
        SolicitudLicencia s=new SolicitudLicencia();
        s.setAyuntamiento(a);s.setEstadoLicencia(EstadoLicencia.RECHAZADA);
        when(jwt.<Ayuntamiento>userLogin()).thenReturn(a);
        when(aytos.findByIdForUpdate(1)).thenReturn(Optional.of(a));
        when(licencias.findByIdForUpdate(8)).thenReturn(Optional.of(s));
        assertFalse(service.aceptarSolicitud(8));
        verify(licencias,never()).save(any());
    }
}
