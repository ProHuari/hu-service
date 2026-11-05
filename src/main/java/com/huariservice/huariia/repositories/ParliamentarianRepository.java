package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Parliamentarian;
import com.huariservice.huariia.entities.enums.ParliamentarianChamber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParliamentarianRepository extends JpaRepository<Parliamentarian, Long> {

    List<Parliamentarian> findByChamber(ParliamentarianChamber chamber);

    List<Parliamentarian> findByPartyIgnoreCase(String party);
}
