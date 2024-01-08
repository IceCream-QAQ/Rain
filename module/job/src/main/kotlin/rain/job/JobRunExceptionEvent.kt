package rain.job

import rain.api.event.Event

class JobRunExceptionEvent(
    val job: JobRuntime,
    val error: Throwable
) : Event