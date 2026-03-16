package rain.job.next

import rain.job.NextTime

class EveryNext(private val round: Long) : NextTime {
    override fun invoke(invokeTime: Long, endTime: Long): Long =
        if (invokeTime == -1L || endTime == -1L) round
        else (endTime - invokeTime).let { round - (it % round) }

}