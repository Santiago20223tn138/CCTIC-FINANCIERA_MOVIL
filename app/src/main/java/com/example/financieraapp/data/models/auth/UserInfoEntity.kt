package com.example.financieraapp.data.models.auth

data class UserInfoEntity(
    val message: String,
    val error: Boolean,
    val data: DataEntity
)

data class DataEntity(
    val userInfo: UserInfoDetailsEntity,
    val user: UserEntity,
    val token: String
)

data class UserInfoDetailsEntity(
    val id: Int,
    val username: String,
    val person: PersonEntity,
    val status: Int,
    val authorities: List<AuthorityEntity>
)

data class PersonEntity(
    val id: Int,
    val name: String,
    val lastName: String,
    val address: String?,
    val phone: String,
    val email: String,
    val edad: Int
)

data class AuthorityEntity(
    val id: Int,
    val acronym: String,
    val description: String
)

data class UserEntity(
    val username: String,
    val password: String,
    val authorities: List<AuthorityEntity>,
    val enabled: Boolean,
    val accountNonExpired: Boolean,
    val credentialsNonExpired: Boolean,
    val accountNonLocked: Boolean
)
