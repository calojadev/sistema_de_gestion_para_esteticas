package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.repository;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Reserva;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Verificar si un profesional ya tiene reserva en esa fecha y hora exacta
    // (se mantiene por compatibilidad pero ya no se usa para validar solapamientos)
    boolean existsByProfesionalIdAndFechaAndHora(Long profesionalId, LocalDate fecha, LocalTime hora);

    /**
     * Detecta reservas del mismo profesional en la misma fecha que se solapan
     * con el bloque [horaInicio, horaFin).
     *
     * Una reserva existente solapa si su horaInicio < horaFin_nueva
     * Y su horaFin (hora + duracion) > horaInicio_nueva.
     *
     * Se excluye la reserva con el id indicado (útil al editar una reserva existente).
     * Se ignoran reservas CANCELADAS porque ya no ocupan el horario.
     *
     * duracionExistente se obtiene sumando los duracionMinutos de los servicios
     * de cada reserva existente via JOIN a reservaServicios y servicio.
     */
    @Query("""
        SELECT r FROM Reserva r
        JOIN r.reservaServicios rs
        JOIN rs.servicio s
        WHERE r.profesional.id = :profesionalId
          AND r.fecha = :fecha
          AND r.estado <> com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.EstadoReserva.CANCELADA
          AND (:excludeId IS NULL OR r.id <> :excludeId)
        GROUP BY r
        HAVING r.hora < :horaFin
          AND FUNCTION('TIMESTAMPADD', MINUTE, SUM(s.duracionMinutos), r.hora) > :horaInicio
        """)
    List<Reserva> findSolapamientos(
        @Param("profesionalId") Long profesionalId,
        @Param("fecha") LocalDate fecha,
        @Param("horaInicio") LocalTime horaInicio,
        @Param("horaFin") LocalTime horaFin,
        @Param("excludeId") Long excludeId
    );
}
