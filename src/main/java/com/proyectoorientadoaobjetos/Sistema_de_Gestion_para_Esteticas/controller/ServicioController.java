package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.controller;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Servicio;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    // GET http://localhost:8080/api/servicios
    @GetMapping
    public List<Servicio> obtenerTodos() {
        return servicioService.obtenerTodos();
    }

    // GET http://localhost:8080/api/servicios/1
    @GetMapping("/{id}")
    public ResponseEntity<Servicio> obtenerPorId(@PathVariable Long id) {
        return servicioService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET http://localhost:8080/api/servicios/buscar?nombre=corte
    @GetMapping("/buscar")
    public List<Servicio> buscarPorNombre(@RequestParam String nombre) {
        return servicioService.buscarPorNombre(nombre);
    }

    // POST http://localhost:8080/api/servicios
    @PostMapping
    public ResponseEntity<Servicio> crear(@RequestBody Servicio servicio) {
        try {
            Servicio nuevo = servicioService.guardar(servicio);
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT http://localhost:8080/api/servicios/1
    @PutMapping("/{id}")
    public ResponseEntity<Servicio> actualizar(@PathVariable Long id, @RequestBody Servicio servicio) {
        try {
            Servicio actualizado = servicioService.actualizar(id, servicio);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE http://localhost:8080/api/servicios/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            servicioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}