package io.devist.chat.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmailVerificationHash(hash: String): Optional<User>

    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    fun updateUserStatus(@Param("id") id: UUID, @Param("status") status: UserStatus): Int
}