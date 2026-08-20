package fakezin.dev.budgeting.application.output;

import fakezin.dev.budgeting.domain.Transaction;

public record TransactionOutput(String id, String description, String category, double amount) {
    public static TransactionOutput from(Transaction transaction) {
        return new TransactionOutput(
                transaction.getId().uuid().toString(),
                transaction.getDescription(),
                transaction.getCategory().name(),
                transaction.getAmount());
    }
}
