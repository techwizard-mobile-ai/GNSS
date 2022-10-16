package com.lysaan.malikab.addgroceries.others

import android.content.Context
import android.net.Uri

class GoogleMarket : Market {
  
  override fun getMarketURI(context: Context): Uri {
    return Uri.parse(marketLink + context.packageName.toString())
  }
  
  companion object {
    private val marketLink = "market://details?id="
  }
}