package com.trackmybus.theBouncer.features.v1.data.mapper

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.UserEntity
import com.trackmybus.theBouncer.features.v1.data.model.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

object UserEntityMapper : KoinComponent {
    private val dbFactory: DatabaseFactory by inject()

    suspend fun User.toEntity(): UUID {
        require(provider != null) {
            "Cannot create entity with null provider"
        }

        require(createdAt != null) {
            "Cannot create entity with null createdAt"
        }

        require(id == null) {
            "Cannot create entity with non-null id"
        }

        return dbFactory.dbQuery {
            val user =
                UserEntity.new {
                    this.email = this@toEntity.email
                    this.firstName = this@toEntity.firstName
                    this.lastName = this@toEntity.lastName
                    this.hashedPassword = this@toEntity.hashedPassword
                    this.provider = this@toEntity.provider
                    this.role = this@toEntity.role
                    this.createdAt = this@toEntity.createdAt
                }
            return@dbQuery user.userId.value
        }
    }

    suspend fun List<User>.toEntities() {
        dbFactory.dbQuery {
            forEach {
                require(it.provider != null) {
                    "Cannot create entity with null provider"
                }

                require(it.createdAt != null) {
                    "Cannot create entity with null createdAt"
                }

                require(it.id == null) {
                    "Cannot create entity with non-null id"
                }

                UserEntity.new {
                    this.email = it.email
                    this.firstName = it.firstName
                    this.lastName = it.lastName
                    this.hashedPassword = it.hashedPassword
                    this.provider = it.provider
                    this.role = it.role
                    this.createdAt = it.createdAt
                }
            }
        }
    }

    fun UserEntity.toModel() =
        User(
            id = this.userId.value,
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            hashedPassword = this.hashedPassword,
            provider = this.provider,
            role = this.role,
            createdAt = this.createdAt,
        )

    fun List<UserEntity>.toModel() = map { it.toModel() }

    suspend fun UserEntity.updateModel(model: User) {
        require(model.id != null) {
            "Cannot update model with null id"
        }

        require(model.id == this.userId.value) {
            "Cannot update model with different id"
        }

        require(model.createdAt != null) {
            "Cannot create entity with null createdAt"
        }

        require(model.provider != null) {
            "Cannot create entity with null provider"
        }

        dbFactory.dbQuery {
            this.email = model.email
            this.firstName = model.firstName
            this.lastName = model.lastName
            this.hashedPassword = model.hashedPassword
            this.provider = model.provider
            this.role = model.role
            this.createdAt = model.createdAt
        }
    }
}
