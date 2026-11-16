package mrRebujito.MrRebujito.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import mrRebujito.MrRebujito.entity.Ayuntamiento;

public interface AyuntamientoRepository extends JpaRepository<Ayuntamiento,Integer>{
    Optional<Ayuntamiento> findByUsername(String username);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Ayuntamiento a WHERE a.id=:id")
    Optional<Ayuntamiento> findByIdForUpdate(@Param("id")int id);
}
