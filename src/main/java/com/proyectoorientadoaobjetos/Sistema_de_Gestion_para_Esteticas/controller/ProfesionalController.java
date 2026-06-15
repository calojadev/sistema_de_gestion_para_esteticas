package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.controller;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Profesional;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.service.ProfesionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profesionales")
public class ProfesionalController {

    @Autowired
    private ProfesionalService profesionalService;

    // GET http://localhost:8080/api/profesionales
    @GetMapping
    public List<Profesional> obtenerTodos() {
        return profesionalService.obtenerTodos();
    }

    // GET http://localhost:8080/api/profesionales/1
    @GetMapping("/{id}")
    public ResponseEntity<Profesional> obtenerPorId(@PathVariable Long id) {
        return profesionalService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET http://localhost:8080/api/profesionales/especialidad/Estilista
    @GetMapping("/especialidad/{especialidad}")
    public List<Profesional> obtenerPorEspecialidad(@PathVariable String especialidad) {
        return profesionalService.obtenerPorEspecialidad(especialidad);
    }

    // POST http://localhost:8080/api/profesionales
    @PostMapping
    public ResponseEntity<Profesional> crear(@RequestBody Profesional profesional) {
        try {
            Profesional nuevo = profesionalService.guardar(profesional);
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT http://localhost:8080/api/profesionales/1
    @PutMapping("/{id}")
    public ResponseEntity<Profesional> actualizar(@PathVariable Long id, @RequestBody Profesional profesional) {
        try {
            Profesional actualizado = profesionalService.actualizar(id, profesional);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE http://localhost:8080/api/profesionales/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            profesionalService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
