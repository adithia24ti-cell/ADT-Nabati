package com.example.adt_nabati.data.model

// Bentuk JSON dari Open Food Facts: {"count": 31, "products": [ ... ]}
data class ProdukDuniaResponse(
    val count: Int,
    val products: List<ProdukDuniaModel>
)

// Satu produk, data komunitas sehingga beberapa kolom bisa kosong (null)
data class ProdukDuniaModel(
    val code: String?,
    val product_name: String?,
    val brands: String?,
    val image_front_small_url: String?,
    val nutriscore_grade: String?,
    val nutriments: NutrimenModel?
)

// Nilai gizi per 100 gram
data class NutrimenModel(
    val sugars_100g: Double?,
    val fat_100g: Double?,
    val salt_100g: Double?
)
