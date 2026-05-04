package com.example.shintech

object DummyData {
    fun getPhoneList(): List<Phone> {
        // res/drawable 폴더에 있는 실제 파일 이름과 일치하도록 설정합니다.
        return listOf(
            Phone(id = 1, name = "Galaxy S22", brand = "Samsung", price = 1099000, imageResId = R.drawable.galaxy_s22),
            Phone(id = 2, name = "Galaxy S24", brand = "Samsung", price = 1298000, imageResId = R.drawable.galaxy_s24),
            Phone(id = 3, name = "Galaxy S25", brand = "Samsung", price = 1496000, imageResId = R.drawable.galaxy_s25),
            Phone(id = 4, name = "iPhone 15", brand = "Apple", price = 1250000, imageResId = R.drawable.iphone_15),
            Phone(id = 5, name = "iPhone 16", brand = "Apple", price = 1450000, imageResId = R.drawable.iphone_16),
            Phone(id = 6, name = "iPhone 16 Pro", brand = "Apple", price = 1650000, imageResId = R.drawable.iphone_16_pro)
        )
    }
}
