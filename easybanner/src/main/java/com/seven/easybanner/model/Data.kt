package com.seven.easybanner.model

open class Data(val txt: String = "", val source: DataSource = DataSource.Net, val url: String = "", val id: Int = -1) {

}

enum class DataSource {
    Net,
    Disk,
    Resource
}