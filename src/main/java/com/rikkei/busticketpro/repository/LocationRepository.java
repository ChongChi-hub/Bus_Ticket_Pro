package com.rikkei.busticketpro.repository;

import com.rikkei.busticketpro.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
