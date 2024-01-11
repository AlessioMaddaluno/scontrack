package it.alessiomaddaluno.scontrackbot.repository;

import it.alessiomaddaluno.scontrackbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

}
