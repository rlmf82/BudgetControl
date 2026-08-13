package rlmf.java.budgetcontrol.adapter.in.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rlmf.java.budgetcontrol.adapter.in.web.dto.OperationRequest;
import rlmf.java.budgetcontrol.adapter.in.web.dto.OperationResponse;
import rlmf.java.budgetcontrol.application.OperationCommand;
import rlmf.java.budgetcontrol.application.ports.in.OperationUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
public class OperationController {

    private final OperationUseCase operationService;

    public OperationController(OperationUseCase operationService) {
        this.operationService = operationService;
    }

    @PostMapping
    public ResponseEntity<OperationResponse> create(@Valid @RequestBody OperationRequest request) {
        var operation = operationService.create(toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(OperationResponse.from(operation));
    }

    @PutMapping("/{id}")
    public OperationResponse update(@PathVariable Long id, @Valid @RequestBody OperationRequest request) {
        var operation = operationService.update(id, toCommand(request));
        return OperationResponse.from(operation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        operationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public OperationResponse get(@PathVariable Long id) {
        return OperationResponse.from(operationService.get(id));
    }

    @GetMapping
    public List<OperationResponse> list() {
        return operationService.list().stream()
                .map(OperationResponse::from)
                .toList();
    }

    private OperationCommand toCommand(OperationRequest request) {
        return new OperationCommand(request.type(), request.amount(), request.description(), request.categoryId(), request.date());
    }
}
