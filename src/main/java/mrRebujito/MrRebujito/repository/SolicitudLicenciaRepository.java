package mrRebujito.MrRebujito.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import mrRebujito.MrRebujito.entity.EstadoLicencia;
import mrRebujito.MrRebujito.entity.SolicitudLicencia;
import mrRebujito.MrRebujito.entity.Ayuntamiento;

public interface SolicitudLicenciaRepository extends JpaRepository<SolicitudLicencia,Integer> {
    long countByAyuntamientoIdAndEstadoLicencia(int ayuntamientoId,EstadoLicencia estado);
    long countByAyuntamientoId(int ayuntamientoId);
    List<SolicitudLicencia> findAllByAyuntamiento(Ayuntamiento ayuntamiento);
    @Query("SELECT DISTINCT s FROM Caseta c JOIN c.solicitudesLicencia s WHERE c.id=:casetaId")
    List<SolicitudLicencia> findAllByCasetaId(@Param("casetaId")int casetaId);
    @Query("SELECT COUNT(s) FROM Caseta c JOIN c.solicitudesLicencia s WHERE c.id=:casetaId AND s.id=:solicitudId")
    long countByCasetaAndSolicitud(@Param("casetaId")int casetaId,@Param("solicitudId")int solicitudId);
    @Query("SELECT COUNT(s) FROM Caseta c JOIN c.solicitudesLicencia s WHERE c.id=:casetaId AND s.ayuntamiento.id=:ayuntamientoId AND s.estadoLicencia IN :estados")
    long countOpenByCasetaAndAyuntamiento(@Param("casetaId")int casetaId,
       @Param("ayuntamientoId")int ayuntamientoId,@Param("estados")Collection<EstadoLicencia> estados);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SolicitudLicencia s WHERE s.id=:id")
    Optional<SolicitudLicencia> findByIdForUpdate(@Param("id")int id);
}
