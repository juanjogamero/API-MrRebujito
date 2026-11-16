package mrRebujito.MrRebujito.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import mrRebujito.MrRebujito.entity.*;
import mrRebujito.MrRebujito.repository.*;

@SpringBootTest
class LicenciaIntegrationTest {
    @Autowired CasetaService casetas;
    @Autowired AyuntamientoService ayuntamientos;
    @Autowired SolicitudLicenciaRepository licencias;

    @Test
    @Transactional
    @WithMockUser(username="caseta-integracion",authorities={"CASETA"})
    void laSolicitudSePersisteYElDuplicadoSeRechaza() {
        Ayuntamiento a=new Ayuntamiento();
        a.setNombre("Ayuntamiento de prueba");
        a.setUsername("ayuntamiento-integracion");
        a.setCorreo("ayto@example.com");
        a.setPassword("clave-segura-local");
        a.setLicenciaMax(2);
        a=ayuntamientos.saveAyuntamiento(a);

        Caseta c=new Caseta();
        c.setNombre("Caseta de prueba");
        c.setRazonS("Asociación Prueba Integración");
        c.setUsername("caseta-integracion");
        c.setCorreo("caseta@example.com");
        c.setPassword("clave-segura-local");
        c.setAforo(5);
        c=casetas.saveCaseta(c);

        SolicitudLicencia creada=casetas.crearSolicitud(a.getId());
        assertNotNull(creada.getId());
        assertEquals(EstadoLicencia.PENDIENTE,creada.getEstadoLicencia());
        assertEquals(1,licencias.findAllByCasetaId(c.getId()).size());

        final int ayuntamientoId=a.getId();
        assertThrows(IllegalStateException.class,()->casetas.crearSolicitud(ayuntamientoId));
        assertEquals(1,licencias.findAllByCasetaId(c.getId()).size());
    }
}
