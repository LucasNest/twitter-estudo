package tech.buildrun.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.buildrun.springsecurity.entities.UserAppearence;

import java.util.UUID;

@Repository
public interface UserAppearenceRepository extends JpaRepository<UserAppearence, UUID> {
}
