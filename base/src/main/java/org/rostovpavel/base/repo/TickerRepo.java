package org.rostovpavel.base.repo;

import org.rostovpavel.base.models.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TickerRepo extends JpaRepository<Ticker, Long> {
    List<Ticker> findByNameOrderByIdDesc(String name);
}
