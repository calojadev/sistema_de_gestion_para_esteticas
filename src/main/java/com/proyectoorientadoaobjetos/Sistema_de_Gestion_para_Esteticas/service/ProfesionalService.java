package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.service;

import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model.Profesional;
import com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.repository.ProfesionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProfesionalService {

    @Autowired
    private ProfesionalRepository profesionalRepository;

    // Obtener todos los profesionales
    public List<Profesional> obtenerTodos() {
        return profesionalRepository.findAll();
    }

    // Obtener por ID
    public Optional<Profesional> obtenerPorId(Long id) {
        return profesionalRepository.findById(id);
    }

    // Obtener por especialidad
    public List<Profesional> obtenerPorEspecialidad(String especialidad) {
        return profesionalRepository.findByEspecialidad(especialidad);
    }

    // Crear nuevo profesional
    public Profesional guardar(Profesional profesional) {
        if (profesionalRepository.existsByCorreo(profesional.getCorreo())) {
            throw new RuntimeException("Ya existe un profesional con ese correo: " + profesional.getCorreo());
        }
        return profesionalRepository.save(profesional);
    }

    // Actualizar profesional
    public Profesional actualizar(Long id, Profesional profesionalActualizado) {
        Profesional existente = profesionalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profesional no encontrado con ID: " + id));

        existente.setNombre(profesionalActualizado.getNombre());
        existente.setApellido(profesionalActualizado.getApellido());
        existente.setEspecialidad(profesionalActualizado.getEspecialidad());
        existente.setTelefono(profesionalActualizado.getTelefono());
        existente.setCorreo(profesionalActualizado.getCorreo());

        return profesionalRepository.save(existente);
    }

    // Eliminar profesional
    public void eliminar(Long id) {
        if (!profesionalRepository.existsById(id)) {
            throw new RuntimeException("Profesional no encontrado con ID: " + id);
        }
        profesionalRepository.deleteById(id);
    }
}
