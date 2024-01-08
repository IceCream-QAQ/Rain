package rain.api.loader

interface ApplicationService {

    fun priority() = 10

    fun start() {}
    fun stop() {}

}