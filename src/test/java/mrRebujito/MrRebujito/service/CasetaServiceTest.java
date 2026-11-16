package mrRebujito.MrRebujito.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import mrRebujito.MrRebujito.entity.*;
import mrRebujito.MrRebujito.repository.*;
import mrRebujito.MrRebujito.security.JWTUtils;

class CasetaServiceTest {
    CasetaRepository casetas=mock(CasetaRepository.class);
    AyuntamientoRepository aytos=mock(AyuntamientoRepository.class);
    SolicitudLicenciaRepository licencias=mock(SolicitudLicenciaRepository.class);
    SocioRepository socios=mock(SocioRepository.class);
    PasswordEncoder encoder=mock(PasswordEncoder.class);
    JWTUtils jwt=mock(JWTUtils.class);
    CasetaService service;
    Caseta caseta;
    @BeforeEach void preparar(){
        service=new CasetaService(casetas,aytos,licencias,socios,encoder,jwt);
        caseta=new Caseta();
        caseta.setId(3);caseta.setAforo(1);caseta.setSocios(new ArrayList<>());
        caseta.setSolicitudesLicencia(new ArrayList<>());
        when(jwt.<Caseta>userLogin()).thenReturn(caseta);
    }
    @Test void permiteOcuparUltimaPlaza(){
        when(casetas.findByIdForUpdate(3)).thenReturn(Optional.of(caseta));
        Socio socio=new Socio();socio.setId(9);
        when(socios.findById(9)).thenReturn(Optional.of(socio));
        when(casetas.save(caseta)).thenReturn(caseta);
        assertEquals(1,service.addSocio(9).getSocios().size());
    }
    @Test void noPermiteSuperarAforo(){
        Socio actual=new Socio();actual.setId(5);caseta.getSocios().add(actual);
        when(casetas.findByIdForUpdate(3)).thenReturn(Optional.of(caseta));
        Socio nuevo=new Socio();nuevo.setId(8);
        when(socios.findById(8)).thenReturn(Optional.of(nuevo));
        assertThrows(IllegalStateException.class,()->service.addSocio(8));
        verify(casetas,never()).save(any());
    }
    @Test void persistenciaSolicitudPendiente(){
        Ayuntamiento a=new Ayuntamiento();a.setId(7);
        when(casetas.findByIdForUpdate(3)).thenReturn(Optional.of(caseta));
        when(aytos.findById(7)).thenReturn(Optional.of(a));
        when(licencias.save(any())).thenAnswer(invocation->invocation.getArgument(0));
        var result=service.crearSolicitud(7);
        assertEquals(EstadoLicencia.PENDIENTE,result.getEstadoLicencia());
        assertEquals(1,caseta.getSolicitudesLicencia().size());
        verify(licencias).save(any(SolicitudLicencia.class));
        verify(casetas).save(caseta);
    }
}
