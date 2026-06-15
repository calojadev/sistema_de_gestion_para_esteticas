package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.controller;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Cliente;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // GET http://localhost:8080/api/clientes
    @GetMapping
    public List<Cliente> obtenerTodos() {
        return clienteService.obtenerTodos();
    }

    // GET http://localhost:8080/api/clientes/ci/12345678
    @GetMapping("/ci/{ci}")
    public ResponseEntity<Cliente> obtenerPorCi(@PathVariable Long ci) {
        return clienteService.obtenerPorCi(ci)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // POST http://localhost:8080/api/clientes
    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        try {
            Cliente nuevo = clienteService.guardar(cliente);
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT http://localhost:8080/api/clientes/ci/12345678
    @PutMapping("/ci/{ci}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long ci, @RequestBody Cliente cliente) {
        try {
            Cliente actualizado = clienteService.actualizar(ci, cliente);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE http://localhost:8080/api/clientes/ci/12345678
    @DeleteMapping("/ci/{ci}")
    public ResponseEntity<Void> eliminar(@PathVariable Long ci) {
        try {
            clienteService.eliminar(ci);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}