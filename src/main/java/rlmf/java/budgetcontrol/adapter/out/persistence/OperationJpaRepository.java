package rlmf.java.budgetcontrol.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import rlmf.java.budgetcontrol.adapter.out.persistence.entities.OperationEntity;

import java.time.LocalDate;
import java.util.List;

interface OperationJpaRepository extends JpaRepository<OperationEntity, Long> {

    List<OperationEntity> findByOperationDateBetweenOrderByOperationDateAsc(LocalDate startDate, LocalDate endDate);
}
