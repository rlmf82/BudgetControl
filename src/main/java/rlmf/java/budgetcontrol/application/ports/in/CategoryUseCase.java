package rlmf.java.budgetcontrol.application.ports.in;

import rlmf.java.budgetcontrol.domain.model.Category;

import java.util.List;

public interface CategoryUseCase {

    List<Category> list();
}
