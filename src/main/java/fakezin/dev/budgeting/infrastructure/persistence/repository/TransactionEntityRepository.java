package fakezin.dev.budgeting.infrastructure.persistence.repository;

import fakezin.dev.budgeting.domain.Category;
import fakezin.dev.budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByCategory(Category category);
}
