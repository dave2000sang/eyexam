package com.example.eyexam

class TextIt {

    private var list = ArrayList<Pair<String,Float>>()

    public fun insert(el: String) {
        if(el != null) list.add(Pair(el,0f))
    }

    public fun at(i: Int): String {
        if(i < list.size) return list[i].first
        return ""
    }

    public fun store(i: Int, f: Float, fs: Float) {
        if(i < list.size) {
            val weighted = (fs * f) /12
            println("dist weighted: " + weighted)
            val tmp = Pair(list[i].first, weighted)
            list[i] = tmp
        }
    }

    // TODO gradually decrease the font and accommodate by weighing the scores
    public fun average(): Float {
        var sum = 0f
        for(pair in list) {
            sum += pair.second
        }
        return sum / list.size
    }

    public fun size(): Int{
        return list.size
    }


}