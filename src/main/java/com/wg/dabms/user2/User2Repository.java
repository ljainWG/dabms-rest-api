package com.wg.dabms.user2;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wg.dabms.enums.UserDepartment;
import com.wg.dabms.enums.UserGender;
import com.wg.dabms.enums.UserRole;

public interface User2Repository extends JpaRepository<User2DTO, String> {

    @Query("SELECT u FROM User2DTO u WHERE "
            + "(:userName IS NULL OR u.userName LIKE %:userName%) AND "
            + "(:userRealName IS NULL OR u.userRealName LIKE %:userRealName%) AND "
            + "(:userEmail IS NULL OR u.userEmail LIKE %:userEmail%) AND "
            + "(:userGender IS NULL OR u.userGender = :userGender) AND "
            + "(:userRole IS NULL OR u.userRole = :userRole) AND "
            + "(:userDepartment IS NULL OR u.userDepartment = :userDepartment) AND "
            + "(:userPhoneNo IS NULL OR u.userPhoneNo LIKE %:userPhoneNo%) AND "
            + "(:userDOB IS NULL OR u.userDOB = :userDOB)")
    Page<User2DTO> searchUsers(@Param("userName") String userName,
                                @Param("userRealName") String userRealName,
                                @Param("userEmail") String userEmail,
                                @Param("userGender") UserGender userGender,
                                @Param("userRole") UserRole userRole,
                                @Param("userDepartment") UserDepartment userDepartment,
                                @Param("userPhoneNo") String userPhoneNo,
                                @Param("userDOB") LocalDate userDOB,
                                Pageable pageable);
    
    boolean existsByUserName(String userName);
    boolean existsByUserEmail(String userEmail);
    boolean existsByUserPhoneNo(String userPhoneNo);
    Optional<User2DTO> findByUserName(String username);
}
