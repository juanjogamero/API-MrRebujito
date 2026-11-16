package mrRebujito.MrRebujito.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import mrRebujito.MrRebujito.entity.Caseta;

public interface CasetaRepository extends JpaRepository<Caseta,Integer> {
    Optional<Caseta> findByUsername(String username);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Caseta c WHERE c.id=:id")
    Optional<Caseta> findByIdForUpdate(@Param("id")int id);
    @Query("SELECT DISTINCT c FROM Caseta c JOIN c.socios s WHERE s.id=:id")
    List<Caseta> findAllBySocioId(@Param("id")int socioId);
}
