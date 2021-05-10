package jsonresponce

import com.google.gson.annotations.SerializedName

data class Cities(

	@field:SerializedName("city")
	val city: List<CityItem?>? = null
)

data class CityItem(

	@field:SerializedName("region_id")
	val regionId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("country_id")
	val countryId: String? = null,

	@field:SerializedName("city_id")
	val cityId: String? = null
)
