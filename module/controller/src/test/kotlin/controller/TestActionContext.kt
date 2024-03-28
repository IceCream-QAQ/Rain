package controller

import rain.controller.dss.PathActionContext


class TestActionContext(val channel: String, path: Array<String>) : PathActionContext(path)