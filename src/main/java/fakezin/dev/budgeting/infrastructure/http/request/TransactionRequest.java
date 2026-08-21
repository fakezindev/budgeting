package fakezin.dev.budgeting.infrastructure.http.request;

import fakezin.dev.budgeting.application.input.PersistTransactionInput;
import fakezin.dev.budgeting.domain.Category;

public record TransactionRequest(String description, Category category, long amount) {
    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(description, amount, category);
    }
}
