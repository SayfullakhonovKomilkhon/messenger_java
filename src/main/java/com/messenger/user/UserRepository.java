package com.messenger.user;

import com.messenger.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhone(String phone);

    Optional<User> findByUsername(String username);

    Optional<User> findByPublicId(String publicId);

    boolean existsByPhone(String phone);

    boolean existsByPublicId(String publicId);

    @Query("SELECT u FROM User u WHERE " +
            "(UPPER(u.publicId) = UPPER(:query) " +
            "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND u.id <> :excludeId")
    List<User> searchByPublicIdOrName(@Param("query") String query, @Param("excludeId") UUID excludeId);
}
