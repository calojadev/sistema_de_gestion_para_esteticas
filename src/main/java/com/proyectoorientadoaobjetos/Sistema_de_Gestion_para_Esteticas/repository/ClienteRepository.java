package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.repository;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {


    // Buscar cliente por CI
    Optional<Cliente> findByCi(Long ci);

    // Buscar cliente por correo
    Optional<Cliente> findByCorreo(String correo);

    // Buscar clientes por apellido
    java.util.List<Cliente> findByApellido(String apellido);

    // Verificar si existe un correo
    boolean existsByCorreo(String correo);

    // Verificar si existe un CI
    boolean existsByCi(Long ci);
}