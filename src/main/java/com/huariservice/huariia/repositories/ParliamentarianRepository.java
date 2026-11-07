package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Parliamentarian;
import com.huariservice.huariia.entities.enums.ParliamentarianChamber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParliamentarianRepository extends JpaRepository<Parliamentarian, Long> {

    List<Parliamentarian> findByChamber(ParliamentarianChamber chamber);

    Page<Parliamentarian> findByChamber(ParliamentarianChamber chamber, Pageable pageable);

    List<Parliamentarian> findByPartyIgnoreCase(String party);

    Optional<Parliamentarian> findByExternalIdAndChamber(String externalId, ParliamentarianChamber chamber);
}

