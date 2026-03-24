package com.abdur.rahman.habittracker.data.repository

import com.abdur.rahman.habittracker.data.local.dao.CategoryDao
import com.abdur.rahman.habittracker.domain.model.Category
import com.abdur.rahman.habittracker.domain.repository.CategoryRepository
import com.abdur.rahman.habittracker.mapper.EntityMapper.toDomain
import com.abdur.rahman.habittracker.mapper.EntityMapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    
    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { categories ->
            categories.map { it.toDomain() }
        }
    }
    
    override suspend fun getCategoryById(id: String): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }
    
    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }
    
    override suspend fun insertCategories(categories: List<Category>) {
        categoryDao.insertCategories(categories.map { it.toEntity() })
    }
    
    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }
    
    override suspend fun deleteCategory(categoryId: String) {
        categoryDao.deleteCategoryById(categoryId)
    }
}
