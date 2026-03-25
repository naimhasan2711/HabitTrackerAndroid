package com.nakibul.hassan.habittracker.domain.repository

import com.nakibul.hassan.habittracker.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: String): Category?
    suspend fun insertCategory(category: Category)
    suspend fun insertCategories(categories: List<Category>)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(categoryId: String)
}

