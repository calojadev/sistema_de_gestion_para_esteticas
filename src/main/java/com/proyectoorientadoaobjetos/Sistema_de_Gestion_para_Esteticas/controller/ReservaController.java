package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.controller;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.EstadoReserva;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Reserva;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    // GET http://localhost:8080/api/reservas
    @GetMapping
    public List<Reserva> obtenerTodas() {
        return reservaService.obtenerTodas();
    }

    // GET http://localhost:8080/api/reservas/1
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerPorId(@PathVariable Long id) {
        return reservaService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET http://localhost:8080/api/reservas/cliente/1
    @GetMapping("/cliente/{clienteId}")
    public List<Reserva> obtenerPorCliente(@PathVariable Long clienteId) {
        return reservaService.obtenerPorCliente(clienteId);
    }

    // GET http://localhost:8080/api/reservas/profesional/1
    @GetMapping("/profesional/{profesionalId}")
    public List<Reserva> obtenerPorProfesional(@PathVariable Long profesionalId) {
        return reservaService.obtenerPorProfesional(profesionalId);
    }

    // GET http://localhost:8080/api/reservas/estado/PENDIENTE
    @GetMapping("/estado/{estado}")
    public List<Reserva> obtenerPorEstado(@PathVariable EstadoReserva estado) {
        return reservaService.obtenerPorEstado(estado);
    }

    // POST http://localhost:8080/api/reservas
    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody Reserva reserva) {
        try {
            Reserva nueva = reservaService.guardar(reserva);
            return ResponseEntity.ok(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PATCH http://localhost:8080/api/reservas/1/estado/CONFIRMADA
    @PatchMapping("/{id}/estado/{estado}")
    public ResponseEntity<Reserva> cambiarEstado(@PathVariable Long id, @PathVariable EstadoReserva estado) {
        try {
            Reserva actualizada = reservaService.cambiarEstado(id, estado);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PATCH http://localhost:8080/api/reservas/1/cancelar
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelar(@PathVariable Long id) {
        try {
            Reserva cancelada = reservaService.cancelar(id);
            return ResponseEntity.ok(cancelada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE http://localhost:8080/api/reservas/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            reservaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}