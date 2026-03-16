package rain.job.next

import rain.job.NextTime

class AtTimeNext(private val first: Long, private val round: Long) : NextTime {

    private val roundNext = EveryNext(round)

    override fun invoke(invokeTime: Long, endTime: Long): Long =
        if (invokeTime == -1L || endTime == -1L) first
        else roundNext(invokeTime, endTime)
}