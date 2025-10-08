package controller

import rain.controller.dss.PathActionContext


class TestActionContext(val channel: String, override val path: Array<String>) : PathActionContext()