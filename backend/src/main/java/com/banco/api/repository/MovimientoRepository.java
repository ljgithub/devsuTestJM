package com.banco.api.repository;

import com.banco.api.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findByCuentaNumeroCuentaOrderByFechaDesc(String numeroCuenta);

    List<Movimiento> findByCuentaClienteIdAndFechaBetweenOrderByFechaDesc(
            Integer id, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM Movimiento m " +
           "WHERE m.cuenta.cliente.id = :clienteId " +
           "AND m.valor < 0 " +
           "AND m.fecha >= :start " +
           "AND m.fecha <= :end")
    BigDecimal sumDebitsByClienteAndDay(
            @Param("clienteId") Integer clienteId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Movimiento> findByCuentaClienteClienteidAndFechaBetweenOrderByFechaDesc(
            String clienteid, LocalDateTime start, LocalDateTime end);
}
