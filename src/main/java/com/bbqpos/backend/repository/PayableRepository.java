package com.bbqpos.backend.repository;

import com.bbqpos.backend.model.Payable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayableRepository extends JpaRepository<Payable, Long> {

    List<Payable> findByStatus(Payable.Status status);

}
