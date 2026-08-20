package fakezin.dev.budgeting.application.input;

import fakezin.dev.budgeting.domain.Category;

public record PersistTransactionInput(String description, long amount, Category category) {
}
