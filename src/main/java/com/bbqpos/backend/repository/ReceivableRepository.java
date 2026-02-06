package com.bbqpos.backend.repository;

import com.bbqpos.backend.model.Receivable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivableRepository extends JpaRepository<Receivable, Long> {

    List<Receivable> findByStatus(Receivable.Status status);

}
