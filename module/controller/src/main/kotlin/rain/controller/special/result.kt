package rain.controller.special

import java.lang.RuntimeException

object DoNone
object SkipMe

open class ActionResult(val result: Any) : RuntimeException("ActionResult", null, false, false)
