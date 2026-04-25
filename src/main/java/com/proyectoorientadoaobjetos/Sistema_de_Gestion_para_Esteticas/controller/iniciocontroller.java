package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class iniciocontroller {

    @GetMapping("/")
    public String inicio() {
        return "Sistema de Gestión para Estéticas - Funcionando correctamente ✅";
    }
}