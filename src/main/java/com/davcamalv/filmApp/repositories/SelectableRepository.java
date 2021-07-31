package com.davcamalv.filmApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.Selectable;

@Repository
public interface SelectableRepository extends JpaRepository<Selectable, Long>{

}
