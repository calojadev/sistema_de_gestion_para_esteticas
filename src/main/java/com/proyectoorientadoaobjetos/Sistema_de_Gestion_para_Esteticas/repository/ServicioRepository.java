package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.repository;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    // Buscar servicios por nombre
    List<Servicio> findByNombreContainingIgnoreCase(String nombre);

    // Verificar si existe un servicio con ese nombre exacto
    boolean existsByNombre(String nombre);
}
