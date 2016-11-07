package com.github.albertosh.flitetrakr.model

data class MultipleConnections(val cities: List<String>, val price: Int) : Comparable<MultipleConnections> {
    override fun compareTo(other: MultipleConnections): Int {
        return price.compareTo(other.price)
    }

}
