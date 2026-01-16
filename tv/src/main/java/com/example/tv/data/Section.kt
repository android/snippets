package com.example.tv.data

data class Section(val title: String, val movieList: List<Movie>)
data class Movie(val title: String, val description: String, val thumbnailUrl: String, val backgroundImageUrl: String)
