package org.example.myphambe.repository;

import org.example.myphambe.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    Optional<Voucher> findByCode(String code);

    boolean existsByCode(String code);

    List<Voucher> findByStatus(String status);

    @Query("SELECT v FROM Voucher v WHERE v.status = 'ACTIVE' " +
           "AND v.startDate <= :now AND v.endDate >= :now " +
           "AND v.quantity > v.usedCount")
    List<Voucher> findAllValidVouchers(@Param("now") LocalDateTime now);

    @Query("SELECT v FROM Voucher v WHERE v.code = :code " +
           "AND v.status = 'ACTIVE' " +
           "AND v.startDate <= :now AND v.endDate >= :now " +
           "AND v.quantity > v.usedCount")
    Optional<Voucher> findValidVoucherByCode(@Param("code") String code,
                                              @Param("now") LocalDateTime now);
}