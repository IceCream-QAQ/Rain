

fun main(){
    println(sun.net.www.MimeTable::class.java.getResourceAsStream("content-types.properties")?.reader()?.readText())
}