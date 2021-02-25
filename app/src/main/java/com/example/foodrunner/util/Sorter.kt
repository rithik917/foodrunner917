package com.example.foodrunner.util

import com.example.foodrunner.model.restaurants


class Sorter {
    companion object {
        var costComparator = Comparator<restaurants> { res1, res2 ->
            val costOne = res1.cost_for_one
            val costTwo = res2.cost_for_one
            if (costOne.compareTo(costTwo) == 0) {
                ratingComparator.compare(res1, res2)
            } else {
                costOne.compareTo(costTwo)
            }
        }

            var ratingComparator = Comparator< restaurants> { res1, res2 ->
                val ratingOne = res1.rating
                val ratingTwo = res2.rating
                if (ratingOne.compareTo(ratingTwo) == 0) {
                    val costOne = res1.cost_for_one
                    val costTwo = res2.cost_for_one
                    costOne.compareTo(costTwo)
                } else {
                    ratingOne.compareTo(ratingTwo)
                }
            }
        }

    }