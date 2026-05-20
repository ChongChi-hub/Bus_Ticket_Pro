package com.rikkei.busticketpro.repository;

import com.rikkei.busticketpro.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {

    // Tìm tuyến theo điểm đi và điểm đến
    @Query("SELECT r FROM Route r " +
           "JOIN FETCH r.fromLocation fl " +
           "JOIN FETCH r.toLocation tl " +
           "WHERE fl.id = :fromId AND tl.id = :toId")
    List<Route> findByFromAndTo(@Param("fromId") Long fromId,
                                @Param("toId")   Long toId);
}
