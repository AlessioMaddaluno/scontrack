package it.alessiomaddaluno.scontrackbot.repository;

import it.alessiomaddaluno.scontrackbot.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt,Long> {

    @Query("SELECT r FROM Receipt r WHERE r.user.id = :userId AND YEAR(r.transactionDate) = :year AND MONTH(r.transactionDate) = :month")
    List<Receipt> findByMonthAndYear(int month, int year, long userId);

    Receipt findByIdAndUserChatId(long id, long userId);


}
