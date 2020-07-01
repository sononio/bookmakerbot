package com.sononio.bookmaker.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

@NoRepositoryBean
@Transactional
interface KCrudRepo<T, ID : Serializable> : CrudRepository<T, ID> {
    @Transactional
    fun findByUid(id: ID): T?
}