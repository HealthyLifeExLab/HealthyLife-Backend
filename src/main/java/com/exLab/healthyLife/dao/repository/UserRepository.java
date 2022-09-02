package com.exLab.healthyLife.dao.repository;

import com.exLab.healthyLife.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByIdAndDeletedFalse(Long id);

    Optional<User> findUserByEmailAndDeletedFalse(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User SET deleted = true where id = :id")
    void softDelete(@Param("id") Long id);
}
