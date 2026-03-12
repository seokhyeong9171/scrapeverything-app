package com.scrapeverything.app.data.model.response

data class CategoryListResponse(
    val categories: List<CategoryItem>,
    val nextCursorId: Long?,
    val hasNext: Boolean
)

data class CategoryItem(
    val categoryId: Long,
    val categoryName: String
)
