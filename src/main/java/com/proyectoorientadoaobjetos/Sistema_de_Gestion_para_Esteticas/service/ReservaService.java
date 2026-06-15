package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.service;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.*;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
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

    // Obtener todas las reservas
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    // Obtener reserva por ID
    public Optional<Reserva> obtenerPorId(Long id) {
        return reservaRepository.findById(id);
    }

    // Obtener reservas de un cliente
    public List<Reserva> obtenerPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    // Obtener reservas de un profesional
    public List<Reserva> obtenerPorProfesional(Long profesionalId) {
        return reservaRepository.findByProfesionalId(profesionalId);
    }

    // Obtener reservas por estado
    public List<Reserva> obtenerPorEstado(EstadoReserva estado) {
        return reservaRepository.findByEstado(estado);
    }

    // Crear nueva reserva
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

        // Validar disponibilidad del profesional en esa fecha y hora
        if (reservaRepository.existsByProfesionalIdAndFechaAndHora(
                reserva.getProfesional().getId(),
                reserva.getFecha(),
                reserva.getHora())) {
            throw new RuntimeException("El profesional ya tiene una reserva en esa fecha y hora");
        }
         // ← ESTO ES LO QUE FALTABA
        if (reserva.getReservaServicios() != null) {
        for (ReservaServicio rs : reserva.getReservaServicios()) {
            rs.setReserva(reserva);
            }
        }

        // Estado inicial siempre es PENDIENTE
        reserva.setEstado(EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    // Cambiar estado de la reserva
    public Reserva cambiarEstado(Long id, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        // Validar que no se reactive una reserva cancelada
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new RuntimeException("No se puede modificar una reserva cancelada");
        }

        reserva.setEstado(nuevoEstado);
        return reservaRepository.save(reserva);
    }

    // Actualizar observaciones
    public Reserva actualizarObservaciones(Long id, String observaciones) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        reserva.setObservaciones(observaciones);
        return reservaRepository.save(reserva);
    }

    // Cancelar reserva
    public Reserva cancelar(Long id) {
        return cambiarEstado(id, EstadoReserva.CANCELADA);
    }

    // Eliminar reserva
    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada con ID: " + id);
        }
        reservaRepository.deleteById(id);
    }
}