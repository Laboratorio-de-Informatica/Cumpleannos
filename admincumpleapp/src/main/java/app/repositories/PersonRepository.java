package app.repositories;

import app.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query(value = "select * from informacion where nombre = :nombreUser and apellido = :apellidoUser and perfil = :perfilUser", nativeQuery = true)
    List<Person> getPersonById(@Param("nombreUser") String nombreUser, @Param("apellidoUser") String apellidoUser, @Param("perfilUser") String perfilUser);

    @Query(value = "select * from informacion where correo = :correoUser", nativeQuery = true)
    List<Person> getPersonByEmail(@Param("correoUser") String correoUser);



}