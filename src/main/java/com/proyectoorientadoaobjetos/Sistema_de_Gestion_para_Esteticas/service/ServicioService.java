package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.service;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Servicio;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    // Obtener todos los servicios
    public List<Servicio> obtenerTodos() {
        return servicioRepository.findAll();
    }

    // Obtener por ID
    public Optional<Servicio> obtenerPorId(Long id) {
        return servicioRepository.findById(id);
    }

    // Buscar por nombre (búsqueda parcial)
    public List<Servicio> buscarPorNombre(String nombre) {
        return servicioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Crear nuevo servicio
    public Servicio guardar(Servicio servicio) {
        if (servicioRepository.existsByNombre(servicio.getNombre())) {
            throw new RuntimeException("Ya existe un servicio con ese nombre: " + servicio.getNombre());
        }
        if (servicio.getPrecio() == null || servicio.getPrecio().doubleValue() < 0) {
            throw new RuntimeException("El precio del servicio no puede ser negativo o nulo");
        }
        return servicioRepository.save(servicio);
    }

    // Actualizar servicio
    public Servicio actualizar(Long id, Servicio servicioActualizado) {
        Servicio existente = servicioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));

        existente.setNombre(servicioActualizado.getNombre());
        existente.setDescripcion(servicioActualizado.getDescripcion());
        existente.setPrecio(servicioActualizado.getPrecio());
        existente.setDuracionMinutos(servicioActualizado.getDuracionMinutos());

        return servicioRepository.save(existente);
    }

    // Eliminar servicio
    public void eliminar(Long id) {
        if (!servicioRepository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado con ID: " + id);
        }
        servicioRepository.deleteById(id);
    }
}