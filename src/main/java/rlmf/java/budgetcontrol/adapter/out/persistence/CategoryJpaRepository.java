package rlmf.java.budgetcontrol.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import rlmf.java.budgetcontrol.adapter.out.persistence.entities.CategoryEntity;

interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
}
