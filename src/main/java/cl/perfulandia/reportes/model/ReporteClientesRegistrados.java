package cl.perfulandia.reportes.model;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reporte_usuarios_registrados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteClientesRegistrados {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalClientes;
    private LocalDateTime fechaGeneracion;
}
