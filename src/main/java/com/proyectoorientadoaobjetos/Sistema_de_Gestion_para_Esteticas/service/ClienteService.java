package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.service;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Cliente;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Obtener todos los clientes
    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    // Obtener cliente por CI (búsqueda principal)
    public Optional<Cliente> obtenerPorCi(Long ci) {
        return clienteRepository.findByCi(ci);
    }

    // Obtener cliente por correo
    public Optional<Cliente> obtenerPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    // Crear nuevo cliente
    public Cliente guardar(Cliente cliente) {
        if (clienteRepository.existsByCi(cliente.getCi())) {
            throw new RuntimeException("Ya existe un cliente con ese CI: " + cliente.getCi());
        }
        if (clienteRepository.existsByCorreo(cliente.getCorreo())) {
            throw new RuntimeException("Ya existe un cliente con ese correo: " + cliente.getCorreo());
        }
        return clienteRepository.save(cliente);
    }

    // Actualizar cliente buscando por CI
    public Cliente actualizar(Long ci, Cliente clienteActualizado) {
        Cliente existente = clienteRepository.findByCi(ci)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con CI: " + ci));

        existente.setNombre(clienteActualizado.getNombre());
        existente.setApellido(clienteActualizado.getApellido());
        existente.setTelefono(clienteActualizado.getTelefono());
        existente.setDireccion(clienteActualizado.getDireccion());
        existente.setCorreo(clienteActualizado.getCorreo());
        // El CI no se actualiza — es el identificador del cliente

        return clienteRepository.save(existente);
    }

    // Eliminar cliente por CI
    public void eliminar(Long ci) {
        Cliente existente = clienteRepository.findByCi(ci)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con CI: " + ci));
        clienteRepository.deleteById(existente.getId());
    }
}