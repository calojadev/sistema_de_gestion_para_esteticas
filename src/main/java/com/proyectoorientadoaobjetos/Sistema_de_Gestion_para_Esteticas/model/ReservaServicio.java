package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reserva_servicios")
public class ReservaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    private Integer cantidad;
}