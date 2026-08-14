package rlmf.java.budgetcontrol.application.ports.in;

import rlmf.java.budgetcontrol.domain.model.Category;

import java.util.List;

public interface CategoryUseCase {

    Category get(Long id);

    List<Category> list();
}
