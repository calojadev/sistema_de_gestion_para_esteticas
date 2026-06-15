package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.repository;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {

    // Buscar por especialidad (ej: todos los Estilistas)
    List<Profesional> findByEspecialidad(String especialidad);

    // Buscar por correo
    Optional<Profesional> findByCorreo(String correo);

    // Verificar si existe el correo
    boolean existsByCorreo(String correo);
}
