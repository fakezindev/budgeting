package fakezin.dev.budgeting.application;

import fakezin.dev.budgeting.application.input.PersistTransactionInput;
import fakezin.dev.budgeting.application.output.TransactionOutput;
import fakezin.dev.budgeting.domain.Transaction;
import fakezin.dev.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class PersistTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public PersistTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "persist-transaction", description = "Persiste uma nova transação financeira")
    public TransactionOutput execute(PersistTransactionInput input) {
        var transaction = new Transaction(input.description(),  input.amount(), input.category());
        var savedTransaction = this.transactionRepository.save(transaction);

        return TransactionOutput.from(savedTransaction);
    }
}
