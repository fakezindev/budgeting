package fakezin.dev.budgeting.infrastructure.http.response;

import fakezin.dev.budgeting.application.output.TransactionOutput;
import fakezin.dev.budgeting.domain.Category;
import fakezin.dev.budgeting.domain.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransactionResponse(String id, String description, String category, double amount) {
    public static TransactionResponse from(TransactionOutput output) {
        return new TransactionResponse(
                output.id(),
                output.category(),
                output.description(),
                output.value()
        );
    }
}
