package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.LoyaltyAccount;
import ca.senecacollege.malibuluminahotel.models.LoyaltyTransaction;
import ca.senecacollege.malibuluminahotel.models.enums.LoyaltyTransactionType;
import java.time.LocalDate;
import java.util.List;

public interface ILoyaltyTransactionRepository
        extends IRepository<LoyaltyTransaction, Long> {

    List<LoyaltyTransaction> findByLoyaltyAccount(
            LoyaltyAccount loyaltyAccount
    );

    List<LoyaltyTransaction> findByTransactionType(
            LoyaltyTransactionType transactionType
    );

    List<LoyaltyTransaction> findByLoyaltyAccountAndType(
            LoyaltyAccount loyaltyAccount,
            LoyaltyTransactionType transactionType
    );

    List<LoyaltyTransaction> findByTransactionDate(
            LocalDate transactionDate
    );

    List<LoyaltyTransaction> findByTransactionDateRange(
            LocalDate startDate,
            LocalDate endDate
    );
}
