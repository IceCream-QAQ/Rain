package rain.api.loader

interface ApplicationService {

    fun priority() = 10

    fun init() {}
    fun start() {}
    fun stop() {}

}