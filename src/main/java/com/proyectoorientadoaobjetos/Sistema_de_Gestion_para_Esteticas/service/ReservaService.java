package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.service;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.*;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    // ── Obtener todas las reservas ──────────────────────────────────────────
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    // ── Obtener reserva por ID ──────────────────────────────────────────────
    public Optional<Reserva> obtenerPorId(Long id) {
        return reservaRepository.findById(id);
    }

    // ── Obtener reservas de un cliente ──────────────────────────────────────
    public List<Reserva> obtenerPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    // ── Obtener reservas de un profesional ─────────────────────────────────
    public List<Reserva> obtenerPorProfesional(Long profesionalId) {
        return reservaRepository.findByProfesionalId(profesionalId);
    }

    // ── Obtener reservas por estado ─────────────────────────────────────────
    public List<Reserva> obtenerPorEstado(EstadoReserva estado) {
        return reservaRepository.findByEstado(estado);
    }

    // ── Calcular la duración total de los servicios de una reserva ──────────
    /**
     * Suma los duracionMinutos de cada servicio en la lista de ReservaServicio.
     * Si un servicio no tiene duración definida (null) se trata como 0.
     * Si la lista está vacía o es null devuelve 0.
     */
    private int calcularDuracionTotal(List<ReservaServicio> reservaServicios) {
        if (reservaServicios == null || reservaServicios.isEmpty()) return 0;
        return reservaServicios.stream()
            .mapToInt(rs -> {
                if (rs.getServicio() == null) return 0;
                // Resolver el servicio completo desde la BD para obtener duracionMinutos
                Servicio servicio = servicioRepository.findById(rs.getServicio().getId())
                    .orElse(null);
                if (servicio == null || servicio.getDuracionMinutos() == null) return 0;
                int cantidad = (rs.getCantidad() != null && rs.getCantidad() > 0) ? rs.getCantidad() : 1;
                return servicio.getDuracionMinutos() * cantidad;
            })
            .sum();
    }

    // ── Validar solapamiento de horarios ────────────────────────────────────
    /**
     * Lanza RuntimeException si el profesional ya tiene una reserva activa
     * (no CANCELADA) que se solape con el bloque [horaInicio, horaInicio + duracionMinutos).
     *
     * excludeId: id de la reserva a excluir de la búsqueda (usar al editar).
     *            Pasar null al crear una reserva nueva.
     */
    private void validarSolapamiento(Long profesionalId, LocalDate fecha,
                                     LocalTime horaInicio, int duracionMinutos,
                                     Long excludeId) {
        // Si la duración es 0 (servicios sin duración configurada) usamos 30 min por defecto
        int duracion = duracionMinutos > 0 ? duracionMinutos : 30;
        LocalTime horaFin = horaInicio.plusMinutes(duracion);

        List<Reserva> solapadas = reservaRepository.findSolapamientos(
            profesionalId, fecha, horaInicio, horaFin, excludeId
        );

        if (!solapadas.isEmpty()) {
            Reserva primera = solapadas.get(0);
            throw new RuntimeException(
                "El profesional ya tiene una reserva en ese horario " +
                "(reserva #" + primera.getId() + " a las " + primera.getHora() + "). " +
                "Por favor elige otro horario."
            );
        }
    }

    // ── Crear nueva reserva ─────────────────────────────────────────────────
    public Reserva guardar(Reserva reserva) {

        // Validar que el cliente existe
        clienteRepository.findById(reserva.getCliente().getId())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + reserva.getCliente().getId()));

        // Validar que el profesional existe
        profesionalRepository.findById(reserva.getProfesional().getId())
            .orElseThrow(() -> new RuntimeException("Profesional no encontrado con ID: " + reserva.getProfesional().getId()));

        // Validar que la fecha no sea en el pasado
        if (reserva.getFecha().isBefore(LocalDate.now())) {
            throw new RuntimeException("No se puede crear una reserva en una fecha pasada");
        }

        // Calcular duración total de los servicios seleccionados
        int duracionTotal = calcularDuracionTotal(reserva.getReservaServicios());

        // Validar solapamiento con reservas existentes del profesional
        validarSolapamiento(
            reserva.getProfesional().getId(),
            reserva.getFecha(),
            reserva.getHora(),
            duracionTotal,
            null  // null porque es una reserva nueva, no excluimos ninguna
        );

        // Enlazar cada ReservaServicio con la reserva padre
        if (reserva.getReservaServicios() != null) {
            for (ReservaServicio rs : reserva.getReservaServicios()) {
                rs.setReserva(reserva);
            }
        }

        // Estado inicial siempre es PENDIENTE
        reserva.setEstado(EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    // ── Actualizar reserva completa ─────────────────────────────────────────
    public Reserva actualizar(Long id, Reserva reservaActualizada) {
        Reserva existente = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        // Validar que el cliente existe
        clienteRepository.findById(reservaActualizada.getCliente().getId())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Validar que el profesional existe
        profesionalRepository.findById(reservaActualizada.getProfesional().getId())
            .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        // Calcular duración total de los servicios actualizados
        int duracionTotal = calcularDuracionTotal(reservaActualizada.getReservaServicios());

        // Validar solapamiento excluyendo la reserva que se está editando
        validarSolapamiento(
            reservaActualizada.getProfesional().getId(),
            reservaActualizada.getFecha(),
            reservaActualizada.getHora(),
            duracionTotal,
            id  // excluimos la propia reserva para no bloquearse a sí misma
        );

        existente.setFecha(reservaActualizada.getFecha());
        existente.setHora(reservaActualizada.getHora());
        existente.setObservaciones(reservaActualizada.getObservaciones());
        existente.setCliente(reservaActualizada.getCliente());
        existente.setProfesional(reservaActualizada.getProfesional());

        return reservaRepository.save(existente);
    }

    // ── Cambiar estado de la reserva ────────────────────────────────────────
    public Reserva cambiarEstado(Long id, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        // No se puede reactivar una reserva cancelada
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new RuntimeException("No se puede modificar una reserva cancelada");
        }

        reserva.setEstado(nuevoEstado);
        return reservaRepository.save(reserva);
    }

    // ── Actualizar solo observaciones ───────────────────────────────────────
    public Reserva actualizarObservaciones(Long id, String observaciones) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        reserva.setObservaciones(observaciones);
        return reservaRepository.save(reserva);
    }

    // ── Cancelar reserva ────────────────────────────────────────────────────
    public Reserva cancelar(Long id) {
        return cambiarEstado(id, EstadoReserva.CANCELADA);
    }

    // ── Eliminar reserva ────────────────────────────────────────────────────
    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada con ID: " + id);
        }
        reservaRepository.deleteById(id);
    }
}
