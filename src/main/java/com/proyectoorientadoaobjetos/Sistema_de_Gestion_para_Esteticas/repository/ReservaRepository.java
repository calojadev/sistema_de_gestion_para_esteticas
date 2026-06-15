package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.repository;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Reserva;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Todas las reservas de un cliente
    List<Reserva> findByClienteId(Long clienteId);

    // Todas las reservas de un profesional
    List<Reserva> findByProfesionalId(Long profesionalId);

    // Reservas por estado (PENDIENTE, CONFIRMADA, etc.)
    List<Reserva> findByEstado(EstadoReserva estado);

    // Reservas de un profesional en una fecha específica
    List<Reserva> findByProfesionalIdAndFecha(Long profesionalId, LocalDate fecha);

    // Verificar si un profesional ya tiene reserva en esa fecha y hora
    boolean existsByProfesionalIdAndFechaAndHora(Long profesionalId, LocalDate fecha, LocalTime hora);
}
