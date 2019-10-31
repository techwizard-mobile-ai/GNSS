package com.lysaan.malikab.addgroceries.others


import android.content.Context
import android.net.Uri

interface Market {
    fun getMarketURI(context: Context): Uri
}