package com.gloria.data.model

import com.google.gson.annotations.SerializedName

data class ConteosLogsBulkResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("totalInserted")
    val totalInserted: Int
)

