package rain.api.loader

interface Loader : Comparable<Loader> {

    fun priority(): Int = 10

    fun load(items: Collection<LoadItem>)

    override fun compareTo(other: Loader): Int = this.priority() - other.priority()

}