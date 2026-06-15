package com.proyectoorientadoaobjetos.Sistema_de_Gestion_para_Esteticas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Data
@Entity
@Table(name = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // Ej: Corte de cabello, Manicure, Tinte

    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    private Integer duracionMinutos; // Duración estimada del servicio

   @JsonIgnore
   @OneToMany(mappedBy = "servicio")
   private List<ReservaServicio> reservaServicios;

    // Getters y Setters
}
